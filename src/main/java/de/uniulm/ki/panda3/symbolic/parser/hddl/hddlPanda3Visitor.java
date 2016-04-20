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
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.panda3.symbolic.plan.flaw.AbstractPlanStep;
import de.uniulm.ki.panda3.symbolic.plan.flaw.CausalThreat;
import de.uniulm.ki.panda3.symbolic.plan.flaw.OpenPrecondition;
import de.uniulm.ki.panda3.symbolic.plan.flaw.UnboundVariable;
import de.uniulm.ki.panda3.symbolic.plan.modification.*;
import de.uniulm.ki.panda3.symbolic.search.FlawsByClass;
import de.uniulm.ki.panda3.symbolic.search.IsFlawAllowed;
import de.uniulm.ki.panda3.symbolic.search.IsModificationAllowed;
import de.uniulm.ki.panda3.symbolic.search.ModificationsByClass;
import de.uniulm.ki.panda3.util.JavaToScala;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import scala.*;
import scala.collection.JavaConversions;
import scala.collection.Seq;
import scala.collection.immutable.Vector;
import scala.collection.immutable.VectorBuilder;
import scala.runtime.AbstractFunction1;

import java.util.*;
import java.util.HashMap;

/**
 * Created by dhoeller on 14.04.15.
 */
public class hddlPanda3Visitor {

    private final static List<Class<?>> allowedModificationsClasses = new LinkedList<>();
    private final static List<Class<?>> allowedFlawClasses = new LinkedList<>();

    static {

        allowedModificationsClasses.add(AddOrdering.class);
        allowedModificationsClasses.add(BindVariableToValue.class);
        allowedModificationsClasses.add(DecomposePlanStep.class);
        allowedModificationsClasses.add(InsertCausalLink.class);
        //allowedModificationsClasses.add(InsertPlanStepWithLink.class); // TODO what to do Daniel ???
        allowedModificationsClasses.add(MakeLiteralsUnUnifiable.class);

        allowedFlawClasses.add(AbstractPlanStep.class);
        allowedFlawClasses.add(CausalThreat.class);
        allowedFlawClasses.add(OpenPrecondition.class);
        allowedFlawClasses.add(UnboundVariable.class);

    }

    public final static IsModificationAllowed allowedModifications = new ModificationsByClass(JavaToScala.toScalaSeq(allowedModificationsClasses));
    public final static IsFlawAllowed allowedFlaws = new FlawsByClass(JavaToScala.toScalaSeq(allowedFlawClasses));
    public final static scala.collection.immutable.Map<PlanStep, DecompositionMethod> planStepsDecomposedBy =
            scala.collection.immutable.Map$.MODULE$.<PlanStep, DecompositionMethod>empty();
    public final static scala.collection.immutable.Map<PlanStep, Tuple2<PlanStep, PlanStep>> planStepsDecompositionParents =
            scala.collection.immutable.Map$.MODULE$.<PlanStep, Tuple2<PlanStep, PlanStep>>empty();


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

        visitInitialTN(ctxProblem.p_htn(), tn, tasks, sorts);


        Plan p = new Plan(tn.planSteps(), tn.causalLinks(), tn.taskOrderings(), tn.csp(), psInit, psGoal, allowedModifications, allowedFlaws, planStepsDecomposedBy,
                planStepsDecompositionParents);

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

            PlanStep psNew = new PlanStep(psID++, schema, parameters.result());
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
        Formula f = new And<Literal>(new Vector<Literal>(0, 0, 0));
        if (ctx != null) {
            f = visitGoalConditions(predicates, taskParameters, sorts, parameterConstraints, ctx.gd());
        }
        return new GeneralTask("goal", true, taskParameters.result(), parameterConstraints.result(), f, new And<Literal>(new Vector<Literal>(0, 0, 0)));
    }

    private Task visitInitialState(Seq<Sort> sorts, Seq<Predicate> predicates, hddlParser.P_initContext ctx) {
        seqProviderList<VariableConstraint> varConstraints = new seqProviderList<>();
        seqProviderList<Variable> parameter = getVariableForEveryConst(sorts, varConstraints);

        seqProviderList<Literal> initEffects = new seqProviderList<>();
        for (hddlParser.LiteralContext lc : ctx.literal()) {
            if (lc.atomic_formula() != null) {
                initEffects.add(visitAtomFormula(parameter, predicates, sorts, varConstraints, true, lc.atomic_formula()));
            } else if (lc.neg_atomic_formula() != null) {
                initEffects.add(visitAtomFormula(parameter, predicates, sorts, varConstraints, false, lc.atomic_formula()));
            }
        }
        return new ReducedTask("init", true, parameter.result(), varConstraints.result(), new And<Literal>(new Vector<Literal>(0, 0, 0)), new And<Literal>(initEffects.result()));
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
            Formula precFormula = null;
            boolean hasPrecondition;
            if (m.gd() != null) {
                seqProviderList<VariableConstraint> constraints = new seqProviderList<>();
                precFormula = visitGoalConditions(predicates, methodParams, sorts, constraints, m.gd());
                subNetwork.addCspConstraints(constraints.result());
                hasPrecondition = true;
            } else {
                hasPrecondition = false;
            }

            // Create subplan, method and add it to method list
            Plan subPlan = subNetwork.readTaskNetwork(m.tasknetwork_def(), methodParams.result(), abstractTask, tasks, sorts);

            DecompositionMethod method;
            if (hasPrecondition) {
                method = new SHOPDecompositionMethod(abstractTask, subPlan, precFormula);
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
        Formula f = new And<Literal>(new Vector<Literal>(0, 0, 0));
        if (ctxTask.gd() != null) {
            f = visitGoalConditions(predicates, parameters, sorts, constraints, ctxTask.gd());
        }

        // build effects
        // todo: implement forall- and conditional effects

        Formula f2 = new And<Literal>(new Vector<Literal>(0, 0, 0));

        if ((ctxTask.effect_body() != null) && (ctxTask.effect_body().c_effect() != null)) {
            f2 = visitConEff(parameters, sorts, predicates, ctxTask.effect_body().c_effect());
        } else if ((ctxTask.effect_body() != null) && (ctxTask.effect_body().eff_conjuntion() != null)) {
            f2 = visitConEffConj(parameters, sorts, predicates, ctxTask.effect_body().eff_conjuntion());
        }
        return new GeneralTask(taskName, isPrimitive, parameters.result(), constraints.result(), f, f2);
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

    /**
     * Dispatcher for parsing goal descriptions (GD). Please be aware that these descriptions are used at several
     * places in the grammar, e.g. as precondition of actions, tasks or methods and not just as goal condition.
     */
    private Formula visitGoalConditions(Seq<Predicate> predicates, seqProviderList<Variable> parameters, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, hddlParser.GdContext ctx) {
        if (ctx.atomic_formula() != null) { // a single precondition
            return visitAtomFormula(parameters, predicates, sorts, constraints, true, ctx.atomic_formula());
        } else if (ctx.gd_negation() != null) { // a negated single precondition
            return visitNegAtomFormula(parameters, predicates, sorts, constraints, ctx.gd_negation());
        } else if (ctx.gd_conjuction() != null) { // a conduction of preconditions
            return visitGdConOrDisjunktion(conOrDis.and, parameters, predicates, sorts, constraints, ctx.gd_conjuction().gd());
        } else if (ctx.gd_univeral() != null) {
            return visitUniveralQuantifier(parameters, predicates, sorts, constraints, ctx.gd_univeral());
        } else if (ctx.gd_existential() != null) {
            return visitExistentialQuantifier(parameters, predicates, sorts, constraints, ctx.gd_existential());
        } else if (ctx.gd_disjuction() != null) { // well ...
            return visitGdConOrDisjunktion(conOrDis.or, parameters, predicates, sorts, constraints, ctx.gd_disjuction().gd());
        }
        return new And<Literal>(new Vector<Literal>(0, 0, 0));
    }

    private Formula visitExistentialQuantifier(seqProviderList<Variable> parameters, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, hddlParser.Gd_existentialContext gd_existentialContext) {
        Tuple2<Seq<Variable>, Formula> inner2 = readInner(parameters, predicates, sorts, constraints, gd_existentialContext.typed_var_list().typed_vars(), gd_existentialContext.gd());
        return new Exists(inner2._1().apply(0), inner2._2());
    }

    private Formula visitUniveralQuantifier(seqProviderList<Variable> methodParams, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, hddlParser.Gd_univeralContext gd_conjuctionContext) {
        Tuple2<Seq<Variable>, Formula> inner2 = readInner(methodParams, predicates, sorts, constraints, gd_conjuctionContext.typed_var_list().typed_vars(), gd_conjuctionContext.gd());
        return new Forall(inner2._1().apply(0), inner2._2());
    }

    private Tuple2<Seq<Variable>, Formula> readInner(seqProviderList<Variable> methodParams, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, List<hddlParser.Typed_varsContext> typed_varsContexts, hddlParser.GdContext gd) {

        // read new variables
        int curIndex = methodParams.size();
        seqProviderList<Variable> parameters2 = new seqProviderList<>();
        seqProviderList<Variable> quantifiedVars = new seqProviderList<>();

        for (int i = 0; i < methodParams.size(); i++) {
            parameters2.add(methodParams.get(i));
        }

        VectorBuilder<Sort> newSorts = new VectorBuilder<>();
        VectorBuilder<String> newVarnames = new VectorBuilder<>();
        visitTypedList(newSorts, newVarnames, sorts, typed_varsContexts);

        Seq<Sort> newS = newSorts.result();
        Seq<String> newN = newVarnames.result();

        for (int i = 0; i < newN.size(); i++) {
            parameters2.add(new Variable(curIndex + i, newN.apply(i), newS.apply(i)));
            quantifiedVars.add(new Variable(curIndex + i, newN.apply(i), newS.apply(i)));
        }

        if (quantifiedVars.size() > 1) {
            System.out.println("ERROR: More than one quantified Variable - this is not yet implemented.");
        }

        // read inner formula
        Formula inner = visitGoalConditions(predicates, parameters2, sorts, constraints, gd);
        return new Tuple2<>(quantifiedVars.result(), inner);
    }

    private Formula visitConEffConj(seqProviderList<Variable> parameters, Seq<Sort> sorts, Seq<Predicate> predicates, hddlParser.Eff_conjuntionContext ctx) {
        seqProviderList<Literal> conj = new seqProviderList<>();

        for (hddlParser.C_effectContext eff : ctx.c_effect()) {
            conj.add(visitConEff(parameters, sorts, predicates, eff));
        }
        return new And(conj.result());
    }

    private Literal visitConEff(seqProviderList<Variable> parameters, Seq<Sort> sorts, Seq<Predicate> predicates, hddlParser.C_effectContext ctx) {
        if (ctx.literal() != null) {
            if (ctx.literal().atomic_formula() != null) {
                return visitAtomFormula(parameters, predicates, sorts, null, true, ctx.literal().atomic_formula());
            } else if (ctx.literal().neg_atomic_formula() != null) {
                return visitAtomFormula(parameters, predicates, sorts, null, false, ctx.literal().neg_atomic_formula().atomic_formula());
            }
        } else if (ctx.forall_effect() != null) {
            System.out.println("ERROR: not yet implemented - forall effects.");
        } else if (ctx.conditional_effect() != null) {
            System.out.println("ERROR: not yet implemented - conditional effects.");
        } else {
            System.out.println("ERROR: found an empty effect in action declaration.");
        }
        return null;
    }

    enum conOrDis {
        or, and
    }

    /**
     * Parse a conjunction or disjunction of goal conditions
     */
    private Formula visitGdConOrDisjunktion(conOrDis what, seqProviderList<Variable> parameters, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, List<hddlParser.GdContext> ctx) {
        seqProviderList<Formula> elements = new seqProviderList<>();
        for (hddlParser.GdContext gd : ctx) {
            Formula f = visitGoalConditions(predicates, parameters, sorts, constraints, gd);
            elements.add(f);
        }
        if (what == conOrDis.and)
            return new And(elements.result());
        else
            return new Or(elements.result());
    }

    /**
     * This method has to handle both (1) negation of a normal formula as well as (2) "not equal" variable constraints.
     *
     * @return In the first case it returns the negation of the inner formula, in the second case it adds
     * the constraint and returns the logical identity.
     */
    private Formula visitNegAtomFormula(seqProviderList<Variable> parameters, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, hddlParser.Gd_negationContext ctx) {
        if (ctx.gd().gd_equality_constraint() != null) {
            Variable var1 = getVariable(ctx.gd().gd_equality_constraint().var_or_const(0), parameters, constraints, sorts);
            Variable var2 = getVariable(ctx.gd().gd_equality_constraint().var_or_const(1), parameters, constraints, sorts);
            NotEqual ne = new NotEqual(var1, var2);
            constraints.add(ne);
            return new And<Literal>(new Vector<Literal>(0, 0, 0));
        } else {
            Formula inner = visitGoalConditions(predicates, parameters, sorts, constraints, ctx.gd());
            return new Not(inner);
        }
    }

    /**
     * @param taskParameters
     * @param predicates
     * @param sorts
     * @param constraints
     * @param isPositive
     * @param ctx
     * @return
     */
    private Literal visitAtomFormula(seqProviderList<Variable> taskParameters, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, boolean isPositive, hddlParser.Atomic_formulaContext ctx) {
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

        return new Literal(predicate, isPositive, parameterVariables.result());
    }

    /**
     * This method gets the parse tree of a variable or constant and returns a variable that represents it in the given
     * context. There are several cases to distinguish: ir may be (1) a variable in the given context (e.g. a parameter
     * of an action), a (2) an object that has already been defined in s0 or (3) a constant in method or action
     * definition, than a new variable is introduced, bound it to the constant, and returned
     * // add it to the parameter-list
     *
     * @param param       (in) The parse tree containing the object or variable
     * @param parameters  (in/out) The parameter list in the given context (e.g. action/method parameters).
     *                    It may be updated
     * @param constraints (in/out) The constraint network in the given context. It may be updated
     * @param sorts       (in) The sort hierarchy that also contains constants for every sort.
     * @return Method returns a variable that may be out of the parameter list, or the initial state definition
     */
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
            // this may be (1) an object that has already been defined in s0
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
    The following method(s) read a typed list and fills the first vector with their names and the second one with the types. The vectors equal in length.
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
