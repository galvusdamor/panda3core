package de.uniulm.ki.panda3.symbolic.compiler.prefix;

import de.uniulm.ki.panda3.symbolic.compiler.DomainTransformer;
import de.uniulm.ki.panda3.symbolic.csp.Equal;
import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint;
import de.uniulm.ki.panda3.symbolic.domain.*;
import de.uniulm.ki.panda3.symbolic.domain.updates.*;
import de.uniulm.ki.panda3.symbolic.logic.*;
import de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel.internalTaskNetwork;
import de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel.seqProviderList;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.SymbolicPlan;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.panda3.util.JavaToScala;
import scala.Option;
import scala.Tuple2;
import scala.Unit;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PrefixTransformer implements DomainTransformer<Unit> {
    public static Option<DecompositionMethod> noneForMethod = (Option<DecompositionMethod>) (Object) scala.None$.MODULE$;
    public static Option<PlanStep> noneForPlanStep = (Option<PlanStep>) (Object) scala.None$.MODULE$;

    private String uniqueStringPrefix = "n";

    private seqProviderList<Predicate> newPredicates = new seqProviderList<>();
    private seqProviderList<Task> newPrimitiveTasks = new seqProviderList<>();
    private ArrayList<Task> newCompoundTasks = new ArrayList<Task>();
    private Task processTaskSchema;
    private HashMap<Task, Task> map_M_t = new HashMap<Task, Task>();

    private String[][] prefixNameArguments;
    private String[][] processLiteralsValues;

    private programTasks whatToDo;

    enum programTasks {
        repair,
        recognition
    }

    private PrefixTransformer() {
    }

    public static PrefixTransformer getRepairTransformer(String[][] prefix, String[][] process) {
        PrefixTransformer res = new PrefixTransformer();
        res.prefixNameArguments = prefix;
        res.processLiteralsValues = process;
        res.whatToDo = programTasks.repair;
        return res;
    }

    public static PrefixTransformer getRecognitionTransformer(String[][] prefix) {
        PrefixTransformer res = new PrefixTransformer();
        res.prefixNameArguments = prefix;
        res.whatToDo = programTasks.recognition;
        return res;
    }

    @Override
    public Tuple2<Domain, Plan> transform(Domain domain, Plan plan, Unit info) {
        Tuple2<Domain, Plan> domPlan = new Tuple2<Domain, Plan>(domain, plan);
        try {
            domPlan = transformStep_L_x(domPlan);
            domPlan = transformStep_O_x_und_p_mit_alpha(domPlan);
            domPlan = transformStep_C_x_und_generateMapMt(domPlan);
            domPlan = transformStep_M_t(domPlan, true);
            domPlan = transformStep_alpha_O(domPlan);
            domPlan = transformStep_M_x(domPlan);
            domPlan = transformStep_s_i(domPlan);
            domPlan = transformStep_M_t(domPlan, false);
        } catch (addPrefixException e) {
            e.printStackTrace();
        }
        return domPlan;
    }

    @Override
    public Tuple2<Domain, Plan> transform(Tuple2<Domain, Plan> domainAndPlan, Unit info) {
        return transform(domainAndPlan._1(), domainAndPlan._2(), info);
    }

    @Override
    public Tuple2<Domain, Plan> apply(Domain domain, Plan plan, Unit info) {
        return null;
    }

    @Override
    public Tuple2<Domain, Plan> apply(Tuple2<Domain, Plan> domainAndPlan, Unit info) {
        return null;
    }

    /**
     * L_x is the set of new literals that is added to the preconditions to enforce the ordering of the prefix.
     * The number of literals differs in the cases of (1) repair and (2) recognition
     */
    private Tuple2<Domain, Plan> transformStep_L_x(Tuple2<Domain, Plan> domPlan) {
        String prefixExtension = "[L]";

        int predicateCount;
        if (this.whatToDo == programTasks.repair) {
            predicateCount = prefixNameArguments.length + 2;
        } else {
            predicateCount = prefixNameArguments.length + 1;
        }

        for (int a = 1; a <= predicateCount; a++) {
            String tempName = uniqueStringPrefix + prefixExtension + a;
            Predicate tempPredicate = new Predicate(tempName, JavaToScala.<Sort>nil());
            newPredicates.add(tempPredicate);
        }

        AddPredicate updateObject = new AddPredicate(newPredicates.result());
        return new Tuple2<Domain, Plan>(domPlan._1().update(updateObject), domPlan._2());
    }


    /**
     * O_x is the set of new actions that represent the elements in the prefix. p is the process that is added in plan repair.
     * <p/>
     * todo: Daniel says: do not really know why the alpha is in the title.
     */
    private Tuple2<Domain, Plan> transformStep_O_x_und_p_mit_alpha(Tuple2<Domain, Plan> domPlan) throws addPrefixException {
        String prefixExtension = "[O]";

        for (int a = 1; a <= prefixNameArguments.length; a++) {
            String tempName = uniqueStringPrefix + prefixExtension + a;
            Task tempRelatedTask = getTaskFromDomainByName(domPlan._1(), prefixNameArguments[a - 1][0]);

            ArrayList<Formula> tempPrecLiteralList = new ArrayList<Formula>();
            Literal tempPrecLiteral = new Literal(newPredicates.get(a - 1), true, JavaToScala.<Variable>nil());
            tempPrecLiteralList.add(tempPrecLiteral);

            ArrayList<Formula> tempEffektLiteralList = new ArrayList<Formula>();
            Literal tempAddLiteral = new Literal(newPredicates.get(a), true, JavaToScala.<Variable>nil());
            Literal tempDelLiteral = new Literal(newPredicates.get(a - 1), false, JavaToScala.<Variable>nil());
            tempEffektLiteralList.add(tempAddLiteral);
            tempEffektLiteralList.add(tempDelLiteral);

            ArrayList<VariableConstraint> tempNewPrimitivesVariableConstraints = new ArrayList<VariableConstraint>();

            for (int b = 0; b < tempRelatedTask.parameters().size(); b++) {
                Constant tempConstant = getConstantFromDomainByName(domPlan._1(), prefixNameArguments[a - 1][b + 1]);

                tempNewPrimitivesVariableConstraints.add(new Equal(tempRelatedTask.parameters().apply(b), tempConstant));
            }

            Task tempNewPrimitiveTask = new GeneralTask(tempName, true, tempRelatedTask.parameters(),
                    JavaToScala.concatJavaListToScalaSeq(tempRelatedTask.parameterConstraints(), tempNewPrimitivesVariableConstraints),
                    new And<Formula>(JavaToScala.concatJavaListToScalaSeq(JavaToScala.toScalaSeq(tempRelatedTask.precondition()), tempPrecLiteralList)),
                    new And<Formula>(JavaToScala.concatJavaListToScalaSeq(JavaToScala.toScalaSeq(tempRelatedTask.effect()), tempEffektLiteralList)));
            newPrimitiveTasks.add(tempNewPrimitiveTask);
        }

        // create process
        if (this.whatToDo == programTasks.repair) {
            String tempName = uniqueStringPrefix + prefixExtension + "p";

            ArrayList<Literal> tempPrecLiteralList = new ArrayList<Literal>();
            Literal tempPrecLiteral = new Literal(newPredicates.get(prefixNameArguments.length), true, JavaToScala.<Variable>nil());
            tempPrecLiteralList.add(tempPrecLiteral);

            ArrayList<Literal> tempEffektLiteralList = new ArrayList<Literal>();
            Literal tempAddLiteral = new Literal(newPredicates.get(prefixNameArguments.length + 1), true, JavaToScala.<Variable>nil());
            Literal tempDelLiteral = new Literal(newPredicates.get(prefixNameArguments.length), false, JavaToScala.<Variable>nil());
            tempEffektLiteralList.add(tempAddLiteral);
            tempEffektLiteralList.add(tempDelLiteral);


            ArrayList<VariableConstraint> tempProcessVariableConstraints = new ArrayList<VariableConstraint>();
            ArrayList<Variable> tempProcessVariables = new ArrayList<Variable>();

            int tempId = 0;

            for (int a = 0; a < processLiteralsValues.length; a++) {
                boolean positive = processLiteralsValues[a][0].charAt(0) == '+';
                processLiteralsValues[a][0] = processLiteralsValues[a][0].substring(1);

                Predicate tempPredicate = getPredicateFromDomainByName(domPlan._1(), processLiteralsValues[a][0]);

                ArrayList<Variable> tempProcessLiteralVariables = new ArrayList<Variable>();
                for (int b = 1; b < processLiteralsValues[a].length; b++) {
                    tempId++;
                    Variable tempVariable = new Variable(tempId, uniqueStringPrefix + "[V]" + "p_" + tempId, tempPredicate.argumentSorts().apply(b - 1));
                    Constant tempConstant = getConstantFromDomainByName(domPlan._1(), processLiteralsValues[a][b]);

                    tempProcessLiteralVariables.add(tempVariable);
                    tempProcessVariables.add(tempVariable);

                    tempProcessVariableConstraints.add(new Equal(tempVariable, tempConstant));
                }

                Literal tempProcessLiteral = new Literal(getPredicateFromDomainByName(domPlan._1(), processLiteralsValues[a][0]), positive, JavaToScala.toScalaSeq(tempProcessLiteralVariables));
                tempEffektLiteralList.add(tempProcessLiteral);
            }

            processTaskSchema = new ReducedTask(tempName, true, JavaToScala.toScalaSeq(tempProcessVariables), JavaToScala.toScalaSeq(tempProcessVariableConstraints),
                    new And<Literal>(JavaToScala.toScalaSeq(tempPrecLiteralList)), new And<Literal>(JavaToScala.toScalaSeq(tempEffektLiteralList)));
        }
        AddTask updateObject = new AddTask(newPrimitiveTasks.result());
        return new Tuple2<Domain, Plan>(domPlan._1().update(updateObject), domPlan._2());
    }

    /**
     * C_o is the set of new compound tasks - each represents a primitive task. The primitive tasks in the original
     * domain are replace by these tasks. Newly introduced methods decompose them either in the original primitive task
     * or in a (corresponding) action that is included in the enforced prefix.
     */
    private Tuple2<Domain, Plan> transformStep_C_x_und_generateMapMt(Tuple2<Domain, Plan> domPlan) throws addPrefixException {
        String prefixExtension = "[C]";

        for(String oName : getPrimitivesFromPrefixDistinct()){
            String tName = uniqueStringPrefix + prefixExtension + oName;
            Task relatedTask = getTaskFromDomainByName(domPlan._1(), oName);
            Task newCompoundTask = new GeneralTask(tName, false, relatedTask.parameters(), relatedTask.parameterConstraints(),
                    relatedTask.precondition(), relatedTask.effect());
            newCompoundTasks.add(newCompoundTask);
            map_M_t.put(relatedTask, newCompoundTask);
        }

        AddTask updateObject = new AddTask(JavaToScala.toScalaSeq(newCompoundTasks));
        return new Tuple2<Domain, Plan>(domPlan._1().update(updateObject), domPlan._2());
    }

    /**
     * The last new predicate is the precondition of all original actions in the domain, it is set by the effect of the
     * process or of the last action in the prefix.
     */
    private Tuple2<Domain, Plan> transformStep_alpha_O(Tuple2<Domain, Plan> domPlan) throws addPrefixException {
        HashMap<Task, Task> tempMap = new HashMap<Task, Task>();

        // new part of the precondition
        ArrayList<Formula> tempPrecLiteralList = new ArrayList<Formula>();
        Literal tempPrecLiteral = new Literal(newPredicates.get(newPredicates.size() - 1), true, JavaToScala.<Variable>nil());
        tempPrecLiteralList.add(tempPrecLiteral);

        for (int a = 0; a < domPlan._1().tasks().size(); a++) {
            Task tempCurrentTask = domPlan._1().tasks().apply(a);
            if (!tempCurrentTask.isPrimitive()) {
                continue;
            } else if (tempCurrentTask.name().startsWith(uniqueStringPrefix + "[O]")) {
                continue;
            } else {
                Task tempNewPrimitiveTask = new GeneralTask(tempCurrentTask.name(), tempCurrentTask.isPrimitive(), tempCurrentTask.parameters(), tempCurrentTask.parameterConstraints(),
                        new And(JavaToScala.concatJavaListToScalaSeq(JavaToScala.toScalaSeq(tempCurrentTask.precondition()), tempPrecLiteralList)), tempCurrentTask.effect());
                tempMap.put(tempCurrentTask, tempNewPrimitiveTask);
            }
        }

        scala.collection.immutable.Map<Task, Task> tempScalaMap = JavaToScala.toScalaMap(tempMap);
        ExchangeTask updateObject = new ExchangeTask(tempScalaMap);

        return new Tuple2<Domain, Plan>(domPlan._1().update(updateObject), domPlan._2());
    }

    /**
     * The set M_t includes exactly the methods that have been in the original domain, but with the following change:
     * When there has been an action in the sub-task network, it is replaced by the newly introduced abstract task
     * (out of C_x) that corresponds to it.
     */
    private Tuple2<Domain, Plan> transformStep_M_t(Tuple2<Domain, Plan> domPlan, boolean useDomain) throws addPrefixException {
        scala.collection.immutable.Map<Task, Task> tempScalaMap = JavaToScala.toScalaMap(map_M_t);
        ExchangeTaskSchemaInMethods updateObject = new ExchangeTaskSchemaInMethods(tempScalaMap);
        Domain domain = domPlan._1();
        Plan plan = domPlan._2();
        if (useDomain) {
            domain = domain.update(updateObject);
        } else {
            plan = plan.update(updateObject);
        }
        return new Tuple2<Domain, Plan>(domain, plan);
    }

    /**
     * M_x is a set of methods, it is a union of three sets. The first one contains methods that decompose the newly
     * introduced abstract tasks in C_x into their corresponding actions, i.e. it makes the old decompositions
     * reachable. The second set includes methods that make (but the last, see 3rd set) the observations reachable via
     * decomposition. The third set includes a single method for the type of the last task in the prefix. In
     * recognition it is the same as the methods in the 2nd set, but in repair it adds also the process.
     */
    private Tuple2<Domain, Plan> transformStep_M_x(Tuple2<Domain, Plan> domPlan) throws addPrefixException {
        ArrayList<DecompositionMethod> newMethods = new ArrayList<DecompositionMethod>();

        for (Map.Entry<Task, Task> entry : map_M_t.entrySet()) {
            Task tempPrimitiveTask = getTaskFromDomainByName(domPlan._1(), entry.getKey().name());
            Task tempCompoundTask = entry.getValue();

            PlanStep tempPlanStepInit = getEmptyPlanStep(1, new And<>(JavaToScala.<Literal>nil()), tempCompoundTask.precondition(), tempCompoundTask.parameters());
            PlanStep tempPlanStepGoal = getEmptyPlanStep(3, tempCompoundTask.effect(), new And<>(JavaToScala.<Literal>nil()), tempCompoundTask.parameters());


            PlanStep tempPlanStepO = new PlanStep(2, tempPrimitiveTask, tempPrimitiveTask.parameters(), noneForMethod, noneForPlanStep);

            internalTaskNetwork tempInternalTaskNetwork = new internalTaskNetwork();
            tempInternalTaskNetwork.addPlanStep(tempPlanStepInit);
            tempInternalTaskNetwork.addPlanStep(tempPlanStepO);
            tempInternalTaskNetwork.addPlanStep(tempPlanStepGoal);
            tempInternalTaskNetwork.addOrdering(tempPlanStepInit, tempPlanStepO);
            tempInternalTaskNetwork.addOrdering(tempPlanStepO, tempPlanStepGoal);

            SymbolicPlan tempSubPlan = new SymbolicPlan(tempInternalTaskNetwork.planSteps(), tempInternalTaskNetwork.causalLinks(), tempInternalTaskNetwork.taskOrderings(),
                    tempInternalTaskNetwork.csp().addVariables(tempPlanStepO.arguments()), tempPlanStepInit, tempPlanStepGoal);

            DecompositionMethod tempDecompositionMethod = new SimpleDecompositionMethod(tempCompoundTask, tempSubPlan);
            newMethods.add(tempDecompositionMethod);
        }

        String[] primitivesFromPrefixDistinct = getPrimitivesFromPrefixDistinct();

        // add a new compound task for every type of primitive task that is included in the prefix
        for (int a = 0; a < newPrimitiveTasks.size(); a++) {
            Task currentNewPrimitiveTask = newPrimitiveTasks.get(a);
            Task correspondingCompoundTask = null;

            for (int b = 0; b < primitivesFromPrefixDistinct.length; b++) {
                if (prefixNameArguments[a][0].equals(primitivesFromPrefixDistinct[b])) {
                    correspondingCompoundTask = newCompoundTasks.get(b);
                    break;
                }
            }

            PlanStep psInit = getEmptyPlanStep(1, new And<>(JavaToScala.<Literal>nil()), correspondingCompoundTask.precondition(), correspondingCompoundTask.parameters());
            PlanStep psGoal = getEmptyPlanStep(4, correspondingCompoundTask.effect(), new And<>(JavaToScala.<Literal>nil()), correspondingCompoundTask.parameters());
            PlanStep tempPlanStep_O = new PlanStep(2, currentNewPrimitiveTask, currentNewPrimitiveTask.parameters(), noneForMethod, noneForPlanStep);

            internalTaskNetwork subnetwork = new internalTaskNetwork();
            subnetwork.addPlanStep(psInit);
            subnetwork.addPlanStep(tempPlanStep_O);
            subnetwork.addPlanStep(psGoal);
            subnetwork.addOrdering(psInit, tempPlanStep_O);
            subnetwork.addOrdering(tempPlanStep_O, psGoal);

            SymbolicPlan tempSubPlan = null;

            // In case of plan repair, the process is added, but not in recognition
            if ((whatToDo == programTasks.repair) && (a == newPrimitiveTasks.size() - 1)) {
                PlanStep psProcess = null;
                psProcess = new PlanStep(3, processTaskSchema, processTaskSchema.parameters(), noneForMethod, noneForPlanStep);
                subnetwork.addPlanStep(psProcess);
                subnetwork.addOrdering(tempPlanStep_O, psProcess);
                subnetwork.addOrdering(psProcess, psGoal);

                tempSubPlan = new SymbolicPlan(subnetwork.planSteps(), subnetwork.causalLinks(), subnetwork.taskOrderings(),
                        subnetwork.csp().addVariables(tempPlanStep_O.arguments()).addVariables(psProcess.arguments()),
                        psInit, psGoal);

            } else {
                subnetwork.addOrdering(tempPlanStep_O, psGoal);

                tempSubPlan = new SymbolicPlan(subnetwork.planSteps(), subnetwork.causalLinks(), subnetwork.taskOrderings(),
                        subnetwork.csp().addVariables(tempPlanStep_O.arguments()),
                        psInit, psGoal);
            }

            DecompositionMethod tempDecompositionMethod = new SimpleDecompositionMethod(correspondingCompoundTask, tempSubPlan);
            newMethods.add(tempDecompositionMethod);
        }

        AddMethod updateObject = new AddMethod(JavaToScala.toScalaSeq(newMethods));
        return new Tuple2<Domain, Plan>(domPlan._1().update(updateObject), domPlan._2());
    }

    private Tuple2<Domain, Plan> transformStep_s_i(Tuple2<Domain, Plan> domPlan) throws addPrefixException {
        Literal tempPrecLiteral = new Literal(newPredicates.get(0), true, JavaToScala.<Variable>nil());
        AddLiteralsToInit updateObject = new AddLiteralsToInit(JavaToScala.toScalaSeq(tempPrecLiteral), JavaToScala.<VariableConstraint>nil());
        return new Tuple2<Domain, Plan>(domPlan._1(), domPlan._2().update(updateObject));
    }

    private PlanStep getEmptyPlanStep(int id, Formula precondition, Formula effect, Seq<Variable> variablesOfprecsAndEffects) {
        String prefixExtension = "[GENERIC]";

        String tempName = uniqueStringPrefix + prefixExtension + "[" + java.util.UUID.randomUUID() + "]";
        Task tempEmptyTask = new GeneralTask(tempName, true, variablesOfprecsAndEffects, JavaToScala.<VariableConstraint>nil(), precondition, effect);
        return new PlanStep(id, tempEmptyTask, variablesOfprecsAndEffects, noneForMethod, noneForPlanStep);
    }

    public String[] getPrimitivesFromPrefixDistinct() {
        HashSet<String> primitiveTaskNamesDistinct = new HashSet<String>();
        for (int a = 0; a < prefixNameArguments.length; a++) {
            primitiveTaskNamesDistinct.add(prefixNameArguments[a][0]);
        }
        return primitiveTaskNamesDistinct.toArray(new String[0]);
    }

    public Task getTaskFromDomainByName(Domain domain, String name) throws addPrefixException {
        for (int a = 0; a < domain.tasks().size(); a++) {
            Task tempCurrentTask = domain.tasks().apply(a);
            if (tempCurrentTask.name().equals(name)) {
                return tempCurrentTask;
            }
        }
        throw new addPrefixException("No task with name '" + name + "' in repairContext.getDomain().");
    }

    public Predicate getPredicateFromDomainByName(Domain domain, String name) throws addPrefixException {
        for (int a = 0; a < domain.predicates().size(); a++) {
            Predicate tempCurrentPredicate = domain.predicates().apply(a);
            if (tempCurrentPredicate.name().equals(name)) {
                return tempCurrentPredicate;
            }
        }
        throw new addPrefixException("No predicate with name '" + name + "' in repairContext.getDomain().");
    }

    public Constant getConstantFromDomainByName(Domain domain, String name) throws addPrefixException {
        for (int a = 0; a < domain.constants().size(); a++) {
            Constant tempCurrentConstant = domain.constants().apply(a);
            if (tempCurrentConstant.name().equals(name)) {
                return tempCurrentConstant;
            }
        }
        throw new addPrefixException("No constant with name '" + name + "' in repairContext.getDomain().");
    }
}
