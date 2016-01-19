package de.uniulm.ki.panda3.symbolic.parser.hddl;

import de.uniulm.ki.panda3.symbolic.csp.Equal;
import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint;
import de.uniulm.ki.panda3.symbolic.domain.*;
import de.uniulm.ki.panda3.symbolic.logic.*;
import de.uniulm.ki.panda3.symbolic.parser.hddl.hddlParser;
import de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel.internalSortsAndConsts;
import de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel.internalTaskNetwork;
import de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel.parserUtil;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.SymbolicPlan;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.panda3.util.JavaToScala;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.Vector;
import scala.collection.immutable.VectorBuilder;
import scala.runtime.AbstractFunction1;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dhoeller on 14.04.15.
 * - @dh: schau dir mal die variablen und zugehörigen constraints an
 */
public class hddlPanda3Visitor {
    public Tuple2<Domain, Plan> visitInstance(@NotNull hddlParser.DomainContext ctxDomain, @NotNull hddlParser.ProblemContext ctxProblem) {

        Seq<Sort> sorts = visitTypeAndObjDef(ctxDomain, ctxProblem);
        Seq<Predicate> predicates = visitPredicateDeclaration(sorts, ctxDomain.predicates_def());

        Task init = visitInitialState(sorts, predicates, ctxProblem.p_init());
        Task goal = visitGoalState(sorts, predicates, ctxProblem.p_goal());
        Seq<Task> tasks = visitTaskDefs(sorts, predicates, ctxDomain);

        Seq<DecompositionMethod> decompositionMethods = visitMethodDef(ctxDomain.method_def(), sorts, predicates, tasks);
        Seq<DecompositionAxiom> decompositionAxioms = null;

        Domain d = new Domain(sorts, predicates, tasks, decompositionMethods, decompositionAxioms);

        Seq<Variable> initArguments = init.parameters();
        PlanStep psInit = new PlanStep(0, init, initArguments);

        Seq<Variable> goalArguments = goal.parameters();
        PlanStep psGoal = new PlanStep(1, goal, goalArguments);

        // initial plan
        internalTaskNetwork tn = new internalTaskNetwork();
        tn.addPlanStep(psInit);
        tn.addPlanStep(psGoal);
        tn.addOrdering(psInit, psGoal);
        tn.addCspVariables(init.parameters());
        assert (init.parameters().equals(goal.parameters()));
        tn.addCspConstraints(init.parameterConstraints());
        //tn.addCspConstraints(goal.parameterConstraints());

        Task schema = d.tasks().find(new AbstractFunction1<Task, Object>() {
            @Override
            public Object apply(Task v1) {
                return v1.name().equals("tlt") ? Boolean.TRUE : Boolean.FALSE;
            }
        }).get();

        tn.addPlanStep(new PlanStep(2, schema, new Vector<Variable>(0, 0, 0)));

//tn.readTaskNetwork(ctxProblem.p_htn().tasknetwork_def(),)

        Plan p = new SymbolicPlan(tn.planSteps(), tn.causalLinks(), tn.taskOrderings(), tn.csp(), psInit, psGoal);

        Tuple2<Domain, Plan> initialProblem = new Tuple2<>(d, p);
        return initialProblem;
    }

    private Task visitGoalState(Seq<Sort> sorts, Seq<Predicate> predicates, hddlParser.P_goalContext ctx) {
        VectorBuilder<VariableConstraint> parameterConstraints = new VectorBuilder<VariableConstraint>();
        Seq<Variable> taskParameters = getVariableForEveryConst(sorts, parameterConstraints);
        VectorBuilder<Literal> goalCondition = new VectorBuilder<>();
        if (ctx != null) {
            visitGoalConditions(goalCondition, predicates, taskParameters, ctx.gd());
        }
        return new Task("goal", true, taskParameters, parameterConstraints.result(), goalCondition.result(), new Vector<Literal>(0, 0, 0));
    }

    private Task visitInitialState(Seq<Sort> sorts, Seq<Predicate> predicates, hddlParser.P_initContext ctx) {
        VectorBuilder<VariableConstraint> varConstraints = new VectorBuilder<>();
        Seq<Variable> taskParameter = getVariableForEveryConst(sorts, varConstraints);

        VectorBuilder<Literal> initEffects = new VectorBuilder<>();
        for (hddlParser.LiteralContext lc : ctx.literal()) {
            if (lc.atomic_formula() != null) {
                visitAtomFormula(initEffects, taskParameter, predicates, true, lc.atomic_formula());
            } else if (lc.neg_atomic_formula() != null) {
                visitAtomFormula(initEffects, taskParameter, predicates, false, lc.atomic_formula());
            }
        }
        return new Task("init", true, taskParameter, varConstraints.result(), new Vector<Literal>(0, 0, 0), initEffects.result());
    }

    private Seq<Variable> getVariableForEveryConst(Seq<Sort> sorts, VectorBuilder<VariableConstraint> varConstraints) {
        VectorBuilder<Variable> taskParameter = new VectorBuilder<>();
        int cspId = 0;
        for (int i = 0; i < sorts.length(); i++) {
            Sort s = sorts.apply(i);
            for (int j = 0; j < s.elements().length(); j++) {
                Constant c = s.elements().apply(j);
                Variable v = new Variable(cspId++, c.name(), s);

                taskParameter.$plus$eq(v);
                Equal eq = new Equal(v, c);
                varConstraints.$plus$eq(eq);
            }
        }
        return taskParameter.result();
    }

    private Seq<DecompositionMethod> visitMethodDef(List<hddlParser.Method_defContext> ctx, Seq<Sort> sorts, Seq<Predicate> predicates, Seq<Task> tasks) {
        VectorBuilder<DecompositionMethod> methods = new VectorBuilder<>();
        for (hddlParser.Method_defContext m : ctx) {
            // Read method's name and parameters
            String nameStr = m.method_symbol().NAME().toString();
            Seq<Variable> methodParams = typedParamsToVars(sorts, m.typed_var_list().typed_vars());
            internalTaskNetwork subNetwork = new internalTaskNetwork();
            subNetwork.addCspVariables(methodParams);

            // Read method preconditions
            VectorBuilder<Literal> preconditions = new VectorBuilder<>();
            boolean hasPrecondition;
            if (m.gd() != null) {
                visitGoalConditions(preconditions, predicates, methodParams, m.gd());
                hasPrecondition = true;
            } else hasPrecondition = false;

            // Read abstract task
            String taskname = m.task_symbol().NAME().toString();
            Task abstractTask = parserUtil.taskByName(taskname, tasks);
            subNetwork.addCspVariables(abstractTask.parameters());
            if (abstractTask == null) {
                System.out.println("ERROR: compound task given in method definition is undefined: " + taskname);
                continue;
            } else if (abstractTask.isPrimitive()) {
                System.out.println("ERROR: compound task given in method definition is not compound, but a primitive task: " + taskname);
                continue;
            }

            // Read task's parameters and connect them to method's parameters
            boolean paramsOk = subNetwork.connectMethVarsAndTaskVars(methodParams, abstractTask, m.var_or_const());
            if (!paramsOk) {
                continue;
            }

            // Create subplan, method and add it to method list
            Plan subPlan = subNetwork.readTaskNetwork(m.tasknetwork_def(), methodParams, abstractTask, tasks, sorts);

            DecompositionMethod method;
            if (hasPrecondition) {
                method = new SHOPDecompositionMethod(abstractTask, subPlan, preconditions.result());
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
        Seq<Variable> parameters = typedParamsToVars(sorts, ctxTask.typed_var_list().typed_vars());
        Seq<VariableConstraint> parameterConstraints = new Vector<>(0, 0, 0);

        // build preconditions
        // todo: implement fancy precondition stuff
        VectorBuilder<Literal> preconditions = new VectorBuilder<>();
        if (ctxTask.gd() != null) {
            visitGoalConditions(preconditions, predicates, parameters, ctxTask.gd());
        }

        // build effects
        // todo: implement forall- and conditional effects
        VectorBuilder<Literal> effects = new VectorBuilder<>();

        if ((ctxTask.effect_body() != null) && (ctxTask.effect_body().c_effect() != null)) {
            visitConEff(effects, parameters, predicates, ctxTask.effect_body().c_effect());
        } else if ((ctxTask.effect_body() != null) && (ctxTask.effect_body().eff_conjuntion() != null)) {
            visitConEffConj(effects, parameters, predicates, ctxTask.effect_body().eff_conjuntion());
        }
        return new Task(taskName, isPrimitive, parameters, parameterConstraints, preconditions.result(), effects.result());
    }

    private Seq<Variable> typedParamsToVars(Seq<Sort> sorts, List<hddlParser.Typed_varsContext> vars) {
        VectorBuilder<Sort> bVarSorts = new VectorBuilder<>();
        VectorBuilder<String> bVarNames = new VectorBuilder<>();
        visitTypedList(bVarSorts, bVarNames, sorts, vars);

        // build parameters
        Vector<String> varNames = bVarNames.result();
        Vector<Sort> varSorts = bVarSorts.result();
        VectorBuilder<Variable> bParameters = new VectorBuilder<>();

        for (int i = 0; i < varNames.length(); i++) {
            bParameters.$plus$eq(new Variable(i, varNames.apply(i), varSorts.apply(i)));
        }
        return bParameters.result();
    }

    private void visitGoalConditions(VectorBuilder<Literal> outGoalDefinitions, Seq<Predicate> predicates, Seq<Variable> parameters, hddlParser.GdContext ctx) {
        if (ctx.atomic_formula() != null) { // a single precondition
            visitAtomFormula(outGoalDefinitions, parameters, predicates, true, ctx.atomic_formula());
        } else if (ctx.gd_negation() != null) { // a negated single precondition
            visitNegAtomFormula(outGoalDefinitions, parameters, predicates, ctx.gd_negation());
        } else if (ctx.gd_conjuction() != null) { // a conduction of preconditions
            visitLiteralConj(outGoalDefinitions, parameters, predicates, ctx.gd_conjuction());
        } else if (ctx.gd_univeral() != null) {
            // todo: universal quantifier
        } else if (ctx.gd_disjuction() != null) { // well ...
            System.out.println("ERROR: not yet implemented - disjunction in preconditions.");
        }
    }

    private void visitConEffConj(VectorBuilder<Literal> outEffects, Seq<Variable> parameters, Seq<Predicate> predicates, hddlParser.Eff_conjuntionContext ctx) {
        for (hddlParser.C_effectContext eff : ctx.c_effect()) {
            visitConEff(outEffects, parameters, predicates, eff);
        }
    }

    private void visitConEff(VectorBuilder<Literal> outEffects, Seq<Variable> parameters, Seq<Predicate> predicates, hddlParser.C_effectContext ctx) {
        if (ctx.literal() != null) {
            if (ctx.literal().atomic_formula() != null) {
                visitAtomFormula(outEffects, parameters, predicates, true, ctx.literal().atomic_formula());
            } else if (ctx.literal().neg_atomic_formula() != null) {
                visitAtomFormula(outEffects, parameters, predicates, false, ctx.literal().neg_atomic_formula().atomic_formula());
            }
        } else if (ctx.forall_effect() != null) {
            System.out.println("ERROR: not yet implemented - forall effects.");
        } else if (ctx.conditional_effect() != null) {
            System.out.println("ERROR: not yet implemented - conditional effects.");
        } else {
            System.out.println("ERROR: found an empty effect in action declaration.");
        }
    }

    private void visitLiteralConj(VectorBuilder<Literal> outLiterals, Seq<Variable> parameters, Seq<Predicate> predicates, hddlParser.Gd_conjuctionContext ctx) {
        for (hddlParser.GdContext gd : ctx.gd()) {
            if ((gd.atomic_formula() == null) && (gd.gd_negation() == null)) {
                System.out.println("ERROR: not yet implemented - an element of a conjunction of preconditions or effects seems to be no literal.");
            } else {
                if (gd.atomic_formula() != null) {
                    visitAtomFormula(outLiterals, parameters, predicates, true, gd.atomic_formula());
                } else if (gd.gd_negation() != null) {
                    visitNegAtomFormula(outLiterals, parameters, predicates, gd.gd_negation());
                }
            }
        }
    }

    private void visitNegAtomFormula(VectorBuilder<Literal> outLiterals, Seq<Variable> parameters, Seq<Predicate> predicates, hddlParser.Gd_negationContext ctx) {
        if (ctx.gd().atomic_formula() != null) {
            visitAtomFormula(outLiterals, parameters, predicates, false, ctx.gd().atomic_formula());
        } else if (ctx.gd().gd_equality_constraint() != null) {
            // todo: add equality constraint
            hddlParser.Var_or_constContext v1 = ctx.gd().gd_equality_constraint().var_or_const(0);
            hddlParser.Var_or_constContext v2 = ctx.gd().gd_equality_constraint().var_or_const(1);
        } else {
            System.out.println("ERROR: not yet implemented - a negated literal seems to be no atomic formula.");
        }
    }

    private void visitAtomFormula(VectorBuilder<Literal> outLiterals, Seq<Variable> taskParameters, Seq<Predicate> predicates, boolean isPositive, hddlParser.Atomic_formulaContext ctx) {
        // get predicate definition
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

        // get variable definition
        VectorBuilder<Variable> parameterVariables = new VectorBuilder<>();
        List<hddlParser.Var_or_constContext> params = ctx.var_or_const();

        for (hddlParser.Var_or_constContext param : params) {
            String pname;
            if (param.VAR_NAME() != null)
                pname = param.VAR_NAME().toString(); // this is the name of a variable, it starts with a question mark
            else
                pname = param.NAME().toString(); // this is the name of a constant
            Variable var = null;
            for (int i = 0; i < taskParameters.length(); i++) {
                Variable v = taskParameters.apply(i);
                if (v.name().equals(pname)) {
                    var = v;
                    break;
                }
            }
            // todo: this could also be a constant
            if (var == null) {
                System.out.println("ERROR: The variable name \"" + param.VAR_NAME() + "\" is used in a precondition or effect definition, but is not defined in the actions parameter definition.");
                System.out.println("Maybe it is a constant, then it is not your fault, but just a not yet implemented feature, but anyway...");
            }
            parameterVariables.$plus$eq(var);
        }

        outLiterals.$plus$eq(new Literal(predicate, isPositive, parameterVariables.result()));
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
            for (int j = 0; j < c.new_consts().getChildCount(); j++) {
                final String constName = c.new_consts().NAME(j).toString();
                internalModel.addConst(type, constName);
            }
        }
    }
}
