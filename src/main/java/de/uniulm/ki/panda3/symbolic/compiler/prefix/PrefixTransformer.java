package de.uniulm.ki.panda3.symbolic.compiler.prefix;

import de.uniulm.ki.panda3.symbolic.compiler.DomainTransformer;
import de.uniulm.ki.panda3.symbolic.csp.Equal;
import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint;
import de.uniulm.ki.panda3.symbolic.domain.*;
import de.uniulm.ki.panda3.symbolic.domain.updates.*;
import de.uniulm.ki.panda3.symbolic.logic.*;
import de.uniulm.ki.panda3.symbolic.parser.hddl.hddlPanda3Visitor;
import de.uniulm.ki.panda3.util.seqProviderList;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.panda3.symbolic.search.NoFlaws$;
import de.uniulm.ki.panda3.symbolic.search.NoModifications$;
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

    private String uniqueStringPrefix = "";
    String prefixExtension = "p*";

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
        recognition,
        verify
    }

    private PrefixTransformer() {
    }

    public static PrefixTransformer getVerifyTransformer(String[][] prefix) {
        System.out.println("Verify");
        PrefixTransformer res = new PrefixTransformer();
        res.prefixNameArguments = prefix;
        res.whatToDo = programTasks.verify;
        return res;
    }

    public static PrefixTransformer getRepairTransformer(String[][] prefix, String[][] process) {
        System.out.println("Repair");
        PrefixTransformer res = new PrefixTransformer();
        res.prefixNameArguments = prefix;
        res.processLiteralsValues = process;
        res.whatToDo = programTasks.repair;
        return res;
    }

    public static PrefixTransformer getRecognitionTransformer(String[][] prefix) {
        System.out.println("Recognition");
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
     * The number of literals differs in the cases of (1) repair and (2) recognition and (3) verification.
     * The predicate that is made true by the last enforced action is added to the goal of the problem.
     */
    private Tuple2<Domain, Plan> transformStep_L_x(Tuple2<Domain, Plan> domPlan) {
        String prefixExtension = "l";

        int predicateCount;
        if ((this.whatToDo == programTasks.repair) || (this.whatToDo == programTasks.verify)) {
            predicateCount = prefixNameArguments.length + 2;
        } else {
            predicateCount = prefixNameArguments.length + 1;
        }

        for (int a = 1; a <= predicateCount; a++) {
            String name = uniqueStringPrefix + prefixExtension + a;
            Predicate p = new Predicate(name, JavaToScala.<Sort>nil());
            newPredicates.add(p);
        }

        AddPredicate updateObject = new AddPredicate(newPredicates.result());
        Tuple2<Domain, Plan> tempDomain = new Tuple2<Domain, Plan>(domPlan._1().update(updateObject), domPlan._2());

        //
        // add the last literal to the goal to enforce the execution of the prefix
        //
        int addToGoal;
        if ((this.whatToDo == programTasks.repair) || (this.whatToDo == programTasks.recognition)) {
            addToGoal = predicateCount - 1;
        } else {
            addToGoal = predicateCount - 2;
        }
        Formula g = new Literal(newPredicates.get(addToGoal), true, (new seqProviderList<Variable>()).result());

        seqProviderList<Formula> goalLiterals = new seqProviderList();
        goalLiterals.add(tempDomain._2().goal().schema().precondition());
        goalLiterals.add(g);
        And newGoal = new And(goalLiterals.result());

        Task tGoal = new GeneralTask(
                tempDomain._2().goal().schema().name(),
                tempDomain._2().goal().schema().isPrimitive(),
                tempDomain._2().goal().schema().parameters(),
                tempDomain._2().goal().schema().artificialParametersRepresentingConstants(),
                tempDomain._2().goal().schema().parameterConstraints(),
                newGoal,
                tempDomain._2().goal().schema().effect()
        );

        scala.collection.immutable.HashMap map = new scala.collection.immutable.HashMap();
        map = map.$plus(new Tuple2(domPlan._2().goal().schema(), tGoal));
        ExchangeTask exchangeGoal = new ExchangeTask(map);

        return new Tuple2<Domain, Plan>(tempDomain._1(), tempDomain._2().update(exchangeGoal));
    }


    /**
     * O_x is the set of new actions that represent the elements in the prefix. p is the process that is added in plan repair.
     * <p/>
     * todo: Daniel says: do not really know why the alpha is in the title.
     */
    private Tuple2<Domain, Plan> transformStep_O_x_und_p_mit_alpha(Tuple2<Domain, Plan> domPlan) throws addPrefixException {

        for (int a = 1; a <= prefixNameArguments.length; a++) {
            Task relatedTask = getTaskFromDomainByName(domPlan._1(), prefixNameArguments[a - 1][0]);
            String newTaskName = uniqueStringPrefix + prefixExtension + a + relatedTask.name().substring(0, 1).toUpperCase() + relatedTask.name().substring(1);

            ArrayList<Formula> precLiteralList = new ArrayList<Formula>();
            Literal precLiteral = new Literal(newPredicates.get(a - 1), true, JavaToScala.<Variable>nil());
            precLiteralList.add(precLiteral);

            ArrayList<Formula> effectLiteralList = new ArrayList<Formula>();
            Literal addLiteral = new Literal(newPredicates.get(a), true, JavaToScala.<Variable>nil());
            Literal delLiteral = new Literal(newPredicates.get(a - 1), false, JavaToScala.<Variable>nil());
            effectLiteralList.add(addLiteral);
            effectLiteralList.add(delLiteral);

            ArrayList<VariableConstraint> newPrimVarConstraints = new ArrayList<VariableConstraint>();

            for (int b = 0; b < relatedTask.parameters().size(); b++) {
                Constant tempConstant = getConstantFromDomainByName(domPlan._1(), prefixNameArguments[a - 1][b + 1]);

                newPrimVarConstraints.add(new Equal(relatedTask.parameters().apply(b), tempConstant));
            }

            Task newPrimitiveTask = new GeneralTask(newTaskName, true, relatedTask.parameters(), relatedTask.artificialParametersRepresentingConstants(),
                    JavaToScala.concatJavaListToScalaSeq(relatedTask.parameterConstraints(), newPrimVarConstraints),
                    new And<Formula>(JavaToScala.concatJavaListToScalaSeq(JavaToScala.toScalaSeq(relatedTask.precondition()), precLiteralList)),
                    new And<Formula>(JavaToScala.concatJavaListToScalaSeq(JavaToScala.toScalaSeq(relatedTask.effect()), effectLiteralList)));
            newPrimitiveTasks.add(newPrimitiveTask);
        }

        // create process
        if (this.whatToDo == programTasks.repair) {
            String name = uniqueStringPrefix + prefixExtension + "process";

            ArrayList<Literal> precLiteralList = new ArrayList<Literal>();
            Literal precLiteral = new Literal(newPredicates.get(prefixNameArguments.length), true, JavaToScala.<Variable>nil());
            precLiteralList.add(precLiteral);

            ArrayList<Literal> effektLiteralList = new ArrayList<Literal>();
            Literal addLiteral = new Literal(newPredicates.get(prefixNameArguments.length + 1), true, JavaToScala.<Variable>nil());
            Literal delLiteral = new Literal(newPredicates.get(prefixNameArguments.length), false, JavaToScala.<Variable>nil());
            effektLiteralList.add(addLiteral);
            effektLiteralList.add(delLiteral);


            ArrayList<VariableConstraint> processVariableConstraints = new ArrayList<VariableConstraint>();
            ArrayList<Variable> processVariables = new ArrayList<Variable>();

            int tempId = 0;

            for (int a = 0; a < processLiteralsValues.length; a++) {
                boolean positive = processLiteralsValues[a][0].charAt(0) == '+';
                processLiteralsValues[a][0] = processLiteralsValues[a][0].substring(1);

                Predicate tempPredicate = getPredicateFromDomainByName(domPlan._1(), processLiteralsValues[a][0]);

                ArrayList<Variable> processLiteralVariables = new ArrayList<Variable>();
                for (int b = 1; b < processLiteralsValues[a].length; b++) {
                    tempId++;
                    Variable var = new Variable(tempId, uniqueStringPrefix + "[V]" + "p_" + tempId, tempPredicate.argumentSorts().apply(b - 1));
                    Constant constant = getConstantFromDomainByName(domPlan._1(), processLiteralsValues[a][b]);

                    processLiteralVariables.add(var);
                    processVariables.add(var);

                    processVariableConstraints.add(new Equal(var, constant));
                }

                Literal tempProcessLiteral = new Literal(getPredicateFromDomainByName(domPlan._1(), processLiteralsValues[a][0]), positive, JavaToScala.toScalaSeq(processLiteralVariables));
                effektLiteralList.add(tempProcessLiteral);
            }

            processTaskSchema = new ReducedTask(name, true, JavaToScala.toScalaSeq(processVariables), JavaToScala.<Variable>nil(), JavaToScala.toScalaSeq(processVariableConstraints),
                    new And<Literal>(JavaToScala.toScalaSeq(precLiteralList)), new And<Literal>(JavaToScala.toScalaSeq(effektLiteralList)));
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
        String prefixExtension = "c";

        for (String oName : getPrimitivesFromPrefixDistinct()) {
            String tName = uniqueStringPrefix + prefixExtension + oName;
            Task relatedTask = getTaskFromDomainByName(domPlan._1(), oName);
            Task newCompoundTask = new GeneralTask(tName, false, relatedTask.parameters(), relatedTask.artificialParametersRepresentingConstants(), relatedTask.parameterConstraints(),
                    relatedTask.precondition(), relatedTask.effect());
            newCompoundTasks.add(newCompoundTask);
            map_M_t.put(relatedTask, newCompoundTask);
        }

        AddTask updateObject = new AddTask(JavaToScala.toScalaSeq(newCompoundTasks));
        return new Tuple2<Domain, Plan>(domPlan._1().update(updateObject), domPlan._2());
    }

    /**
     * The last new predicate is the precondition of all original actions in the domain, it is set by the effect of the
     * process or of the last action in the prefix. In case of plan verification, no action accept the actions in the
     * prefix shall be executable, so here the literal that is added to the prefix is never ever set.
     */
    private Tuple2<Domain, Plan> transformStep_alpha_O(Tuple2<Domain, Plan> domPlan) throws addPrefixException {
        HashMap<Task, Task> tempMap = new HashMap<Task, Task>();

        // new part of the precondition
        ArrayList<Formula> precLiteralList = new ArrayList<Formula>();
        Literal precLiteral = new Literal(newPredicates.get(newPredicates.size() - 1), true, JavaToScala.<Variable>nil());
        precLiteralList.add(precLiteral);

        for (int a = 0; a < domPlan._1().tasks().size(); a++) {
            Task currentTask = domPlan._1().tasks().apply(a);
            if (!currentTask.isPrimitive()) {
                continue;
            } else if (currentTask.name().startsWith(uniqueStringPrefix + prefixExtension)) {
                continue;
            } else {
                Task tempNewPrimitiveTask = new GeneralTask(currentTask.name(), currentTask.isPrimitive(), currentTask.parameters(), currentTask.artificialParametersRepresentingConstants(),
                        currentTask.parameterConstraints(), new And(JavaToScala.concatJavaListToScalaSeq(JavaToScala.toScalaSeq(currentTask.precondition()), precLiteralList)), currentTask.effect());
                tempMap.put(currentTask, tempNewPrimitiveTask);
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
        scala.collection.immutable.Map<Task, Task> scalaMap = JavaToScala.toScalaMap(map_M_t);
        ExchangeTaskSchemaInMethods updateObject = new ExchangeTaskSchemaInMethods(scalaMap);
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
            Task primitiveTask = getTaskFromDomainByName(domPlan._1(), entry.getKey().name());
            Task compoundTask = entry.getValue();

            PlanStep psInit = getEmptyPlanStep(1, new And<>(JavaToScala.<Literal>nil()), compoundTask.precondition(), compoundTask.parameters());
            PlanStep psGoal = getEmptyPlanStep(3, compoundTask.effect(), new And<>(JavaToScala.<Literal>nil()), compoundTask.parameters());

            PlanStep psO = new PlanStep(2, primitiveTask, primitiveTask.parameters());

            internalTaskNetwork internalTN = new internalTaskNetwork();
            internalTN.addPlanStep(psInit);
            internalTN.addPlanStep(psO);
            internalTN.addPlanStep(psGoal);
            internalTN.addOrdering(psInit, psO);
            internalTN.addOrdering(psO, psGoal);

            Plan subPlan = new Plan(internalTN.planSteps(), internalTN.causalLinks(), internalTN.taskOrderings(),
                    internalTN.csp().addVariables(psO.arguments()), psInit, psGoal, domPlan._2().isModificationAllowed(),
                    domPlan._2().isFlawAllowed(), domPlan._2().planStepDecomposedByMethod(), domPlan._2().planStepParentInDecompositionTree());

            DecompositionMethod decompositionMethod = new SimpleDecompositionMethod(compoundTask, subPlan, "new method");
            newMethods.add(decompositionMethod);
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
            PlanStep tempPlanStep_O = new PlanStep(2, currentNewPrimitiveTask, currentNewPrimitiveTask.parameters());

            internalTaskNetwork subnetwork = new internalTaskNetwork();
            subnetwork.addPlanStep(psInit);
            subnetwork.addPlanStep(tempPlanStep_O);
            subnetwork.addPlanStep(psGoal);
            subnetwork.addOrdering(psInit, tempPlanStep_O);
            subnetwork.addOrdering(tempPlanStep_O, psGoal);

            Plan tempSubPlan = null;

            // In case of plan repair, the process is added, but not in recognition
            if ((whatToDo == programTasks.repair) && (a == newPrimitiveTasks.size() - 1)) {
                PlanStep psProcess = null;
                psProcess = new PlanStep(3, processTaskSchema, processTaskSchema.parameters());
                subnetwork.addPlanStep(psProcess);
                subnetwork.addOrdering(tempPlanStep_O, psProcess);
                subnetwork.addOrdering(psProcess, psGoal);

                tempSubPlan = new Plan(subnetwork.planSteps(), subnetwork.causalLinks(), subnetwork.taskOrderings(),
                        subnetwork.csp().addVariables(tempPlanStep_O.arguments()).addVariables(psProcess.arguments()),
                        psInit, psGoal, NoModifications$.MODULE$, NoFlaws$.MODULE$, hddlPanda3Visitor.planStepsDecomposedBy, hddlPanda3Visitor.planStepsDecompositionParents);

            } else {
                subnetwork.addOrdering(tempPlanStep_O, psGoal);

                tempSubPlan = new Plan(subnetwork.planSteps(), subnetwork.causalLinks(), subnetwork.taskOrderings(),
                        subnetwork.csp().addVariables(tempPlanStep_O.arguments()),
                        psInit, psGoal, NoModifications$.MODULE$, NoFlaws$.MODULE$, hddlPanda3Visitor.planStepsDecomposedBy, hddlPanda3Visitor.planStepsDecompositionParents);
            }

            DecompositionMethod decompositionMethod = new SimpleDecompositionMethod(correspondingCompoundTask, tempSubPlan, "some method");
            newMethods.add(decompositionMethod);
        }

        AddMethod updateObject = new AddMethod(JavaToScala.toScalaSeq(newMethods));
        return new Tuple2<Domain, Plan>(domPlan._1().update(updateObject), domPlan._2());
    }

    private Tuple2<Domain, Plan> transformStep_s_i(Tuple2<Domain, Plan> domPlan) throws addPrefixException {
        Literal initLit = new Literal(newPredicates.get(0), true, JavaToScala.<Variable>nil());
        int goalLiteralIndex;
        if (whatToDo == programTasks.verify)
            goalLiteralIndex = newPredicates.size() - 2;
        else goalLiteralIndex = newPredicates.size() - 1;

        Literal goalLit = new Literal(newPredicates.get(goalLiteralIndex), true, JavaToScala.<Variable>nil());
        AddLiteralsToInitAndGoal updateObject = new AddLiteralsToInitAndGoal(JavaToScala.toScalaSeq(initLit), JavaToScala.toScalaSeq(goalLit), JavaToScala.<VariableConstraint>nil());
        return new Tuple2<Domain, Plan>(domPlan._1(), domPlan._2().update(updateObject));
    }

    private PlanStep getEmptyPlanStep(int id, Formula precondition, Formula effect, Seq<Variable> variablesOfprecsAndEffects) {
        String prefixExtension = "[GENERIC]";

        String tempName = uniqueStringPrefix + prefixExtension + "[" + java.util.UUID.randomUUID() + "]";
        Task tempEmptyTask = new GeneralTask(tempName, true, variablesOfprecsAndEffects, JavaToScala.<Variable>nil(), JavaToScala.<VariableConstraint>nil(), precondition, effect);
        return new PlanStep(id, tempEmptyTask, variablesOfprecsAndEffects);
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
