package de.uniulm.ki.panda3.symbolic.parser.hddl;

import de.uniulm.ki.panda3.symbolic.csp.Equal;
import de.uniulm.ki.panda3.symbolic.csp.NotEqual;
import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint;
import de.uniulm.ki.panda3.symbolic.domain.*;
import de.uniulm.ki.panda3.symbolic.logic.*;
import de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel.internalSortsAndConsts;
import de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel.internalTaskNetwork;
import de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel.parserUtil;
import de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel.seqProviderList;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.SymbolicPlan;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.panda3.util.JavaToScala;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import scala.*;
import scala.collection.Seq;
import scala.collection.immutable.Vector;
import scala.collection.immutable.VectorBuilder;
import scala.runtime.AbstractFunction1;

import java.util.*;

/**
 * Created by dhoeller on 14.04.15.
 * - @dh: schau dir mal die variablen und zugehörigen constraints an
 */
public class hddlPanda3Visitor {
    public static Option<DecompositionMethod> noneForMethod = (Option<DecompositionMethod>) (Object) scala.None$.MODULE$;
    public static Option<PlanStep> noneForPlanStep =(Option<PlanStep>) (Object)  scala.None$.MODULE$;


    public Tuple2<Domain, Plan> visitInstance(@NotNull hddlParser.DomainContext ctxDomain, @NotNull hddlParser.ProblemContext ctxProblem) {

        Seq<Sort> sorts = visitTypeAndObjDef(ctxDomain, ctxProblem);
        Seq<Predicate> predicates = visitPredicateDeclaration(sorts, ctxDomain.predicates_def());

        Task init = visitInitialState(sorts, predicates, ctxProblem.p_init());
        Task goal = visitGoalState(sorts, predicates, ctxProblem.p_goal());
        Seq<Task> tasks = visitTaskDefs(sorts, predicates, ctxDomain);

        Seq<DecompositionMethod> decompositionMethods = visitMethodDef(ctxDomain.method_def(), sorts, predicates, tasks);
        Seq<DecompositionAxiom> decompositionAxioms = new Vector<>(0, 0, 0);

        Domain d = new Domain(sorts, predicates, tasks, decompositionMethods, decompositionAxioms);

        Seq<Variable> initArguments = init.parameters();
        PlanStep psInit = new PlanStep(0, init, initArguments, noneForMethod, noneForPlanStep);

        Seq<Variable> goalArguments = goal.parameters();
        PlanStep psGoal = new PlanStep(1, goal, goalArguments, noneForMethod, noneForPlanStep);

        // initial plan
        internalTaskNetwork tn = new internalTaskNetwork();
        tn.addPlanStep(psInit);
        tn.addPlanStep(psGoal);
        tn.addOrdering(psInit, psGoal);
        tn.addCspVariables(init.parameters());
        assert (init.parameters().equals(goal.parameters()));
        tn.addCspConstraints(init.parameterConstraints());
        //tn.addCspConstraints(goal.parameterConstraints());

        visitInitialTN(ctxProblem.p_htn(), tn, tasks, sorts);

        Plan p = new SymbolicPlan(tn.planSteps(), tn.causalLinks(), tn.taskOrderings(), tn.csp(), psInit, psGoal);

        Tuple2<Domain, Plan> initialProblem = new Tuple2<>(d, p);
        return initialProblem;
    }

    private void visitInitialTN(hddlParser.P_htnContext p_htnContext, internalTaskNetwork tn, Seq<Task> tasks, Seq<Sort> sorts) {
        hddlParser.Subtask_defsContext subtask = p_htnContext.tasknetwork_def().subtask_defs();
        int nextId = 0;
        int psID = 2;

        for (hddlParser.Subtask_defContext oneST : subtask.subtask_def()) {
            Task schema = parserUtil.taskByName(oneST.task_symbol().getText(), tasks);
            VectorBuilder<Variable> parameters = new VectorBuilder<>();

            for (final hddlParser.Var_or_constContext constant : oneST.var_or_const()) {
                assert (constant.VAR_NAME() == null); // must not be a variable
                assert (constant.NAME() != null); // must actually be a constant

                Variable v = tn.csp().constraints().find(new AbstractFunction1<VariableConstraint, Object>() {
                    @Override
                    public Object apply(VariableConstraint v1) {
                        if (v1 instanceof Equal) {
                            Equal eq = (Equal) v1;
                            if (eq.right() instanceof Constant) {
                                Constant c = (Constant) eq.right();
                                if (c.name().equals(constant.getText()))
                                    return java.lang.Boolean.TRUE;
                            }
                        }
                        return java.lang.Boolean.FALSE;
                    }
                }).get().getVariables().head();

                parameters.$plus$eq(v);
            }

            PlanStep psNew = new PlanStep(psID++, schema, parameters.result(), noneForMethod,noneForPlanStep);
            tn.addPlanStep(psNew);
            for (int i = 0; i < tn.planSteps().size(); i++) {
                PlanStep ps = tn.planSteps().apply(i);
                if (ps.id() == 0) {
                    tn.addOrdering(ps, psNew);
                } else if (ps.id() == 1) {
                    tn.addOrdering(psNew, ps);
                }
            }
        }

        // TODO: ordering!!
        return;
    }

    private Task visitGoalState(Seq<Sort> sorts, Seq<Predicate> predicates, hddlParser.P_goalContext ctx) {
        seqProviderList<VariableConstraint> parameterConstraints = new seqProviderList<VariableConstraint>();
        seqProviderList<Variable> taskParameters = getVariableForEveryConst(sorts, parameterConstraints);
        VectorBuilder<Literal> goalCondition = new VectorBuilder<>();
        if (ctx != null) {
            visitGoalConditions(goalCondition, predicates, taskParameters, sorts, null, ctx.gd()); // it is not possible to have equality/inequality constraints in the goal state ->null
        }
        return new ReducedTask("goal", true, taskParameters.result(), parameterConstraints.result(), new And<Literal>(goalCondition.result()), new And<Literal>(new Vector<Literal>(0, 0, 0)));
    }

    private Task visitInitialState(Seq<Sort> sorts, Seq<Predicate> predicates, hddlParser.P_initContext ctx) {
        seqProviderList<VariableConstraint> varConstraints = new seqProviderList<>();
        seqProviderList<Variable> taskParameter = getVariableForEveryConst(sorts, varConstraints);

        VectorBuilder<Literal> initEffects = new VectorBuilder<>();
        for (hddlParser.LiteralContext lc : ctx.literal()) {
            if (lc.atomic_formula() != null) {
                visitAtomFormula(initEffects, taskParameter, predicates, sorts, varConstraints, true, lc.atomic_formula());
            } else if (lc.neg_atomic_formula() != null) {
                visitAtomFormula(initEffects, taskParameter, predicates, sorts, varConstraints, false, lc.atomic_formula());
            }
        }
        return new ReducedTask("init", true, taskParameter.result(), varConstraints.result(), new And<Literal>(new Vector<Literal>(0, 0, 0)), new And<Literal>(initEffects.result()));
    }

    private seqProviderList<Variable> getVariableForEveryConst(Seq<Sort> sorts, seqProviderList<VariableConstraint> varConstraints) {
        seqProviderList<Variable> taskParameter = new seqProviderList<>();
        int cspId = 0;
        for (int i = 0; i < sorts.length(); i++) {
            Sort s = sorts.apply(i);
            for (int j = 0; j < s.elements().length(); j++) {
                Constant c = s.elements().apply(j);
                Variable v = new Variable(cspId++, "varFor" + c.name(), s);

                taskParameter.add(v);
                Equal eq = new Equal(v, c);
                varConstraints.add(eq);
            }
        }
        return taskParameter;
    }

    private Seq<DecompositionMethod> visitMethodDef(List<hddlParser.Method_defContext> ctx, Seq<Sort> sorts, Seq<Predicate> predicates, Seq<Task> tasks) {
        VectorBuilder<DecompositionMethod> methods = new VectorBuilder<>();
        for (hddlParser.Method_defContext m : ctx) {
            // Read abstract task
            String taskname = m.task_symbol().NAME().toString();
            Task abstractTask = parserUtil.taskByName(taskname, tasks);

            if (abstractTask == null) {
                System.out.println("ERROR: compound task given in method definition is undefined: " + taskname);
                continue;
            } else if (abstractTask.isPrimitive()) {
                System.out.println("ERROR: compound task given in method definition is not compound, but a primitive task: " + taskname);
                continue;
            }

            // Read method's name and parameters
            String nameStr = m.method_symbol().NAME().toString();
            seqProviderList<Variable> methodParams = typedParamsToVars(sorts, abstractTask.parameters().size(), m.typed_var_list().typed_vars());

            internalTaskNetwork subNetwork = new internalTaskNetwork();
            subNetwork.addCspVariables(methodParams);
            subNetwork.addCspVariables(abstractTask.parameters());

            // Read task's parameters and connect them to method's parameters
            boolean paramsOk = subNetwork.connectMethVarsAndTaskVars(methodParams.result(), abstractTask, m.var_or_const());
            if (!paramsOk) {
                continue;
            }

            // Read method preconditions
            VectorBuilder<Literal> preconditions = new VectorBuilder<>();
            Formula f2 = null;
            boolean hasPrecondition;
            if (m.gd() != null) {
                if (m.gd().getText().contains("(forall")) {
                    // todo: HACK
                    seqProviderList<VariableConstraint> constraints = new seqProviderList<>();
                    VectorBuilder<Literal> preconditions2 = new VectorBuilder<>();
                    visitGoalConditions(preconditions2, predicates, methodParams, sorts, constraints, m.gd().gd_conjuction().gd(1));
                    subNetwork.addCspConstraints(constraints.result());

                    hddlParser.Gd_univeralContext eq = m.gd().gd_conjuction().gd(0).gd_univeral();
                    Tuple2<Formula, Variable> f = visitUniveral(methodParams.result(), predicates, sorts, constraints, true, eq);
                    // don't add the variable as it is only locally valid
                    //subNetwork.addCspVariable(f._2());
                    VectorBuilder<Object> form = new VectorBuilder<>();
                    form.$plus$eq(preconditions2.result().apply(0));
                    form.$plus$eq(f._1());
                    f2 = new And(form.result());
                } else {
                    seqProviderList<VariableConstraint> constraints = new seqProviderList<>();
                    visitGoalConditions(preconditions, predicates, methodParams, sorts, constraints, m.gd());
                    subNetwork.addCspConstraints(constraints.result());
                }
                hasPrecondition = true;
            } else {
                hasPrecondition = false;
            }

            // Create subplan, method and add it to method list
            Plan subPlan = subNetwork.readTaskNetwork(m.tasknetwork_def(), methodParams.result(), abstractTask, tasks, sorts);

            DecompositionMethod method;
            if (f2 != null) {
                method = new SHOPDecompositionMethod(abstractTask, subPlan, f2);
            } else if (hasPrecondition) {
                method = new SHOPDecompositionMethod(abstractTask, subPlan, new And<Literal>(preconditions.result()));
            } else {
                method = new SimpleDecompositionMethod(abstractTask, subPlan);
            }
            methods.$plus$eq(method);
        }
        return methods.result();
    }

    private Seq<Task> visitTaskDefs(Seq<Sort> sorts, Seq<Predicate> predicates, hddlParser.DomainContext ctxDomain) {
        VectorBuilder<Task> tasks = new VectorBuilder<>();
        for (hddlParser.Action_defContext a : ctxDomain.action_def()) {
            Task t = visitTaskDef(sorts, predicates, a.task_def(), true);
            tasks.$plus$eq(t);
        }
        for (hddlParser.Comp_task_defContext c : ctxDomain.comp_task_def()) {
            Task t = visitTaskDef(sorts, predicates, c.task_def(), false);
            tasks.$plus$eq(t);
        }
        return tasks.result();
    }

    private Task visitTaskDef(Seq<Sort> sorts, Seq<Predicate> predicates, hddlParser.Task_defContext ctxTask, boolean isPrimitive) {
        String taskName = ctxTask.task_symbol().NAME().toString();
        seqProviderList<Variable> parameters = typedParamsToVars(sorts, 0, ctxTask.typed_var_list().typed_vars());
        seqProviderList<VariableConstraint> constraints = new seqProviderList<>();

        // build preconditions
        // todo: implement fancy precondition stuff
        VectorBuilder<Literal> preconditions = new VectorBuilder<>();
        if (ctxTask.gd() != null) {
            visitGoalConditions(preconditions, predicates, parameters, sorts, constraints, ctxTask.gd());
        }

        // build effects
        // todo: implement forall- and conditional effects
        VectorBuilder<Literal> effects = new VectorBuilder<>();

        if ((ctxTask.effect_body() != null) && (ctxTask.effect_body().c_effect() != null)) {
            visitConEff(effects, parameters, sorts, predicates, ctxTask.effect_body().c_effect());
        } else if ((ctxTask.effect_body() != null) && (ctxTask.effect_body().eff_conjuntion() != null)) {
            visitConEffConj(effects, parameters, sorts, predicates, ctxTask.effect_body().eff_conjuntion());
        }
        return new ReducedTask(taskName, isPrimitive, parameters.result(), constraints.result(), new And<Literal>(preconditions.result()), new And<Literal>(effects.result()));
    }

    private seqProviderList<Variable> typedParamsToVars(Seq<Sort> sorts, int startId, List<hddlParser.Typed_varsContext> vars) {
        VectorBuilder<Sort> bVarSorts = new VectorBuilder<>();
        VectorBuilder<String> bVarNames = new VectorBuilder<>();
        visitTypedList(bVarSorts, bVarNames, sorts, vars);

        // build parameters
        Vector<String> varNames = bVarNames.result();
        Vector<Sort> varSorts = bVarSorts.result();
        seqProviderList<Variable> bParameters = new seqProviderList<>();

        for (int i = 0; i < varNames.length(); i++) {
            bParameters.add(new Variable(startId + i, varNames.apply(i), varSorts.apply(i)));
        }
        return bParameters;
    }

    private void visitGoalConditions(VectorBuilder<Literal> outGoalDefinitions, Seq<Predicate> predicates, seqProviderList<Variable> parameters, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, hddlParser.GdContext ctx) {
        if (ctx.atomic_formula() != null) { // a single precondition
            visitAtomFormula(outGoalDefinitions, parameters, predicates, sorts, constraints, true, ctx.atomic_formula());
        } else if (ctx.gd_negation() != null) { // a negated single precondition
            visitNegAtomFormula(outGoalDefinitions, parameters, predicates, sorts, constraints, ctx.gd_negation());
        } else if (ctx.gd_conjuction() != null) { // a conduction of preconditions
            visitLiteralConj(outGoalDefinitions, parameters, predicates, sorts, constraints, ctx.gd_conjuction());
        } else if (ctx.gd_univeral() != null) {
            // todo: universal quantifier
        } else if (ctx.gd_disjuction() != null) { // well ...
            System.out.println("ERROR: not yet implemented - disjunction in preconditions.");
        }
    }

    private void visitConEffConj(VectorBuilder<Literal> outEffects, seqProviderList<Variable> parameters, Seq<Sort> sorts, Seq<Predicate> predicates, hddlParser.Eff_conjuntionContext ctx) {
        for (hddlParser.C_effectContext eff : ctx.c_effect()) {
            visitConEff(outEffects, parameters, sorts, predicates, eff);
        }
    }

    private void visitConEff(VectorBuilder<Literal> outEffects, seqProviderList<Variable> parameters, Seq<Sort> sorts, Seq<Predicate> predicates, hddlParser.C_effectContext ctx) {
        if (ctx.literal() != null) {
            if (ctx.literal().atomic_formula() != null) {
                visitAtomFormula(outEffects, parameters, predicates, sorts, null, true, ctx.literal().atomic_formula());
            } else if (ctx.literal().neg_atomic_formula() != null) {
                visitAtomFormula(outEffects, parameters, predicates, sorts, null, false, ctx.literal().neg_atomic_formula().atomic_formula());
            }
        } else if (ctx.forall_effect() != null) {
            System.out.println("ERROR: not yet implemented - forall effects.");
        } else if (ctx.conditional_effect() != null) {
            System.out.println("ERROR: not yet implemented - conditional effects.");
        } else {
            System.out.println("ERROR: found an empty effect in action declaration.");
        }
    }

    private void visitLiteralConj(VectorBuilder<Literal> outLiterals, seqProviderList<Variable> parameters, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, hddlParser.Gd_conjuctionContext ctx) {
        for (hddlParser.GdContext gd : ctx.gd()) {
            if (gd.atomic_formula() != null) {
                visitAtomFormula(outLiterals, parameters, predicates, sorts, constraints, true, gd.atomic_formula());
            } else if (gd.gd_negation() != null) {
                visitNegAtomFormula(outLiterals, parameters, predicates, sorts, constraints, gd.gd_negation());
            } else if (gd.gd_univeral() != null) {
                System.out.println("ERROR: not yet implemented - an element of a conjunction of preconditions or effects seems to be no literal.");
                //visitUniveral(outLiterals, parameters, predicates, sorts, constraints, true, gd.gd_univeral());
            } else {
                System.out.println("ERROR: not yet implemented - an element of a conjunction of preconditions or effects seems to be no literal.");
            }
        }
    }

    private Tuple2<Formula, Variable> visitUniveral(Seq<Variable> parameters, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, boolean b, hddlParser.Gd_univeralContext gd_univeralContext) {
        int curIndex = parameters.size();
        seqProviderList<Variable> parameters2 = new seqProviderList<>();
        for (int i = 0; i < parameters.size(); i++) {
            parameters2.add(parameters.apply(i));
        }

        List<hddlParser.Typed_varsContext> y = gd_univeralContext.typed_var_list().typed_vars();
        if (y.size() != 1) {
            System.out.println("ERROR: Not implemented: conditional effects");
        }
        String type = y.get(0).children.get(2).getText();
        String obj = y.get(0).children.get(0).getText();

        Sort s = null;
        for (int i = 0; i < sorts.size(); i++) {
            Sort s1 = sorts.apply(i);
            if (s1.name().equals(type)) {
                s = s1;
                break;
            }
        }

        Variable v = new Variable(curIndex++, obj, s);
        parameters2.add(v);

        VectorBuilder<Literal> innerListerals = new VectorBuilder<>();
        visitGoalConditions(innerListerals, predicates, parameters2, sorts, constraints, gd_univeralContext.gd());

        And a = new And(innerListerals.result());
        Formula f = new Forall(v, a);
        return new Tuple2<>(f, v);
    }

    private void visitNegAtomFormula(VectorBuilder<Literal> outLiterals, seqProviderList<Variable> parameters, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, hddlParser.Gd_negationContext ctx) {
        if (ctx.gd().atomic_formula() != null) {
            visitAtomFormula(outLiterals, parameters, predicates, sorts, constraints, false, ctx.gd().atomic_formula());
        } else if (ctx.gd().gd_equality_constraint() != null) {
            Variable var1 = getVariable(ctx.gd().gd_equality_constraint().var_or_const(0), parameters, constraints, sorts);
            Variable var2 = getVariable(ctx.gd().gd_equality_constraint().var_or_const(1), parameters, constraints, sorts);
            NotEqual ne = new NotEqual(var1, var2);
            constraints.add(ne);
        } else {
            System.out.println("ERROR: not yet implemented - a negated literal seems to be no atomic formula.");
        }
    }

    private void visitAtomFormula(VectorBuilder<Literal> outLiterals, seqProviderList<Variable> taskParameters, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, boolean isPositive, hddlParser.Atomic_formulaContext ctx) {
        //
        // get predicate definition
        //
        String predName = ctx.predicate().NAME().toString();
        Predicate predicate = null;
        for (int i = 0; i < predicates.length(); i++) {
            Predicate predDef = predicates.apply(i);
            if (predDef.name().equals(predName)) {
                predicate = predDef;
                break;
            }
        }
        if (predicate == null) {
            System.out.println("ERROR: a precondition or effect in action definition refers to predicate \"" + predName + "\" that can't be found in predicate declaration.");
        }

        //
        // get variable definition
        //
        seqProviderList<Variable> parameterVariables = new seqProviderList<>();
        List<hddlParser.Var_or_constContext> params = ctx.var_or_const();

        for (hddlParser.Var_or_constContext param : params) {
            Variable var = getVariable(param, taskParameters, constraints, sorts);
            parameterVariables.add(var);
        }

        outLiterals.$plus$eq(new Literal(predicate, isPositive, parameterVariables.result()));
    }

    private Variable getVariable(hddlParser.Var_or_constContext param, seqProviderList<Variable> parameters, seqProviderList<VariableConstraint> constraints, Seq<Sort> sorts) {
        Variable var = null;
        if (param.VAR_NAME() != null) {
            // this is a variable
            String pname = param.VAR_NAME().getText();
            for (int i = 0; i < parameters.size(); i++) {
                Variable v = parameters.get(i);
                if (v.name().equals(pname)) {
                    var = v;
                    break;
                }
            }
        } else {
            // this may be (1) an object that has already been defined in s0 or (2) a const in method or action definition
            String pname = param.NAME().getText();

            // (1) const in s0, there is already a var
            for (int i = 0; i < constraints.size(); i++) {
                VariableConstraint vc = constraints.get(i);
                if (vc instanceof Equal) {
                    Equal e = (Equal) vc;
                    if (((Constant) e.right()).name().equals(pname)) {
                        var = e.left();
                    }
                }
            }

            if (var == null) {
                // (2) constants in method or action definition, need to introduce a new var, bind it to const, and
                // add it to the parameter-list
                loopGetConst:
                for (int i = 0; i < sorts.size(); i++) {
                    Sort s = sorts.apply(i);
                    for (int j = 0; j < s.elements().size(); j++) {
                        Constant c = s.elements().apply(j);
                        if (c.name().equals(pname)) {
                            var = new Variable(parameters.size() + 1, "varToConst" + c, s);
                            parameters.add(var);
                            constraints.add(new Equal(var, c));
                            break loopGetConst;
                        }
                    }
                }
            }
        }
        if (var == null) {
            System.out.println("ERROR: The variable name \"" + param.VAR_NAME() + "\" is used in a precondition or effect definition, but is not defined in the actions parameter definition.");
            System.out.println("Maybe it is a constant, then it is not your fault, but just a not yet implemented feature, but anyway...");
        }
        return var;
    }


    private Seq<Predicate> visitPredicateDeclaration(Seq<Sort> sorts, hddlParser.Predicates_defContext ctx) {
        VectorBuilder<Predicate> predicates = new VectorBuilder<>();

        List<hddlParser.Atomic_formula_skeletonContext> listDefs = ctx.atomic_formula_skeleton();
        if (listDefs == null) {
            return new Vector<>(0, 0, 0);
        }

        for (hddlParser.Atomic_formula_skeletonContext def : listDefs) {
            String predName = def.predicate().NAME().toString();
            VectorBuilder<Sort> pSorts = new VectorBuilder<>();
            readTypedList(pSorts, new VectorBuilder<String>(), sorts, def.typed_var_list().typed_vars(), "Predicate \"" + predName + "\"");

            predicates.$plus$eq(new Predicate(predName, pSorts.result()));
        }
        return predicates.result();
    }

    /*
    The following method(s) ready a typed list and fills the first vector with their names and the second one with the types. The vectors equal in length.
    Please provide a meaningful description of who is reading as last parameter
     */
    private void visitTypedList(VectorBuilder<Sort> lSorts, VectorBuilder<String> lNames, Seq<Sort> sorts, List<hddlParser.Typed_varsContext> ctx) {
        readTypedList(lSorts, lNames, sorts, ctx, "Somebody");
    }

    private void readTypedList(VectorBuilder<Sort> outSorts, VectorBuilder<String> outNames, Seq<Sort> sorts, List<hddlParser.Typed_varsContext> ctx, String ErrorMsgWhoIsReader) {
        for (hddlParser.Typed_varsContext varList : ctx) { // one list contains one or more var of the same type

            String sortName = varList.var_type().NAME().toString();
            Sort s = null;
            for (int i = 0; i < sorts.length(); i++) {
                Sort that = sorts.apply(i);
                if (that.name().equals(sortName)) {
                    s = that;
                    break;
                }
            }
            if (s == null) {
                System.out.println("ERROR: " + ErrorMsgWhoIsReader + " refers to sort \"" + sortName + "\" that can't be found in sort declaration.");
            }

            for (TerminalNode oneName : varList.VAR_NAME()) {
                outNames.$plus$eq(oneName.toString());
                outSorts.$plus$eq(s);
            }
        }
    }

    public Seq<Sort> visitTypeAndObjDef(@NotNull hddlParser.DomainContext ctxDomain, @NotNull hddlParser.ProblemContext ctxProblem) {

        // Extract type hierarchy from domain file
        internalSortsAndConsts internalSortModel = new internalSortsAndConsts(); // do not pass out, use only here

        List<hddlParser.One_defContext> typeDefs = ctxDomain.type_def().one_def(); // the "one_def" tag might appear more than once

        for (hddlParser.One_defContext typeDef : typeDefs) {

            hddlParser.New_typesContext newTypes = typeDef.new_types();

            final String parent_type = typeDef.var_type().NAME().toString();
            for (int j = 0; j < newTypes.getChildCount(); j++) {
                final String child_type = newTypes.NAME(j).toString();
                internalSortModel.addParent(child_type, parent_type);
            }
        }

        // Extract constant symbols from domain and problem file
        if (ctxDomain.const_def() != null) {
            List<hddlParser.Typed_objsContext> domainConsts = ctxDomain.const_def().typed_obj_list().typed_objs();
            addToInternalModel(internalSortModel, domainConsts);
        }
        if (ctxProblem.p_object_declaration() != null) {
            List<hddlParser.Typed_objsContext> problemConsts = ctxProblem.p_object_declaration().typed_obj_list().typed_objs();
            addToInternalModel(internalSortModel, problemConsts);
        }

        internalSortModel.checkConsistency();

        List<Sort> allsorts = new LinkedList<>();
        for (String type : internalSortModel.allTypeNamesInRightOrder()) {
            // The types are returned in an order s.t. a parent type is placed after all its children

            Seq<Constant> elements = internalSortModel.getConsts(type);
            List<String> subSortNames = internalSortModel.getSubSorts(type);
            VectorBuilder<Sort> subSorts = new VectorBuilder<>();

            for (Sort s : allsorts) {
                if (subSortNames.contains(s.name())) {
                    subSorts.$plus$eq(s);
                }
            }
            allsorts.add(new Sort(type, elements, subSorts.result()));
        }

        return JavaToScala.toScalaSeq(allsorts);
    }

    private void addToInternalModel(internalSortsAndConsts internalModel, List<hddlParser.Typed_objsContext> consts) {
        for (hddlParser.Typed_objsContext c : consts) {
            final String type = c.var_type().NAME().toString();
            for (int j = 0; j < c.new_consts().size(); j++) {
                final String constName = c.new_consts().get(j).getText();
                internalModel.addConst(type, constName);
            }
        }
    }
}
