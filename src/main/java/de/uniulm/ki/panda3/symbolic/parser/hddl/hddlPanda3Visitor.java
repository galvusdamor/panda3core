package de.uniulm.ki.panda3.symbolic.parser.hddl;

import de.uniulm.ki.panda3.symbolic.csp.*;
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
import scala.None$;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.collection.immutable.Vector;
import scala.collection.immutable.VectorBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    private boolean warningOutput = true;

    public hddlPanda3Visitor() {
        this(true);
    }

    public hddlPanda3Visitor(boolean warningOutput) {
        this.warningOutput = warningOutput;
    }

    private parseReport report = new parseReport();

    private int currentVarId = 0;

    private int nextVarId() {
        return ++currentVarId;
    }

    public Tuple2<Domain, Plan> visitInstance(@NotNull antlrHDDLParser.DomainContext ctxDomain, @NotNull antlrHDDLParser.ProblemContext ctxProblem) {
        Seq<Sort> sorts;
        if ((ctxDomain.type_def() != null) && (ctxDomain.type_def().type_def_list() != null)) {
            sorts = visitTypeAndObjDef(ctxDomain, ctxProblem);
        } else {
            sorts = new seqProviderList<Sort>().result();
        }

        Seq<Predicate> predicates = visitPredicateDeclaration(sorts, ctxDomain.predicates_def());

        seqProviderList<VariableConstraint> varConstraints = new seqProviderList<>();

        VarContext vctx = new VarContext();
        for (Variable v : getVariableForEveryConst(sorts, varConstraints).getList()) {
            vctx.addParameter(v);
        }

        Task init = visitInitialState(vctx, varConstraints, sorts, predicates, ctxProblem.p_init());
        Task goal = visitGoalState(vctx, varConstraints, sorts, predicates, ctxProblem.p_goal());
        Seq<Task> tasks = visitTaskDefs(sorts, predicates, ctxDomain);

        Seq<DecompositionMethod> decompositionMethods = visitMethodDef(ctxDomain.method_def(), sorts, predicates, tasks);
        Seq<DecompositionAxiom> decompositionAxioms = new Vector<>(0, 0, 0);

        Domain d = new Domain(sorts, predicates, tasks, decompositionMethods, decompositionAxioms, None$.empty(), None$.empty());

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


        if (ctxProblem.p_htn() != null && ctxProblem.p_htn().typed_var_list() != null) {
            seqProviderList<Variable> tniVars = typedParamsToVars(sorts, 0, ctxProblem.p_htn().typed_var_list().typed_vars());
            vctx.addParamaters(tniVars.getList());
        }


        if (!goal.parameterConstraints().equals(init.parameterConstraints())) {
            throw new RuntimeException("init and goal constraints should be equal: " + goal.parameterConstraints().equals(init.parameterConstraints()));
        }

        seqProviderList<VariableConstraint> tniConstr = new seqProviderList<>();
        //tniConstr.add(init.parameterConstraints()); //init.parameterConstraints()
        tniConstr.add(goal.parameterConstraints());

        antlrHDDLParser.Tasknetwork_defContext tnCtx = null;

        Plan p;
        if (ctxProblem.p_htn() != null) { // HTN or TIHTN
            p = visitTaskNetwork(ctxProblem.p_htn().tasknetwork_def(), vctx, tniConstr, psInit, psGoal, tasks, predicates, sorts,
                    allowedModifications, allowedFlaws, planStepsDecomposedBy, planStepsDecompositionParents);
        } else {
            CSP csp = new CSP(JavaToScala.toScalaSet(vctx.parameters), tniConstr.result());
            seqProviderList<PlanStep> planSteps = new seqProviderList<>();
            planSteps.add(psInit);
            planSteps.add(psGoal);
            TaskOrdering taskOrderings = new TaskOrdering(new VectorBuilder<OrderingConstraint>().result(), new VectorBuilder<PlanStep>().result());
            taskOrderings = taskOrderings.addPlanStep(psInit).addPlanStep(psGoal).addOrdering(psInit,psGoal);
            p = new Plan(planSteps.result(), new seqProviderList<CausalLink>().result(), taskOrderings, csp, psInit, psGoal,
                    allowedModifications, allowedFlaws, planStepsDecomposedBy, planStepsDecompositionParents);
        }
        report.printReport();

        Tuple2<Domain, Plan> initialProblem = new Tuple2<>(d, p);
        return initialProblem;
    }

    private Plan visitTaskNetwork(antlrHDDLParser.Tasknetwork_defContext tnCtx, VarContext vctx,
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
        taskOrderings = taskOrderings.addPlanStep(psInit).addPlanStep(psGoal).addOrdering(psInit, psGoal);


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
                    Variable v = getVariable(vName, vctx, constraints, sorts);
                    psVars.add(v);
                }

                // while reading task schemata, constants that are included can cause new parameters that are added
                // to the schema. These new parameters have to be added to the plan steps.

                // todo: store the original number of parameters and check whether the user gave the correct number here

                for (int parameter = psVars.size(); parameter < schema.parameters().length(); parameter++) {
                    Variable v = schema.parameters().apply(parameter);
                    Variable newVar = new Variable(nextVarId(), psName + v.name(), v.sort());
                    psVars.add(newVar);
                    vctx.addParameter(newVar);
                    // the constraints will be added by the plan
                }

                Seq<Variable> psVarsSeq = psVars.result();
                if (schema.parameters().size() != psVarsSeq.size()) {
                    if (warningOutput)
                        System.out.println("The task schema " + schema.name() + " is defined with " + schema.parameters().size() + " but used with " + psVarsSeq.size() + " parameters.");
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

                // variables for equals constraint
                List<Variable> constrainedVars = new ArrayList<>();
                for (antlrHDDLParser.Var_or_constContext vName : constraint.var_or_const()) {
                    Variable v = getVariable(vName, vctx, constraints, sorts);
                    constrainedVars.add(v);
                }

                // variable and sort for ofSort constraint
                antlrHDDLParser.Typed_varContext typedVarContext = constraint.typed_var();
                Sort sort = null;
                Variable vari = null;
                if (typedVarContext != null) {
                    //vari = getVariableByName(typedVarContext.VAR_NAME(), variables);
                    vari = vctx.getVarByName(typedVarContext.VAR_NAME().toString());
                    String sortName = typedVarContext.var_type().NAME().toString();
                    for (int i = 0; i < sorts.length(); i++) {
                        Sort that = sorts.apply(i);
                        if (that.name().equals(sortName)) {
                            sort = that;
                            break;
                        }
                    }
                    assert (sort != null);
                }

                boolean isEquallity = constraint.equallity() != null;
                assert ((isEquallity && constrainedVars.size() == 2) || (!isEquallity && constrainedVars.size() == 0));


                VariableConstraint vc;
                if (isEquallity) {
                    if (constraint.children.get(1).toString().equals("not")) { // this is an unequal constraint
                        vc = new NotEqual(constrainedVars.get(0), constrainedVars.get(1));
                    } else {// this is an equal constraint
                        vc = new Equal(constrainedVars.get(0), constrainedVars.get(1));
                    }
                } else {
                    // ofSort constraint
                    if (constraint.children.get(1).toString().equals("not")) { // this is an NotOfSort constraint
                        vc = new NotOfSort(vari, sort);
                    } else {// this is an ofSort constraint
                        vc = new OfSort(vari, sort);
                    }
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
                        if (warningOutput)
                            System.out.println("ERROR: The ID \"" + idLeft + "\" is not a subtask ID, but used in the ordering constraints.");
                    } else if (!idMap.containsKey(idRight)) {
                        if (warningOutput)
                            System.out.println("ERROR: The ID \"" + idRight + "\" is not a subtask ID, but used in the ordering constraints.");
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
                    if (warningOutput)
                        System.out.println("The task id " + producerID + " is used in causal link definition, but no task is definied with this id.");
                    report.reportSkippedMethods();
                    continue;
                } else {
                    producer = idMap.get(producerID);
                }

                if (consumerID.toLowerCase().equals("goal")) {
                    consumer = psGoal;
                } else if (!idMap.containsKey(consumerID)) {
                    if (warningOutput)
                        System.out.println("The task id " + consumerID + " is used in causal link definition, but no task is definied with this id.");
                    report.reportSkippedMethods();
                    continue;
                } else {
                    consumer = idMap.get(consumerID);
                }

                Literal literal;

                if (cl.literal().atomic_formula() != null) {
                    literal = visitAtomFormula(vctx, predicates, sorts, constraints, true, cl.literal().atomic_formula());
                } else { // negative literal
                    literal = visitAtomFormula(vctx, predicates, sorts, constraints, false, cl.literal().neg_atomic_formula().atomic_formula());
                }
                causalLinks.add(new CausalLink(producer, consumer, literal));
            }
        }

        CSP csp = new CSP(JavaToScala.toScalaSet(vctx.parameters), constraints.result());
        Plan subPlan = new Plan(planSteps.result(), causalLinks.result(), taskOrderings, csp, psInit, psGoal,
                allowedModifications, allowedFlaws, planStepsDecomposedBy, planStepsDecompositionParents);
        return subPlan;
    }

    private Task visitGoalState(VarContext vctx, seqProviderList<VariableConstraint> constraints, Seq<Sort> sorts, Seq<Predicate> predicates, antlrHDDLParser.P_goalContext ctx) {
        //seqProviderList<VariableConstraint> parameterConstraints = new seqProviderList<VariableConstraint>();
        //seqProviderList<Variable> taskParameters = getVariableForEveryConst(sorts, parameterConstraints);
        //for (Variable v : taskParameters.getList()) {
        //    vctx.addParameter(v);
        //}
        Formula f = new And<Literal>(new Vector<Literal>(0, 0, 0));
        if (ctx != null) {
            f = visitGoalConditions(vctx, predicates, sorts, constraints, ctx.gd());
        }
        return new GeneralTask("goal(<Instance>)", true, JavaToScala.toScalaSeq(vctx.parameters), JavaToScala.toScalaSeq(vctx.parameters), constraints.result(), f, new And<Literal>(new Vector<Literal>(0, 0, 0)));
    }

    private Task visitInitialState(VarContext vctx, seqProviderList<VariableConstraint> constraints, Seq<Sort> sorts, Seq<Predicate> predicates, antlrHDDLParser.P_initContext ctx) {
        seqProviderList<Literal> initEffects = new seqProviderList<>();
        for (antlrHDDLParser.Init_elContext el : ctx.init_el()) {
            if (el.literal() != null) { // normal STRIPS init
                if (el.literal().atomic_formula() != null) {
                    initEffects.add(visitAtomFormula(vctx, predicates, sorts, constraints, true, el.literal().atomic_formula()));
                } else if (el.literal().neg_atomic_formula() != null) {
                    initEffects.add(visitAtomFormula(vctx, predicates, sorts, constraints, false, el.literal().atomic_formula()));
                }
            }
        }

        return new ReducedTask("init(<Instance>)", true, JavaToScala.toScalaSeq(vctx.parameters), JavaToScala.toScalaSeq(vctx.parameters), constraints.result(), new And<Literal>(new Vector<Literal>(0, 0, 0)), new And<Literal>(initEffects.result()));
    }

    private seqProviderList<Variable> getVariableForEveryConst(Seq<Sort> sorts, seqProviderList<VariableConstraint> varConstraints) {
        seqProviderList<Variable> taskParameter = new seqProviderList<>();
        for (int i = 0; i < sorts.length(); i++) {
            Sort s = sorts.apply(i);
            for (int j = 0; j < s.elements().length(); j++) {
                Constant c = s.elements().apply(j);
                Variable v = new Variable(nextVarId(), "varFor" + c.name(), s);

                taskParameter.add(v);
                Equal eq = new Equal(v, c);
                varConstraints.add(eq);
            }
        }
        return taskParameter;
    }

    private Seq<DecompositionMethod> visitMethodDef(List<antlrHDDLParser.Method_defContext> ctx, Seq<Sort> sorts, Seq<Predicate> predicates, Seq<Task> tasks) {
        seqProviderList<DecompositionMethod> methods = new seqProviderList<>();
        HashSet<String> usedMethodNames = new HashSet<>();
        for (antlrHDDLParser.Method_defContext m : ctx) {
            // Read abstract task
            String taskname = m.task_symbol().NAME().toString();
            Task abstractTask = parserUtil.taskByName(taskname, tasks);

            if (abstractTask == null) {
                if (warningOutput)
                    System.out.println("ERROR: compound task given in method definition is undefined: " + taskname);
                continue;
            } else if (abstractTask.isPrimitive()) {
                if (warningOutput)
                    System.out.println("ERROR: compound task given in method definition is not compound, but a primitive task: " + taskname);
                continue;
            }

            // Read method's name and parameters
            String nameStr = m.method_symbol().NAME().toString();
            assert (!usedMethodNames.contains(nameStr));
            usedMethodNames.add(nameStr);
            seqProviderList<Variable> methodParams = typedParamsToVars(sorts, abstractTask.parameters().size(), m.typed_var_list().typed_vars());

            seqProviderList<Variable> tnVars = new seqProviderList<>();
            seqProviderList<VariableConstraint> tnConstraints = new seqProviderList<>();
            tnVars.add(methodParams.result());
            tnVars.add(abstractTask.parameters());

            VarContext vctx = new VarContext();
            for (Variable v : tnVars.getList()) {
                vctx.addParameter(v);
            }

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
                precFormula = visitGoalConditions(vctx, predicates, sorts, constraints, m.gd());
                tnConstraints.add(constraints.result());
                hasPrecondition = true;
            } else {
                hasPrecondition = false;
            }

            boolean hasEffect = false;
            Formula effect = new And<Literal>(new Vector<Literal>(0, 0, 0));
            if (m.effect() != null) {
                effect = visitEffect(vctx, tnConstraints, sorts, predicates, m.effect());
                hasEffect = true;
            }

            // Create subplan, method and add it to method list
            GeneralTask initSchema = new GeneralTask("init(" + nameStr + ")", true,
                    abstractTask.parameters(),
                    abstractTask.parameters(),
                    new Vector<VariableConstraint>(0, 0, 0),
                    new And<Literal>(new Vector<Literal>(0, 0, 0)),
                    abstractTask.precondition());
            GeneralTask goalSchema = new GeneralTask("goal(" + nameStr + ")", true,
                    abstractTask.parameters(),
                    abstractTask.parameters(),
                    new Vector<VariableConstraint>(0, 0, 0),
                    abstractTask.effect(),
                    new And<Literal>(new Vector<Literal>(0, 0, 0)));

            PlanStep psInit = new PlanStep(-1, initSchema, abstractTask.parameters());
            PlanStep psGoal = new PlanStep(-2, goalSchema, abstractTask.parameters());

            Plan subPlan = visitTaskNetwork(
                    m.tasknetwork_def(),
                    vctx,
                    tnConstraints,
                    psInit,
                    psGoal,
                    tasks,
                    predicates,
                    sorts,
                    NoModifications$.MODULE$,
                    NoFlaws$.MODULE$,
                    hddlPanda3Visitor.planStepsDecomposedBy,
                    hddlPanda3Visitor.planStepsDecompositionParents);

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
                if (warningOutput)
                    System.out.println("ERROR: not yet implemented - a const is used in task definition");
            }

            Variable methodVar = parserUtil.getVarByName(methodParams, param.VAR_NAME().toString());
            if (methodVar == null) {
                if (warningOutput)
                    System.out.println("ERROR: parameter used in method definition has not been found in method's parameter definition. (abstract task " + abstractTask.name() + ")");
                paramsOk = false;
                break;
            }
            Variable taskVar = abstractTask.parameters().apply(i);
            VariableConstraint vc = new Equal(taskVar, methodVar);
            tnConstraints.add(vc);
        }
        return paramsOk;
    }

    HashSet<String> usedTaskNames;

    private Seq<Task> visitTaskDefs(Seq<Sort> sorts, Seq<Predicate> predicates, antlrHDDLParser.DomainContext ctxDomain) {
        usedTaskNames = new HashSet<>();
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
        assert (!usedTaskNames.contains(taskName));
        usedTaskNames.add(taskName);
        seqProviderList<Variable> parameters = typedParamsToVars(sorts, 0, ctxTask.typed_var_list().typed_vars());
        seqProviderList<VariableConstraint> constraints = new seqProviderList<>();

        // due to constants in the definition, this may increase. However, we want to know the original count.
        int numOfParams = parameters.size();

        VarContext vctx = new VarContext();
        for (Variable v : parameters.getList()) {
            vctx.addParameter(v);
        }

        // build preconditions
        Formula f = new And<Literal>(new Vector<Literal>(0, 0, 0));
        if (ctxTask.gd() != null) {
            f = visitGoalConditions(vctx, predicates, sorts, constraints, ctxTask.gd());
        }

        // build effects
        // todo: implement forall- and conditional effects

        Formula f2 = new And<Literal>(new Vector<Literal>(0, 0, 0));

        if (ctxTask.effect() != null) {
            f2 = visitEffect(vctx, constraints, sorts, predicates, ctxTask.effect());
        }
        return new GeneralTask(
                taskName,
                isPrimitive,
                JavaToScala.toScalaSeq(vctx.parameters),
                JavaToScala.toScalaSeq(vctx.parameters).toVector().take(numOfParams),
                constraints.result(), f, f2);
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
            bParameters.add(new Variable(nextVarId(), varNames.apply(i), varSorts.apply(i)));
        }
        return bParameters;
    }

    /**
     * Dispatcher for parsing goal descriptions (GD). Please be aware that these descriptions are used at several
     * places in the grammar, e.g. as precondition of actions, tasks or methods and not just as goal condition.
     */
    private Formula visitGoalConditions(
            VarContext vctx,
            Seq<Predicate> predicates,
            //seqProviderList<Variable> parameters,
            Seq<Sort> sorts,
            seqProviderList<VariableConstraint> constraints,
            antlrHDDLParser.GdContext ctx) {
        if (ctx.atomic_formula() != null) { // a single precondition
            return visitAtomFormula(vctx, predicates, sorts, constraints, true, ctx.atomic_formula());
        } else if (ctx.gd_negation() != null) { // a negated single precondition
            return visitNegAtomFormula(vctx, predicates, sorts, constraints, ctx.gd_negation());
        } else if (ctx.gd_conjuction() != null) { // a conduction of preconditions
            return visitGdConOrDisjunction(conOrDis.and, vctx, predicates, sorts, constraints, ctx.gd_conjuction().gd());
        } else if (ctx.gd_universal() != null) {
            return visitUniversalQuantifier(vctx, predicates, sorts, constraints, ctx.gd_universal());
        } else if (ctx.gd_existential() != null) {
            return visitExistentialQuantifier(vctx, predicates, sorts, constraints, ctx.gd_existential());
        } else if (ctx.gd_disjuction() != null) { // well ...
            return visitGdConOrDisjunction(conOrDis.or, vctx, predicates, sorts, constraints, ctx.gd_disjuction().gd());
        } else if (ctx.gd_implication() != null) { // new
            return visitImplication(vctx, predicates, sorts, constraints, ctx.gd_implication());
        } else if (ctx.gd_equality_constraint() != null) {
            return visitEqConstraint(vctx, sorts, constraints, ctx);
        } else if (ctx.gd_empty() != null) {
            return new And<Literal>(new Vector<Literal>(0, 0, 0));
        } else {
            if (warningOutput) System.out.println(ctx.getText());
            throw new IllegalArgumentException("ERROR: Feature in Precondition is not implemented");
        }
    }

    private Formula visitEqConstraint(VarContext vctx, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, antlrHDDLParser.GdContext ctx) {
        Variable var1 = getVariable(ctx.gd_equality_constraint().var_or_const(0), vctx, constraints, sorts);
        Variable var2 = getVariable(ctx.gd_equality_constraint().var_or_const(1), vctx, constraints, sorts);
        Equal ne = new Equal(var1, var2);
        constraints.add(ne);
        return new And<Literal>(new Vector<Literal>(0, 0, 0));
    }

    private Formula visitExistentialQuantifier(
            VarContext vctx,
            Seq<Predicate> predicates,
            Seq<Sort> sorts,
            seqProviderList<VariableConstraint> constraints,
            antlrHDDLParser.Gd_existentialContext gd_existentialContext) {
        Tuple2<Seq<Variable>, Formula> inner = readInner(
                vctx.child(), predicates, sorts, constraints,
                gd_existentialContext.typed_var_list().typed_vars(), gd_existentialContext.gd());
        return Exists.apply(inner._1, inner._2);
    }

    private Formula visitUniversalQuantifier(
            VarContext vctx,
            Seq<Predicate> predicates,
            Seq<Sort> sorts,
            seqProviderList<VariableConstraint> constraints,
            antlrHDDLParser.Gd_universalContext gd_conjuctionContext) {
        Tuple2<Seq<Variable>, Formula> inner = readInner(
                vctx.child(), predicates, sorts, constraints,
                gd_conjuctionContext.typed_var_list().typed_vars(), gd_conjuctionContext.gd());
        return Forall.apply(inner._1, inner._2);
    }

    private Formula visitImplication(
            VarContext vctx,
            Seq<Predicate> predicates,
            Seq<Sort> sorts,
            seqProviderList<VariableConstraint> constraints,
            antlrHDDLParser.Gd_implicationContext gd_implicationContext) {
        return new Implies(
                visitGoalConditions(vctx, predicates, sorts, constraints, gd_implicationContext.gd(0)),
                visitGoalConditions(vctx, predicates, sorts, constraints, gd_implicationContext.gd(1)));
    }


    private Tuple2<Seq<Variable>, Formula> readInner(
            VarContext vctx,
            Seq<Predicate> predicates,
            Seq<Sort> sorts,
            seqProviderList<VariableConstraint> constraints,
            List<antlrHDDLParser.Typed_varsContext> typed_varsContexts,
            antlrHDDLParser.GdContext gd) {

        seqProviderList<Variable> quantifiedVars = new seqProviderList<>();

        VectorBuilder<Sort> newSorts = new VectorBuilder<>();
        VectorBuilder<String> newVarnames = new VectorBuilder<>();
        visitTypedList(newSorts, newVarnames, sorts, typed_varsContexts);

        Seq<Sort> newS = newSorts.result();
        Seq<String> newN = newVarnames.result();

        for (int i = 0; i < newN.size(); i++) {
            Variable v = new Variable(nextVarId(), newN.apply(i), newS.apply(i));
            quantifiedVars.add(v);
            vctx.addQuantifiedVar(v);
        }

        Formula inner = visitGoalConditions(vctx, predicates, sorts, constraints, gd);
        return new Tuple2<>(quantifiedVars.result(), inner);
    }

    private Tuple2<Seq<Variable>, Formula> readInnerEffect(
            VarContext vctx,
            Seq<Predicate> predicates,
            Seq<Sort> sorts,
            seqProviderList<VariableConstraint> constraints,
            List<antlrHDDLParser.Typed_varsContext> typed_varsContexts,
            antlrHDDLParser.EffectContext ctx) {

        seqProviderList<Variable> quantifiedVars = new seqProviderList<>();

        VectorBuilder<Sort> newSorts = new VectorBuilder<>();
        VectorBuilder<String> newVarnames = new VectorBuilder<>();
        visitTypedList(newSorts, newVarnames, sorts, typed_varsContexts);


        Seq<Sort> newS = newSorts.result();
        Seq<String> newN = newVarnames.result();

        for (int i = 0; i < newN.size(); i++) {
            Variable v = new Variable(nextVarId(), newN.apply(i), newS.apply(i));
            quantifiedVars.add(v);
            vctx.addQuantifiedVar(v);
        }

        Formula inner = visitEffect(vctx, constraints, sorts, predicates, ctx);
        return new Tuple2<>(quantifiedVars.result(), inner);
    }

    private Formula visitEffect(
            VarContext vctx,
            seqProviderList<VariableConstraint> constraints,
            Seq<Sort> sorts,
            Seq<Predicate> predicates,
            antlrHDDLParser.EffectContext ctx) {
        if (ctx.literal() != null) {
            if (ctx.literal().atomic_formula() != null) {
                return visitAtomFormula(vctx, predicates, sorts, constraints, true, ctx.literal().atomic_formula());
            } else if (ctx.literal().neg_atomic_formula() != null) {
                return visitAtomFormula(vctx, predicates, sorts, constraints, false, ctx.literal().neg_atomic_formula().atomic_formula());
            }
        } else if (ctx.eff_empty() != null) {
            return new And<Literal>(new Vector<Literal>(0, 0, 0));
        } else if (ctx.eff_conjunction() != null) {
            return new And<>(JavaToScala.toScalaSeq(ctx.eff_conjunction().effect().stream().map(x ->
                    visitEffect(vctx, constraints, sorts, predicates, x)
            ).collect(Collectors.toList())));
        } else if (ctx.eff_universal() != null) {
            Tuple2<Seq<Variable>, Formula> inner = readInnerEffect(
                    vctx.child(), predicates, sorts, constraints,
                    ctx.eff_universal().typed_var_list().typed_vars(), ctx.eff_universal().effect());
            return Forall.apply(inner._1, inner._2);
        } else if (ctx.eff_conditional() != null) {
            return new When(
                    visitGoalConditions(vctx, predicates, sorts, constraints, ctx.eff_conditional().gd()),
                    visitEffect(vctx, constraints, sorts, predicates, ctx.eff_conditional().effect()));
        } else if (ctx.p_effect() != null) {
            this.report.reportNumericEffect();
        } else if (ctx.eff_empty() != null && warningOutput) {
            System.out.println("ERROR: found an empty effect in action declaration.");
        } else if (warningOutput) {
            System.out.println("ERROR: unexpected token in effect declaration");
        }
        return null;
    }

    enum conOrDis {
        or, and
    }

    /**
     * Parse a conjunction or disjunction of goal conditions
     */
    private Formula visitGdConOrDisjunction(conOrDis what, VarContext vctx, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, List<antlrHDDLParser.GdContext> ctx) {
        seqProviderList<Formula> elements = new seqProviderList<>();
        for (antlrHDDLParser.GdContext gd : ctx) {
            Formula f = visitGoalConditions(vctx, predicates, sorts, constraints, gd);
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
    private Formula visitNegAtomFormula(VarContext vctx, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, antlrHDDLParser.Gd_negationContext ctx) {
        if (ctx.gd().gd_equality_constraint() != null) {
            Variable var1 = getVariable(ctx.gd().gd_equality_constraint().var_or_const(0), vctx, constraints, sorts);
            Variable var2 = getVariable(ctx.gd().gd_equality_constraint().var_or_const(1), vctx, constraints, sorts);
            NotEqual ne = new NotEqual(var1, var2);
            constraints.add(ne);
            return new And<Literal>(new Vector<Literal>(0, 0, 0));
        } else {
            Formula inner = visitGoalConditions(vctx, predicates, sorts, constraints, ctx.gd());
            return new Not(inner);
        }
    }

    /**
     * @param vctx
     * @param predicates
     * @param sorts
     * @param constraints
     * @param isPositive
     * @param ctx
     * @return
     */
    private Literal visitAtomFormula(VarContext vctx, Seq<Predicate> predicates, Seq<Sort> sorts, seqProviderList<VariableConstraint> constraints, boolean isPositive, antlrHDDLParser.Atomic_formulaContext ctx) {
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
            if (warningOutput)
                System.out.println("ERROR: a precondition or effect in action definition refers to predicate \"" + predName + "\" that can't be found in predicate declaration.");
        }

        //
        // get variable definition
        //
        seqProviderList<Variable> parameterVariables = new seqProviderList<>();
        List<antlrHDDLParser.Var_or_constContext> params = ctx.var_or_const();

        for (antlrHDDLParser.Var_or_constContext param : params) {
            Variable var = getVariable(param, vctx, constraints, sorts);
            parameterVariables.add(var);
        }

        return new Literal(predicate, isPositive, parameterVariables.result());
    }


    /**
     * This method gets the parse tree of a variable and returns a variable that represents it in the given
     * context.
     *
     * @param variableName (in) The parse tree containing the object or variable
     * @param parameters   (in/out) The parameter list in the given context (e.g. action/method parameters).
     *                     It may be updated
     * @return Method returns a variable that may be out of the parameter list, or the initial state definition
     */
    private Variable getVariableByName(TerminalNode variableName, seqProviderList<Variable> parameters) {
        Variable var = null;
        // this is a variable
        String pname = variableName.getText();
        for (int i = 0; i < parameters.size(); i++) {
            Variable v = parameters.get(i);
            if (v.name().equals(pname)) {
                var = v;
                break;
            }
        }
        return var;
    }

    /**
     * This method gets the parse tree of a variable or constant and returns a variable that represents it in the given
     * context. There are several cases to distinguish: ir may be (1) a variable in the given context (e.g. a parameter
     * of an action), a (2) an object that has already been defined in s0 or (3) a constant in method or action
     * definition, than a new variable is introduced, bound it to the constant, and returned
     * // add it to the parameter-list
     *
     * @param param       (in) The parse tree containing the object or variable
     * @param vctx        (in/out) The parameter list / quantified variables in the given context
     *                    (e.g. action/method parameters). It may be updated
     * @param constraints (in/out) The constraint network in the given context. It may be updated
     * @param sorts       (in) The sort hierarchy that also contains constants for every sort.
     * @return Method returns a variable that may be out of the parameter list, or the initial state definition
     */
    private Variable getVariable(antlrHDDLParser.Var_or_constContext param, VarContext vctx, seqProviderList<VariableConstraint> constraints, Seq<Sort> sorts) {
        Variable var = null;
        if (param.VAR_NAME() != null) {
            // this is a variable
            String pname = param.VAR_NAME().getText();
            var = vctx.getVarByName(pname);
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
                            var = new Variable(nextVarId(), "varToConst" + c, s);
                            vctx.parameters.add(var);
                            constraints.add(new Equal(var, c));
                            break loopGetConst;
                        }
                    }
                }
            }
        }
        if (var == null) {
            if (warningOutput)
                System.out.println("ERROR: The variable name \"" + param.getText() + "\" is used in a precondition or effect definition, but is not defined in the actions parameter definition.");
            if (warningOutput)
                System.out.println("Maybe it is a constant, then it is not your fault, but just a not yet implemented feature, but anyway...");
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
                if (warningOutput)
                    System.out.println("ERROR: " + ErrorMsgWhoIsReader + " refers to sort \"" + sortName + "\" that can't be found in sort declaration.");
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

        antlrHDDLParser.Type_def_listContext typeDefList = ctxDomain.type_def().type_def_list();

        // types with parents
        while (typeDefList.new_types() != null) {
            antlrHDDLParser.New_typesContext newTypes = typeDefList.new_types();
            assert (typeDefList.var_type() != null);
            final String parent_type = typeDefList.var_type().NAME().toString();
            for (int j = 0; j < newTypes.getChildCount(); j++) {
                final String child_type = newTypes.NAME(j).toString();
                internalSortModel.addParent(child_type, parent_type);
            }
            // recursion
            typeDefList = typeDefList.type_def_list();
        }
        // types without parent
        for (TerminalNode singletonType : typeDefList.NAME())
            internalSortModel.addParent(singletonType.toString(), ARTIFICIAL_ROOT_SORT);


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

    // Note: names of parameters or quantified variables can not be rebound:
    // ∀x:∀x:P(x) is not allowed
    //
    public static class VarContext {
        public java.util.List<Variable> parameters = new ArrayList<>();
        private java.util.List<Variable> quantifiedVars = new ArrayList<>();

        public VarContext child() {
            VarContext child = new VarContext();

            // defensive copy
            child.quantifiedVars = new ArrayList<>(quantifiedVars);

            // changes to the parameters of the child also have to affect the parent
            child.parameters = parameters;

            return child;
        }

        public Variable getVarByName(String name) {
            for (Variable u : parameters) {
                if (u.name().equals(name)) {
                    return u;
                }
            }
            for (Variable u : quantifiedVars) {
                if (u.name().equals(name)) {
                    return u;
                }
            }
            return null;
        }

        public Variable getVarById(int id) {
            for (Variable u : parameters) {
                if (u.id() == id) {
                    return u;
                }
            }
            for (Variable u : quantifiedVars) {
                if (u.id() == id) {
                    return u;
                }
            }
            return null;
        }

        public void addParameter(Variable v) {
            if (getVarById(v.id()) != null) {
                throw new RuntimeException("A  variable with id " + v.id() + " does already exist in this context!");
            }
            // name duplicates do not matter for actual and artificial parameters
            parameters.add(v);
        }

        public void addParamaters(List<Variable> vs) {
            for (Variable v : vs) {
                addParameter(v);
            }
        }

        public void addQuantifiedVar(Variable v) {
            if (getVarById(v.id()) != null) {
                throw new RuntimeException("A variable with id " + v.id() + " does already exist in this context!");
            }
            if (getVarByName(v.name()) != null) {
                throw new RuntimeException("A variable with name " + v.name() + " does already exist in this context!");
            }
            quantifiedVars.add(v);
        }
    }
}
