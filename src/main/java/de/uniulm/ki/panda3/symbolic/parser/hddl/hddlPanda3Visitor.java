package de.uniulm.ki.panda3.symbolic.parser.hddl;

import de.uniulm.ki.panda3.symbolic.csp.CSP;
import de.uniulm.ki.panda3.symbolic.csp.Equal;
import de.uniulm.ki.panda3.symbolic.csp.NotEqual;
import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint;
import de.uniulm.ki.panda3.symbolic.domain.*;
import de.uniulm.ki.panda3.symbolic.logic.*;
import de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel.internalSortsAndConsts;
import de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel.parserUtil;
import de.uniulm.ki.panda3.util.seqProviderList;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.CausalLink;
import de.uniulm.ki.panda3.symbolic.plan.element.OrderingConstraint;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.panda3.symbolic.plan.flaw.AbstractPlanStep;
import de.uniulm.ki.panda3.symbolic.plan.flaw.CausalThreat;
import de.uniulm.ki.panda3.symbolic.plan.flaw.OpenPrecondition;
import de.uniulm.ki.panda3.symbolic.plan.flaw.UnboundVariable;
import de.uniulm.ki.panda3.symbolic.plan.modification.*;
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering;
import de.uniulm.ki.panda3.symbolic.search.*;
import de.uniulm.ki.panda3.util.JavaToScala;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.collection.immutable.Vector;
import scala.collection.immutable.VectorBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dhoeller on 14.04.15.
 */
public class hddlPanda3Visitor {

    private final static List<Class<?>> alwaysAllowedModificationsClasses = new LinkedList<>();
    private final static List<Class<?>> alwaysAllowedFlawClasses = new LinkedList<>();

    static {

        alwaysAllowedModificationsClasses.add(AddOrdering.class);
        alwaysAllowedModificationsClasses.add(BindVariableToValue.class);
        alwaysAllowedModificationsClasses.add(InsertCausalLink.class);
        alwaysAllowedModificationsClasses.add(MakeLiteralsUnUnifiable.class);

        alwaysAllowedFlawClasses.add(CausalThreat.class);
        alwaysAllowedFlawClasses.add(OpenPrecondition.class);
        alwaysAllowedFlawClasses.add(UnboundVariable.class);
    }


    public final static scala.collection.immutable.Map<PlanStep, DecompositionMethod> planStepsDecomposedBy =
            scala.collection.immutable.Map$.MODULE$.<PlanStep, DecompositionMethod>empty();
    public final static scala.collection.immutable.Map<PlanStep, Tuple2<PlanStep, PlanStep>> planStepsDecompositionParents =
            scala.collection.immutable.Map$.MODULE$.<PlanStep, Tuple2<PlanStep, PlanStep>>empty();

    private boolean warningOutput;

    public hddlPanda3Visitor() {
        this(true);
    }

    public hddlPanda3Visitor(boolean warningOutput) {
        this.warningOutput = warningOutput;
    }

    private parseReport report = new parseReport();

    public Tuple2<Domain, Plan> visitInstance(@NotNull antlrHDDLParser.DomainContext ctxDomain, @NotNull antlrHDDLParser.ProblemContext ctxProblem) {

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
        assert (init.parameters().equals(goal.parameters()));

        // determine problem type
        List<Class<?>> allowedModificationsClasses = new LinkedList<Class<?>>();
        List<Class<?>> allowedFlawClasses = new LinkedList<Class<?>>();
        allowedModificationsClasses.addAll(alwaysAllowedModificationsClasses);
        allowedFlawClasses.addAll(alwaysAllowedFlawClasses);

        if (ctxProblem.p_htn() == null) {
            // this is a classical PDDL file
            allowedModificationsClasses.add(InsertPlanStepWithLink.class);
        } else {
            allowedModificationsClasses.add(DecomposePlanStep.class);
            allowedFlawClasses.add(AbstractPlanStep.class);

            if (ctxProblem.p_htn().children.get(1).getText().equals(":htnti")) {
                allowedModificationsClasses.add(InsertPlanStepWithLink.class);
            } else assert (ctxProblem.p_htn().children.get(1).getText().equals(":htn"));
        }

        IsModificationAllowed allowedModifications = new ModificationsByClass(JavaToScala.toScalaSeq(allowedModificationsClasses));
        IsFlawAllowed allowedFlaws = new FlawsByClass(JavaToScala.toScalaSeq(allowedFlawClasses));

        seqProviderList<Variable> tniVars = new seqProviderList<>();
        tniVars.add(init.parameters());

        seqProviderList<VariableConstraint> tniConstr = new seqProviderList<>();
        tniConstr.add(init.parameterConstraints());
        tniConstr.add(goal.parameterConstraints());

        antlrHDDLParser.Tasknetwork_defContext tnCtx = null;

        Plan p;
        if (ctxProblem.p_htn() != null) { // HTN or TIHTN
            p = visitTaskNetwork(ctxProblem.p_htn().tasknetwork_def(), tniVars, tniConstr, psInit, psGoal, tasks, predicates, sorts,
                    allowedModifications, allowedFlaws, planStepsDecomposedBy, planStepsDecompositionParents);
        } else {
            CSP csp = new CSP(JavaToScala.toScalaSet(tniVars.getList()), tniConstr.result());
            seqProviderList<PlanStep> planSteps = new seqProviderList<>();
            planSteps.add(psInit);
            planSteps.add(psGoal);
            TaskOrdering taskOrderings = new TaskOrdering(new VectorBuilder<OrderingConstraint>().result(), new VectorBuilder<PlanStep>().result());
            taskOrderings = taskOrderings.addPlanStep(psInit).addPlanStep(psGoal);
            p = new Plan(planSteps.result(), new seqProviderList<CausalLink>().result(), taskOrderings, csp, psInit, psGoal,
                    allowedModifications, allowedFlaws, planStepsDecomposedBy, planStepsDecompositionParents);
        }
        report.printReport();

        Tuple2<Domain, Plan> initialProblem = new Tuple2<>(d, p);
        return initialProblem;
    }

    private Plan visitTaskNetwork(antlrHDDLParser.Tasknetwork_defContext tnCtx, seqProviderList<Variable> variables,
                                  seqProviderList<VariableConstraint> constraints, PlanStep psInit, PlanStep psGoal,
                                  Seq<Task> tasks, Seq<Predicate> predicates, Seq<Sort> sorts,
                                  IsModificationAllowed allowedModifications, IsFlawAllowed allowedFlaws,
                                  Map<PlanStep, DecompositionMethod> planStepsDecomposedBy,
                                  Map<PlanStep, Tuple2<PlanStep, PlanStep>> planStepsDecompositionParents) {
        HashMap<String, PlanStep> idMap = new HashMap<>(); // used to define ordering constraints and causal links
        TaskOrdering taskOrderings = new TaskOrdering(new VectorBuilder<OrderingConstraint>().result(), new VectorBuilder<PlanStep>().result());
        seqProviderList<PlanStep> planSteps = new seqProviderList<>();
        planSteps.add(psInit);
        planSteps.add(psGoal);
        taskOrderings = taskOrderings.addPlanStep(psInit).addPlanStep(psGoal);

        if (tnCtx.subtask_defs() != null) {
            for (int i = 0; i < tnCtx.subtask_defs().subtask_def().size(); i++) {
                antlrHDDLParser.Subtask_defContext psCtx = tnCtx.subtask_defs().subtask_def().get(i);

                // get task schema definition
                String psName = psCtx.task_symbol().NAME().toString();
                Task schema = parserUtil.taskByName(psName, tasks);
                if (schema == null) {
                    if (warningOutput) System.out.println("Task schema undefined: " + psName);
                    report.reportSkippedMethods();
                    continue;
                }

                // get variables that are passed from the method to the subtask
                seqProviderList<Variable> psVars = new seqProviderList<>();
                for (antlrHDDLParser.Var_or_constContext vName : psCtx.var_or_const()) {
                    Variable v = getVariable(vName, variables, constraints, sorts);
                    psVars.add(v);
                }

                // while reading task schemata, constants that are included can cause new parameters that are added
                // to the schema. These new parameters have to be added to the plan steps.

                // todo: store the original number of parameters and check whether the user gave the correct number here

                for (int parameter = psVars.size(); parameter < schema.parameters().length(); parameter++) {
                    Variable v = schema.parameters().apply(parameter);
                    Variable newVar = new Variable(parameter, psName + v.name(), v.sort());
                    psVars.add(newVar);
                    variables.add(newVar);
                    // the constraints will be added by the plan
                }

                Seq<Variable> psVarsSeq = psVars.result();
                if (schema.parameters().size() != psVarsSeq.size()) {
                    if (warningOutput) System.out.println("The task schema " + schema.name() + " is defined with " + schema.parameters().size() + " but used with " + psVarsSeq.size() + " parameters.");
                    if (warningOutput) System.out.println(schema.parameters());
                    report.reportSkippedMethods();
                    continue;
                }

                PlanStep ps = new PlanStep(i + 2, schema, psVarsSeq);
                taskOrderings = taskOrderings.addPlanStep(ps).addOrdering(OrderingConstraint.apply(psInit, ps)).addOrdering(OrderingConstraint.apply(ps, psGoal));
                if (psCtx.subtask_id() != null) {
                    String id = psCtx.subtask_id().NAME().toString();
                    idMap.put(id, ps);
                }
                planSteps.add(ps);
            }
        }

        // read variable constraints
        if (tnCtx.constraint_defs() != null) {
            for (antlrHDDLParser.Constraint_defContext constraint : tnCtx.constraint_defs().constraint_def()) {

                List<Variable> constrainedVars = new ArrayList<>();
                for (antlrHDDLParser.Var_or_constContext vName : constraint.var_or_const()) {
                    Variable v = getVariable(vName, variables, constraints, sorts);
                    constrainedVars.add(v);
                }
                assert (constrainedVars.size() == 2);

                VariableConstraint vc;
                if (constraint.children.get(1).toString().equals("not")) { // this is an unequal constraint
                    vc = new NotEqual(constrainedVars.get(0), constrainedVars.get(1));
                } else {// this is an equal constraint
                    vc = new Equal(constrainedVars.get(0), constrainedVars.get(1));
                }
                constraints.add(vc);
            }
        }

        // read ordering
        String orderingMode = tnCtx.children.get(0).toString();
        if ((orderingMode.equals(":ordered-subtasks")) || (orderingMode.equals(":ordered-tasks"))) {
            for (int i = 2; i < planSteps.size() - 1; i++) {
                taskOrderings = taskOrderings.addOrdering(planSteps.get(i), planSteps.get(i + 1));
            }
        } else { // i.e. :tasks or :subtasks
            if ((tnCtx.ordering_defs() != null) && (tnCtx.ordering_defs().ordering_def() != null)) {
                for (antlrHDDLParser.Ordering_defContext o : tnCtx.ordering_defs().ordering_def()) {
                    String idLeft = o.subtask_id(0).NAME().toString();
                    String idRight = o.subtask_id(1).NAME().toString();
                    if (!idMap.containsKey(idLeft)) {
                        if (warningOutput) System.out.println("ERROR: The ID \"" + idLeft + "\" is not a subtask ID, but used in the ordering constraints.");
                    } else if (!idMap.containsKey(idRight)) {
                        if (warningOutput) System.out.println("ERROR: The ID \"" + idRight + "\" is not a subtask ID, but used in the ordering constraints.");
                    } else {
                        PlanStep left = idMap.get(idLeft);
                        PlanStep right = idMap.get(idRight);
                        taskOrderings = taskOrderings.addOrdering(left, right);
                    }
                }
            }
        }

        seqProviderList<CausalLink> causalLinks = new seqProviderList<>();
        if (tnCtx.causallink_defs() != null) {
            for (antlrHDDLParser.Causallink_defContext cl : tnCtx.causallink_defs().causallink_def()) {
                assert (cl.subtask_id().size() == 2);
                String producerID = cl.subtask_id().get(0).getText();
                String consumerID = cl.subtask_id().get(1).getText();
                PlanStep producer;
                PlanStep consumer;

                if (producerID.toLowerCase().equals("init")) {
                    producer = psInit;
                } else if (!idMap.containsKey(producerID)) {
                    if (warningOutput) System.out.println("The task id " + producerID + " is used in causal link definition, but no task is definied with this id.");
                    report.reportSkippedMethods();
                    continue;
                } else {
                    producer = idMap.get(producerID);
                }

                if (consumerID.toLowerCase().equals("goal")) {
                    consumer = psGoal;
                } else if (!idMap.containsKey(consumerID)) {
                    if (warningOutput) System.out.println("The task id " + consumerID + " is used in causal link definition, but no task is definied with this id.");
                    report.reportSkippedMethods();
                    continue;
                } else {
                    consumer = idMap.get(consumerID);
                }

                Literal literal;

                if (cl.literal().atomic_formula() != null) {
                    literal = visitAtomFormula(variables, predicates, sorts, constraints, true, cl.literal().atomic_formula());
                } else { // negative literal
                    literal = visitAtomFormula(variables, predicates, sorts, constraints, false, cl.literal().neg_atomic_formula().atomic_formula());
                }
                causalLinks.add(new CausalLink(producer, consumer, literal));
            }
        }

        CSP csp = new CSP(JavaToScala.toScalaSet(variables.getList()), constraints.result());
        Plan subPlan = new Plan(planSteps.result(), causalLinks.result(), taskOrderings, csp, psInit, psGoal,
                allowedModifications, allowedFlaws, planStepsDecomposedBy, planStepsDecompositionParents);
        return subPlan;
    }

    private Task visitGoalState(Seq<Sort> sorts, Seq<Predicate> predicates, antlrHDDLParser.P_goalContext ctx) {
        seqProviderList<VariableConstraint> parameterConstraints = new seqProviderList<VariableConstraint>();
        seqProviderList<Variable> taskParameters = getVariableForEveryConst(sorts, parameterConstraints);
        Formula f = new And<Literal>(new Vector<Literal>(0, 0, 0));
        if (ctx != null) {
            f = visitGoalConditions(predicates, taskParameters, sorts, parameterConstraints, ctx.gd());
        }
        return new GeneralTask("goal", true, taskParameters.result(), taskParameters.result(), parameterConstraints.result(), f, new And<Literal>(new Vector<Literal>(0, 0, 0)));
    }

    private Task visitInitialState(Seq<Sort> sorts, Seq<Predicate> predicates, antlrHDDLParser.P_initContext ctx) {
        seqProviderList<VariableConstraint> varConstraints = new seqProviderList<>();
        seqProviderList<Variable> parameter = getVariableForEveryConst(sorts, varConstraints);

        seqProviderList<Literal> initEffects = new seqProviderList<>();
        for (antlrHDDLParser.Init_elContext el : ctx.init_el()) {
            if (el.literal() != null) { // normal STRIPS init
                if (el.literal().atomic_formula() != null) {
                    initEffects.add(visitAtomFormula(parameter, predicates, sorts, varConstraints, true, el.literal().atomic_formula()));
                } else if (el.literal().neg_atomic_formula() != null) {
                    initEffects.add(visitAtomFormula(parameter, predicates, sorts, varConstraints, false, el.literal().atomic_formula()));
                }
            }
        }

        return new ReducedTask("init", true, parameter.result(), parameter.result(), varConstraints.result(), new And<Literal>(new Vector<Literal>(0, 0, 0)), new And<Literal>(initEffects.result()));
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

    private Seq<DecompositionMethod> visitMethodDef(List<antlrHDDLParser.Method_defContext> ctx, Seq<Sort> sorts, Seq<Predicate> predicates, Seq<Task> tasks) {
        seqProviderList<DecompositionMethod> methods = new seqProviderList<>();
        for (antlrHDDLParser.Method_defContext m : ctx) {
            // Read abstract task
            String taskname = m.task_symbol().NAME().toString();
            Task abstractTask = parserUtil.taskByName(taskname, tasks);

            if (abstractTask == null) {
                if (warningOutput) System.out.println("ERROR: compound task given in method definition is undefined: " + taskname);
                continue;
            } else if (abstractTask.isPrimitive()) {
                if (warningOutput) System.out.println("ERROR: compound task given in method definition is not compound, but a primitive task: " + taskname);
                continue;
            }

            // Read method's name and parameters
            String nameStr = m.method_symbol().NAME().toString();
            seqProviderList<Variable> methodParams = typedParamsToVars(sorts, abstractTask.parameters().size(), m.typed_var_list().typed_vars());

            seqProviderList<Variable> tnVars = new seqProviderList<>();
            seqProviderList<VariableConstraint> tnConstraints = new seqProviderList<>();
            tnVars.add(methodParams.result());
            tnVars.add(abstractTask.parameters());

            // Read task's parameters and connect them to method's parameters
            boolean paramsOk = this.connectMethVarsAndTaskVars(methodParams.result(), tnConstraints, abstractTask, m.var_or_const());
            if (!paramsOk) {
                continue;
            }

            // Read method preconditions
            Formula precFormula = new And<Literal>(new Vector<Literal>(0, 0, 0));
            boolean hasPrecondition;
            if (m.gd() != null) {
                seqProviderList<VariableConstraint> constraints = new seqProviderList<>();
                precFormula = visitGoalConditions(predicates, tnVars, sorts, constraints, m.gd());
                tnConstraints.add(constraints.result());
                hasPrecondition = true;
            } else {
                hasPrecondition = false;
            }

            boolean hasEffect = false;
            Formula effect = new And<Literal>(new Vector<Literal>(0, 0, 0));
            if (m.effect_body() != null) {
                if ((m.effect_body() != null) && (m.effect_body().c_effect() != null)) {
                    effect = visitConEff(methodParams, tnConstraints, sorts, predicates, m.effect_body().c_effect());
                } else if ((m.effect_body() != null) && (m.effect_body().eff_conjuntion() != null)) {
                    effect = visitConEffConj(methodParams, tnConstraints, sorts, predicates, m.effect_body().eff_conjuntion());
                }
                hasEffect = true;
            }

            // Create subplan, method and add it to method list
            GeneralTask initSchema = new GeneralTask("init", true, abstractTask.parameters(), abstractTask.parameters(), new Vector<VariableConstraint>(0, 0, 0), new And<Literal>(new Vector<Literal>(0, 0, 0)), abstractTask.precondition());
            GeneralTask goalSchema = new GeneralTask("goal", true, abstractTask.parameters(), abstractTask.parameters(), new Vector<VariableConstraint>(0, 0, 0), abstractTask.effect(), new And<Literal>(new Vector<Literal>(0, 0, 0)));

            PlanStep psInit = new PlanStep(-1, initSchema, abstractTask.parameters());
            PlanStep psGoal = new PlanStep(-2, goalSchema, abstractTask.parameters());

            Plan subPlan = visitTaskNetwork(m.tasknetwork_def(), tnVars, tnConstraints, psInit, psGoal, tasks, predicates, sorts,
                    NoModifications$.MODULE$, NoFlaws$.MODULE$, hddlPanda3Visitor.planStepsDecomposedBy, hddlPanda3Visitor.planStepsDecompositionParents);

            DecompositionMethod method;
            if (hasPrecondition || hasEffect) {
                // todo @Gregor: wenn "hasEffect" wahr ist, steht der Effekt in "effect"
                method = new SHOPDecompositionMethod(abstractTask, subPlan, precFormula, effect, nameStr);
            } else {
                method = new SimpleDecompositionMethod(abstractTask, subPlan, nameStr);
            }
            methods.add(method);
        }
        return methods.result();
    }

    public boolean connectMethVarsAndTaskVars(Seq<Variable> methodParams, seqProviderList<VariableConstraint> tnConstraints, Task abstractTask, List<antlrHDDLParser.Var_or_constContext> givenParamsCtx) {
        boolean paramsOk = true;
        for (int i = 0; i < givenParamsCtx.size(); i++) {

            antlrHDDLParser.Var_or_constContext param = givenParamsCtx.get(i);

            if (param.NAME() != null) { // this is a consts
                if (warningOutput) System.out.println("ERROR: not yet implemented - a const is used in task definition");
            }

            Variable methodVar = parserUtil.getVarByName(methodParams, param.VAR_NAME().toString());
            if (methodVar == null) {
                if (warningOutput) System.out.println("ERROR: parameter used in method definition has not been found in method's parameter definition.");
                paramsOk = false;
                break;
            }
            Variable taskVar = abstractTask.parameters().apply(i);
            VariableConstraint vc = new Equal(taskVar, methodVar);
            tnConstraints.add(vc);
        }
        return paramsOk;
    }

    private Seq<Task> visitTaskDefs(Seq<Sort> sorts, Seq<Predicate> predicates, antlrHDDLParser.DomainContext ctxDomain) {
        VectorBuilder<Task> tasks = new VectorBuilder<>();
        for (antlrHDDLParser.Action_defContext a : ctxDomain.action_def()) {
            Task t = visitTaskDef(sorts, predicates, a.task_def(), true);
            tasks.$plus$eq(t);
        }
        for (antlrHDDLParser.Comp_task_defContext c : ctxDomain.comp_task_def()) {
            Task t = visitTaskDef(sorts, predicates, c.task_def(), false);
            tasks.$plus$eq(t);
        }
        return tasks.result();
    }

    private Task visitTaskDef(Seq<Sort> sorts, Seq<Predicate> predicates, antlrHDDLParser.Task_defContext ctxTask, boolean isPrimitive) {
        String taskName = ctxTask.task_symbol().NAME().toString();
        seqProviderList<Variable> parameters = typedParamsToVars(sorts, 0, ctxTask.typed_var_list().typed_vars());
        seqProviderList<VariableConstraint> constraints = new seqProviderList<>();

        // due to constants in the definition, this may increase. However, we want to know the original count.
        int numOfParams = parameters.size();

        // build preconditions
        Formula f = new And<Literal>(new Vector<Literal>(0, 0, 0));
        if (ctxTask.gd() != null) {
            f = visitGoalConditions(predicates, parameters, sorts, constraints, ctxTask.gd());
        }

        // build effects
        // todo: implement forall- and conditional effects

        Formula f2 = new And<Literal>(new Vector<Literal>(0, 0, 0));

        if ((ctxTask.effect_body() != null) && (ctxTask.effect_body().c_effect() != null)) {
            f2 = visitConEff(parameters, constraints, sorts, predicates, ctxTask.effect_body().c_effect());
        } else if ((ctxTask.effect_body() != null) && (ctxTask.effect_body().eff_conjuntion() != null)) {
            f2 = visitConEffConj(parameters, constraints, sorts, predicates, ctxTask.effect_body().eff_conjuntion());
        }
        return new GeneralTask(taskName, isPrimitive, parameters.result(), parameters.result(numOfParams), constraints.result(), f, f2);
    }

    private seqProviderList<Variable> typedParamsToVars(Seq<Sort> sorts, int startId, List<antlrHDDLParser.Typed_varsContext> vars) {
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
    private Formula visitGoalConditions(Seq<Predicate> predicates, seqProviderList<Variable> parameters, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, antlrHDDLParser.GdContext ctx) {
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
        } else if (ctx.gd_equality_constraint() != null) {
            return visitEqConstraint(parameters, sorts, constraints, ctx);
        } else if (ctx.gd_empty() != null) {
            return new And<Literal>(new Vector<Literal>(0, 0, 0));
        } else {
            if (warningOutput) System.out.println(ctx.getText());
            throw new IllegalArgumentException("ERROR: Feature in Precondition is not implemented");
        }
    }

    private Formula visitEqConstraint(seqProviderList<Variable> parameters, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, antlrHDDLParser.GdContext ctx) {
        Variable var1 = getVariable(ctx.gd_equality_constraint().var_or_const(0), parameters, constraints, sorts);
        Variable var2 = getVariable(ctx.gd_equality_constraint().var_or_const(1), parameters, constraints, sorts);
        Equal ne = new Equal(var1, var2);
        constraints.add(ne);
        return new And<Literal>(new Vector<Literal>(0, 0, 0));
    }

    private Formula visitExistentialQuantifier(seqProviderList<Variable> parameters, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, antlrHDDLParser.Gd_existentialContext gd_existentialContext) {
        Tuple2<Seq<Variable>, Formula> inner2 = readInner(parameters, predicates, sorts, constraints, gd_existentialContext.typed_var_list().typed_vars(), gd_existentialContext.gd());
        return new Exists(inner2._1().apply(0), inner2._2());
    }

    private Formula visitUniveralQuantifier(seqProviderList<Variable> methodParams, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, antlrHDDLParser.Gd_univeralContext gd_conjuctionContext) {
        Tuple2<Seq<Variable>, Formula> inner2 = readInner(methodParams, predicates, sorts, constraints, gd_conjuctionContext.typed_var_list().typed_vars(), gd_conjuctionContext.gd());
        return new Forall(inner2._1().apply(0), inner2._2());
    }

    private Tuple2<Seq<Variable>, Formula> readInner(seqProviderList<Variable> methodParams, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, List<antlrHDDLParser.Typed_varsContext> typed_varsContexts, antlrHDDLParser.GdContext gd) {

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
            if (warningOutput) System.out.println("ERROR: More than one quantified Variable - this is not yet implemented.");
        }

        // read inner formula
        Formula inner = visitGoalConditions(predicates, parameters2, sorts, constraints, gd);
        return new Tuple2<>(quantifiedVars.result(), inner);
    }

    private Formula visitConEffConj(seqProviderList<Variable> parameters, seqProviderList<VariableConstraint> constraints, Seq<Sort> sorts, Seq<Predicate> predicates, antlrHDDLParser.Eff_conjuntionContext ctx) {
        seqProviderList<Literal> conj = new seqProviderList<>();

        for (antlrHDDLParser.C_effectContext eff : ctx.c_effect()) {
            Literal t = visitConEff(parameters, constraints, sorts, predicates, eff);
            if (t != null) // it may be null if it uses a not yet implemented feature. This should already have been reported, so that we can skip it here
                conj.add(t);
        }
        return new And(conj.result());
    }

    private Literal visitConEff(seqProviderList<Variable> parameters, seqProviderList<VariableConstraint> constraints, Seq<Sort> sorts, Seq<Predicate> predicates, antlrHDDLParser.C_effectContext ctx) {
        if (ctx.literal() != null) {
            if (ctx.literal().atomic_formula() != null) {
                return visitAtomFormula(parameters, predicates, sorts, constraints, true, ctx.literal().atomic_formula());
            } else if (ctx.literal().neg_atomic_formula() != null) {
                return visitAtomFormula(parameters, predicates, sorts, constraints, false, ctx.literal().neg_atomic_formula().atomic_formula());
            }
        } else if (ctx.forall_effect() != null) {
            this.report.reportForallEffect();
        } else if (ctx.conditional_effect() != null) {
            this.report.reportConditionalEffects();
        } else if (ctx.p_effect() != null) {
            this.report.reportNumericEffect();
        } else {
            if (warningOutput) System.out.println("ERROR: found an empty effect in action declaration.");
        }
        return null;
    }

    enum conOrDis {
        or, and
    }

    /**
     * Parse a conjunction or disjunction of goal conditions
     */
    private Formula visitGdConOrDisjunktion(conOrDis what, seqProviderList<Variable> parameters, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, List<antlrHDDLParser.GdContext> ctx) {
        seqProviderList<Formula> elements = new seqProviderList<>();
        for (antlrHDDLParser.GdContext gd : ctx) {
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
    private Formula visitNegAtomFormula(seqProviderList<Variable> parameters, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, antlrHDDLParser.Gd_negationContext ctx) {
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
    private Literal visitAtomFormula(seqProviderList<Variable> taskParameters, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, boolean isPositive, antlrHDDLParser.Atomic_formulaContext ctx) {
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
            if (warningOutput) System.out.println("ERROR: a precondition or effect in action definition refers to predicate \"" + predName + "\" that can't be found in predicate declaration.");
        }

        //
        // get variable definition
        //
        seqProviderList<Variable> parameterVariables = new seqProviderList<>();
        List<antlrHDDLParser.Var_or_constContext> params = ctx.var_or_const();

        for (antlrHDDLParser.Var_or_constContext param : params) {
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
    private Variable getVariable(antlrHDDLParser.Var_or_constContext param, seqProviderList<Variable> parameters, seqProviderList<VariableConstraint> constraints, Seq<Sort> sorts) {
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
            String pname = param.NAME().getText();

            // - this may be an object that has already been defined in s0 -> there is already a const and a var
            // - a const in a task/method AND it is not the fist occurrence -> there is already a variable
            for (int i = 0; i < constraints.size(); i++) {
                VariableConstraint vc = constraints.get(i);
                if (vc instanceof Equal) {
                    Equal e = (Equal) vc;
                    if ((e.right() instanceof Constant) && (((Constant) e.right()).name().equals(pname))) {
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
            if (warningOutput) System.out.println("ERROR: The variable name \"" + param.getText() + "\" is used in a precondition or effect definition, but is not defined in the actions parameter definition.");
            if (warningOutput) System.out.println("Maybe it is a constant, then it is not your fault, but just a not yet implemented feature, but anyway...");
        }
        return var;
    }


    private Seq<Predicate> visitPredicateDeclaration(Seq<Sort> sorts, antlrHDDLParser.Predicates_defContext ctx) {
        VectorBuilder<Predicate> predicates = new VectorBuilder<>();

        List<antlrHDDLParser.Atomic_formula_skeletonContext> listDefs = ctx.atomic_formula_skeleton();
        if (listDefs == null) {
            return new Vector<>(0, 0, 0);
        }

        for (antlrHDDLParser.Atomic_formula_skeletonContext def : listDefs) {
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
    private void visitTypedList(VectorBuilder<Sort> lSorts, VectorBuilder<String> lNames, Seq<Sort> sorts, List<antlrHDDLParser.Typed_varsContext> ctx) {
        readTypedList(lSorts, lNames, sorts, ctx, "Somebody");
    }

    private void readTypedList(VectorBuilder<Sort> outSorts, VectorBuilder<String> outNames, Seq<Sort> sorts, List<antlrHDDLParser.Typed_varsContext> ctx, String ErrorMsgWhoIsReader) {
        for (antlrHDDLParser.Typed_varsContext varList : ctx) { // one list contains one or more var of the same type

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
                if (warningOutput) System.out.println("ERROR: " + ErrorMsgWhoIsReader + " refers to sort \"" + sortName + "\" that can't be found in sort declaration.");
            }

            for (TerminalNode oneName : varList.VAR_NAME()) {
                outNames.$plus$eq(oneName.toString());
                outSorts.$plus$eq(s);
            }
        }
    }

    private static final String ARTIFICIAL_ROOT_SORT = "__Object";

    public Seq<Sort> visitTypeAndObjDef(@NotNull antlrHDDLParser.DomainContext ctxDomain, @NotNull antlrHDDLParser.ProblemContext ctxProblem) {

        // Extract type hierarchy from domain file
        internalSortsAndConsts internalSortModel = new internalSortsAndConsts(); // do not pass out, use only here

        List<antlrHDDLParser.One_defContext> typeDefs = ctxDomain.type_def().one_def(); // the "one_def" tag might appear more than once

        for (antlrHDDLParser.One_defContext typeDef : typeDefs) {

            antlrHDDLParser.New_typesContext newTypes = typeDef.new_types();

            final String parent_type = typeDef.var_type() == null ? ARTIFICIAL_ROOT_SORT : typeDef.var_type().NAME().toString();
            for (int j = 0; j < newTypes.getChildCount(); j++) {
                final String child_type = newTypes.NAME(j).toString();
                internalSortModel.addParent(child_type, parent_type);
            }
        }

        // Extract constant symbols from domain and problem file
        if (ctxDomain.const_def() != null) {
            List<antlrHDDLParser.Typed_objsContext> domainConsts = ctxDomain.const_def().typed_obj_list().typed_objs();
            addToInternalModel(internalSortModel, domainConsts);
        }
        if (ctxProblem.p_object_declaration() != null) {
            List<antlrHDDLParser.Typed_objsContext> problemConsts = ctxProblem.p_object_declaration().typed_obj_list().typed_objs();
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

    private void addToInternalModel(internalSortsAndConsts internalModel, List<antlrHDDLParser.Typed_objsContext> consts) {
        for (antlrHDDLParser.Typed_objsContext c : consts) {
            final String type = c.var_type().NAME().toString();
            for (int j = 0; j < c.new_consts().size(); j++) {
                final String constName = c.new_consts().get(j).getText();
                internalModel.addConst(type, constName);
            }
        }
    }
}
