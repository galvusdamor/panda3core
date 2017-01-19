package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;

import java.util.*;

/**
 * Created by dhoeller on 26.07.16.
 */
public class cRPG implements htnGroundedProgressionHeuristic {

    /**
     * Define "Operator": An operator is either an action or a method
     * Define "Task": A task is either an action or an abstract task(-name)
     */

    private static int numOperators; // number of actions + number of methods
    private static int numTasks; // number of actions + number of ground abstract tasks
    private static int numExtenedStateFeatures; // number of original state features + one feature for every task

    // [f1, f2, ..., fm, a1, a2, c1, a3, ..., an, c2, ..., co]
    public static int[][] prec2task; // [1][4, 5, 7] means that the tasks 4, 5 and 7 all have precondition 1
    public static Set<Integer>[] add2task; // 1 -> [4, 5, 7] means that the tasks 4, 5 and 7 all add fact no. 1
    public static int[] numprecs; // todo: be aware that there might be an HTN-Task that has teh same prec two times!

    public static List<Integer> operatorsWithoutPrec;

    // [a1, a2, ..., an, m1, m2, ..., mp]
    private static int[][] precLists;
    public static int[][] addLists;  // [1][2, 5] means that action 1 adds state-features 2 and 5

    // these are maps because its indices do not start with 0
    private static HashMap<method, Integer> MethodToIndex;
    private static HashMap<Integer, method> IndexToMethod; // todo replace by array

    private static HashMap<GroundTask, Integer> TaskToIndex;
    private static HashMap<Integer, GroundTask> IndexToTask; // todo replace by array

    private static Map<GroundTask, Set<Integer>> reachableFrom;

    // Members of the current object
    private boolean goalRelaxedReachable;
    private BitSet reachability; // set of tasks that is top-down-reachable from the current network
    private int heuristicValue;


    public cRPG() {

    }

    boolean topDownReachability = false;
    boolean orderingInvariants = false;

    public cRPG(HashMap<Task, HashMap<GroundTask, List<method>>> methods, Set<GroundTask> allActions) {

        long time = System.currentTimeMillis();
        System.out.print("Init composition RPG heuristic");

        cRPG.numOperators = createMethodLookupTable(methods);
        cRPG.numTasks = createTaskLookupTable(allActions, getGroundTasks(methods));

        if (topDownReachability || orderingInvariants) {
            cRPG.reachableFrom = initTopDownReachability(methods);
        }

        if (topDownReachability && orderingInvariants) {
            long ordTime = System.currentTimeMillis();
            System.out.println("Calculating global ordering invariants...");
            BitSet[] aBeforeB = new BitSet[numOperators];
            for (int i = 0; i < numOperators; i++) {
                aBeforeB[i] = new BitSet(numOperators);
                aBeforeB[i].set(0, numOperators - 1, true);
            }
            for (Task t : methods.keySet()) {
                HashMap<GroundTask, List<method>> gts = methods.get(t);
                for (GroundTask gt : gts.keySet()) {
                    for (method m : gts.get(gt)) {
                        scala.collection.Iterator<PlanStep> iter1 = m.m.decompositionMethod().subPlan().planStepsWithoutInitGoal().iterator();
                        while (iter1.hasNext()) {
                            PlanStep ps1 = iter1.next();
                            scala.collection.Iterator<PlanStep> iter2 = m.m.decompositionMethod().subPlan().planStepsWithoutInitGoal().iterator();
                            while (iter2.hasNext()) {
                                PlanStep ps2 = iter2.next();
                                if (!m.m.decompositionMethod().subPlan().orderingConstraints().lteq(ps1, ps2)) {
                                    GroundTask gt1 = m.m.subPlanPlanStepsToGrounded().get(ps1).get();
                                    GroundTask gt2 = m.m.subPlanPlanStepsToGrounded().get(ps2).get();
                                    int taskID1 = TaskToIndex.get(gt1);
                                    int taskID2 = TaskToIndex.get(gt2);

                                    //for()
                                }
                            }
                        }

                    }
                }
            }
            System.out.println("Ordering invariants calculated in " + (System.currentTimeMillis() - ordTime) + " ms.");
        }

        // action-task-facts are true one layer after the action, this can done due to the 1-to-1 correspondence
        cRPG.numExtenedStateFeatures = operators.numStateFeatures + cRPG.numTasks;

        cRPG.prec2task = new int[numExtenedStateFeatures][]; // pointers from literals to tasks that have this literal as precondition
        cRPG.precLists = new int[numOperators][];
        cRPG.add2task = new Set[numExtenedStateFeatures]; // pointers from literals to tasks that add it
        cRPG.addLists = new int[numOperators][];

        List<Integer>[] inverseMapping = new List[numExtenedStateFeatures];

        for (int i = 0; i < cRPG.numExtenedStateFeatures; i++) {
            inverseMapping[i] = new ArrayList<>();
        }

        for (int actionI = 0; actionI < operators.numActions; actionI++) {
            precLists[actionI] = operators.precList[actionI];
            for (int precI = 0; precI < operators.precList[actionI].length; precI++) {
                inverseMapping[operators.precList[actionI][precI]].add(actionI);
            }
        }

        // set number of action preconditions
        numprecs = new int[cRPG.numOperators];
        for (int i = 0; i < operators.numActions; i++) {
            numprecs[i] = operators.precList[i].length;
        }

        for (int methodI : cRPG.IndexToMethod.keySet()) {
            method m = cRPG.IndexToMethod.get(methodI);
            numprecs[methodI] = m.tasks.length;
            precLists[methodI] = new int[numprecs[methodI]];

            for (int subtaskId = 0; subtaskId < m.tasks.length; subtaskId++) {
                GroundTask t = m.tasks[subtaskId];
                int taskIndex = cRPG.TaskToIndex.get(t);
                precLists[methodI][subtaskId] = taskIndex;
                inverseMapping[taskIndex].add(methodI);
            }
            int compTaskIndex = cRPG.TaskToIndex.get(m.m.groundAbstractTask());
            addLists[methodI] = new int[1];
            addLists[methodI][0] = compTaskIndex;
        }

        /*
        BitSet effectsOfPrecFreeActions = new BitSet(operators.numStateFeatures);
        effectsOfPrecFreeActions.set(0, operators.numStateFeatures - 1, false);
        */
        operatorsWithoutPrec = new ArrayList<>();
        for (int i = 0; i < cRPG.numOperators; i++) {
            if (cRPG.precLists[i].length == 0) {
                operatorsWithoutPrec.add(i);
            }
        }

        // copy temporal lists to array
        for (int i = 0; i < inverseMapping.length; i++) {
            List<Integer> mapping = inverseMapping[i];
            prec2task[i] = new int[mapping.size()];
            for (int j = 0; j < mapping.size(); j++) {
                prec2task[i][j] = mapping.get(j);
            }
        }

        // generate add-lists
        for (int i = 0; i < operators.numActions; i++) {
            addLists[i] = new int[operators.addList[i].length + 1];
            int j;
            for (j = 0; j < operators.addList[i].length; j++) {
                cRPG.addLists[i][j] = operators.addList[i][j];
            }
            cRPG.addLists[i][j] = operators.numStateFeatures + i; // actions are located after the original state features and this is the i-th action
        }

        // generate lists mapping literal to lists of operators having it as add-effect
        for (int i = 0; i < numExtenedStateFeatures; i++) {
            cRPG.add2task[i] = new HashSet<>();
        }

        for (int i = 0; i < cRPG.numOperators; i++) {
            for (int addEffect : addLists[i]) {
                add2task[addEffect].add(i);
            }
        }

        // every action makes the corresponding task-fact true
        System.out.println(" (" + (System.currentTimeMillis() - time) + " ms)");
    }

    public Map<GroundTask, Set<Integer>> initTopDownReachability(HashMap<Task, HashMap<GroundTask, List<method>>> methods) {
        // this structures are used during search and must be initialized
        this.reachability = new BitSet(cRPG.numOperators);
        this.reachability.set(0, this.reachability.size() - 1, true);

        // here the reachability is calculated
        // (1) first, the direct children, defined by the methods, are added
        System.out.println("Calculating top down reachability of tasks...");
        Map<GroundTask, Set<GroundTask>> reachableTasksFrom = new HashMap<>();
        long time = System.currentTimeMillis();
        for (Task k : methods.keySet()) {
            HashMap<GroundTask, List<method>> methodLists = methods.get(k);
            for (GroundTask t : methodLists.keySet()) {
                Set<GroundTask> subtasks = reachableTasksFrom.get(t);
                if (subtasks == null) {
                    subtasks = new HashSet<>();
                    reachableTasksFrom.put(t, subtasks);
                }
                List<method> methodsT = methodLists.get(t);
                for (method mths : methodsT) {
                    for (GroundTask gt : mths.tasks) {
                        subtasks.add(gt);
                    }
                }
            }
        }

        System.out.println("\n- Processed direct children " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();

        // (2) now it has to be calculated which tasks are reachable transitively
        boolean changed = true;
        while (changed) {
            changed = false;
            for (GroundTask k : reachableTasksFrom.keySet()) {
                Set<GroundTask> tasks = reachableTasksFrom.get(k);
                Set<GroundTask> temp = new HashSet<>();
                int before = tasks.size();
                for (GroundTask subT : tasks) {
                    if (reachableTasksFrom.containsKey(subT)) {
                        temp.addAll(reachableTasksFrom.get(subT));
                    }
                }
                tasks.addAll(temp);
                if (before < tasks.size()) {
                    changed = true;
                }
            }
        }
        System.out.println("- Calculated transitive reachability " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();

        // Calculate reachable methods
        Map<GroundTask, Set<Integer>> reachableFrom = new HashMap<>();
        for (GroundTask t : reachableTasksFrom.keySet()) {
            Set<Integer> reachableOperators = new HashSet<>();
            reachableFrom.put(t, reachableOperators);
            Set<GroundTask> reachableTasks = reachableTasksFrom.get(t);
            for (method m : methods.get(t.task()).get(t)) {
                reachableOperators.add(MethodToIndex.get(m));
            }
            for (GroundTask reachableTask : reachableTasks) {
                if (reachableTask.task().isPrimitive()) {
                    reachableOperators.add(operators.ActionToIndex.get(reachableTask));
                } else {
                    for (method m : methods.get(reachableTask.task()).get(reachableTask)) {
                        reachableOperators.add(MethodToIndex.get(m));
                    }
                }
            }
        }

        System.out.println("- Calculated reachable operators " + (System.currentTimeMillis() - time));
        return reachableFrom;
    }

    private int createTaskLookupTable(Set<GroundTask> allActions, Set<GroundTask> allTasks) {
        int taskNo = operators.numStateFeatures;
        cRPG.IndexToTask = new HashMap<>();
        cRPG.TaskToIndex = new HashMap<>();

        for (GroundTask a : allActions) {
            cRPG.IndexToTask.put(taskNo, a);
            cRPG.TaskToIndex.put(a, taskNo);
            taskNo++;
        }

        for (GroundTask t : allTasks) {
            cRPG.IndexToTask.put(taskNo, t);
            cRPG.TaskToIndex.put(t, taskNo);
            taskNo++;
        }
        return taskNo;
    }

    private Set<GroundTask> getGroundTasks(HashMap<Task, HashMap<GroundTask, List<method>>> methods) {
        Set<GroundTask> allTasks = new HashSet<>();
        for (HashMap<GroundTask, List<method>> val : methods.values()) {
            for (GroundTask t : val.keySet()) {
                allTasks.add(t);
            }
        }
        return allTasks;
    }

    /**
     * Assigns an index to each method and fills the data structures that are used for the translation.
     *
     * @param methods
     * @return
     */
    private int createMethodLookupTable(HashMap<Task, HashMap<GroundTask, List<method>>> methods) {
        int methodID = operators.numActions;
        cRPG.MethodToIndex = new HashMap<>();
        cRPG.IndexToMethod = new HashMap<>();
        for (HashMap<GroundTask, List<method>> val : methods.values()) {
            for (List<method> val2 : val.values()) {
                for (method m : val2) {
                    assert (!cRPG.MethodToIndex.containsKey(m));
                    cRPG.MethodToIndex.put(m, methodID);
                    cRPG.IndexToMethod.put(methodID, m);
                    methodID++;
                }
            }
        }
        return methodID;
    }

    @Override
    public String getName() {
        return "Simple Composition Graph";
    }

    public void build(ProgressionNetwork tn) {
        // The following array stores for every fact in which fact layer it has been made true:
        // [6, 1, 0, -1] means that there are 4 facts, the first one has been made true in layer 6
        // the second in layer 1... The -1 for fact 4 means that this fact has never been made true
        // contains STRIPS as well as HTN facts
        int[] firstLayerWithFact = new int[cRPG.numExtenedStateFeatures];

        // operatorDelta is a list of lists. Each inner list contains those actions that are applicable
        // in this layer for the *first time*, i.e. it is the delta of applicable actions
        List<List<Integer>> operatorDelta = new ArrayList<>();

        // goalDelta is a list of lists. Each inner list contains those goal facts that hold in the layer
        // for the *first time*, i.e. it is the delta of fulfilled goal conditions. Be aware that
        // goalDelta is *changed* during heuristic calculation.
        //List<List<Integer>> goalDelta = new ArrayList<>();
        List<Map<Integer, Integer>> goalDelta = new ArrayList<>();

        // is used to track how may preconditions are unfulfilled yet
        int[] numOfUnfulfilledPrecs = numprecs.clone();

        // Jörg Hoffmanns measure for choosing a supporter
        // an action's difficulty is the sum of the layers of its preconditions
        int[] actionDifficulty = new int[cRPG.numOperators];

        // add literals in s0 to fringe
        LinkedList<Integer> changedLiterals = new LinkedList<>();
        for (int i = 0; i < operators.numStateFeatures; i++) {
            if (tn.state.get(i)) {
                firstLayerWithFact[i] = 0;
                changedLiterals.add(i);
            } else
                firstLayerWithFact[i] = -1;
        }
        // init facts concerning reachable task
        for (int i = operators.numStateFeatures; i < cRPG.numExtenedStateFeatures; i++) {
            firstLayerWithFact[i] = -1;
        }

        // prepare goal - it contains the STRIPS-goal as well as all tasks that appear in the initial HTN
        List<Integer> stripsAndHtnGoals = new LinkedList<>();
        for (int i = 0; i < operators.goalList.length; i++) { // these are the state-based goals
            stripsAndHtnGoals.add(operators.goalList[i]);
        }

        // add all tasks in the current network as goal
        Set<GroundTask> tasksInTNI = new HashSet<>(); // these are used for the top-down reachability analysis

        LinkedList<ProgressionPlanStep> temp = new LinkedList<>();
        temp.addAll(tn.getFirstAbstractTasks());
        temp.addAll(tn.getFirstPrimitiveTasks());
        while (!temp.isEmpty()) {
            ProgressionPlanStep ps = temp.removeFirst();
            tasksInTNI.add(ps.getTask());
            stripsAndHtnGoals.add(cRPG.TaskToIndex.get(ps.getTask()));
            temp.addAll(ps.successorList);
        }

        //LinkedList<Integer> unfulfilledGoals = new LinkedList<>();
        // a map from an int representing a goal fact to the number of times it is needed
        Map<Integer, Integer> unfulfilledGoals = new HashMap<>();

        // todo !evaluate!: this might be a set or a list
        //List<Integer> newGoalDelta = new ArrayList<>();
        //HashSet<Integer> newGoalDelta = new HashSet<>();
        HashMap<Integer, Integer> newGoalDelta = new HashMap<>();

        goalDelta.add(newGoalDelta); // todo: (operator-facts are not marked applicable yet) should only test non-operator-facts
        for (int goalFact : stripsAndHtnGoals) {
            if (firstLayerWithFact[goalFact] == 0) {
                int count = newGoalDelta.containsKey(goalFact) ? newGoalDelta.get(goalFact) : 0;
                newGoalDelta.put(goalFact, count + 1);
            } else {
                int count = unfulfilledGoals.containsKey(goalFact) ? unfulfilledGoals.get(goalFact) : 0;
                unfulfilledGoals.put(goalFact, count + 1);
            }
        }

        operatorDelta.add(new LinkedList<Integer>()); // dummy list to make indices easier

        // start building the graph
        int layerId = 1;
        this.goalRelaxedReachable = true;
        while (!unfulfilledGoals.isEmpty()) {
            // if there are unfulfilled goals, but the state did not change -> relaxed unsolvable
            if (changedLiterals.isEmpty()) {
                this.goalRelaxedReachable = false;
                break;
            }

            // initil data structures
            newGoalDelta = new HashMap<>();
            goalDelta.add(newGoalDelta);
            List<Integer> newOperatorDelta = new LinkedList<>();
            operatorDelta.add(newOperatorDelta);

            // in first layer, add actions without preconditions
            if (layerId == 1) {
                newOperatorDelta.addAll(cRPG.operatorsWithoutPrec);
            }

            // fact-loop
            while (!changedLiterals.isEmpty()) {
                final int addedLiteral = changedLiterals.removeFirst();
                for (int actionWithThisPrec : prec2task[addedLiteral]) {
                    if ((topDownReachability) && (!this.reachability.get(actionWithThisPrec))) {
                        continue; // unreachable via hierarchy
                    }
                    numOfUnfulfilledPrecs[actionWithThisPrec]--;
                    actionDifficulty[actionWithThisPrec] += layerId;
                    if (numOfUnfulfilledPrecs[actionWithThisPrec] == 0) {
                        newOperatorDelta.add(actionWithThisPrec);
                    }
                }
            }

            // operator-loop
            for (int newlyApplicableAction : newOperatorDelta) {
                boolean reachable = false;
                if (topDownReachability) {
                    reachabilityloop:
                    for (GroundTask htnTask : tasksInTNI) {
                        if (htnTask.task().isPrimitive()) {
                            if (operators.ActionToIndex.get(htnTask) == newlyApplicableAction) {
                                reachable = true;
                                break reachabilityloop;
                            }
                        } else if (cRPG.reachableFrom.get(htnTask).contains(newlyApplicableAction)) {
                            reachable = true;
                            break reachabilityloop;
                        }
                    }
                }
                if ((!topDownReachability) || (reachable)) {
                    for (int effect : addLists[newlyApplicableAction]) {
                        if (firstLayerWithFact[effect] < 0) {
                            changedLiterals.add(effect);
                            firstLayerWithFact[effect] = layerId;

                            // test if fact is in goal-list
                            if (unfulfilledGoals.containsKey(effect)) {
                                int add = unfulfilledGoals.remove(effect);
                                int count = newGoalDelta.containsKey(effect) ? newGoalDelta.get(effect) : 0;
                                newGoalDelta.put(effect, count + add);
                            }
                        }
                    }
                } else {
                    this.reachability.set(newlyApplicableAction, false);
                 /*   if (cRPG.IndexToMethod.containsKey(newlyApplicableAction))
                        System.out.println(cRPG.IndexToMethod.get(newlyApplicableAction));
                    else
                        System.out.println(operators.IndexToAction[newlyApplicableAction]);*/
                }

            }
            layerId++;
        }
        //printLayerDelta(operatorDelta, firstLayerWithFact, goalDelta);

        if (goalRelaxedReachable) {
            this.heuristicValue = calcHeu(firstLayerWithFact, operatorDelta, actionDifficulty, goalDelta);
        } else {
            this.heuristicValue = Integer.MAX_VALUE;
        }
    }

    private int calcHeu(int[] firstLayerWithFact, List<List<Integer>> operatorDelta, int[] actionDifficulty, List<Map<Integer, Integer>> goalDelta) {
//        String s = printLayerDelta(operatorDelta, firstLayerWithFact, goalDelta);
        int numactions = 0;
        for (int layer = goalDelta.size() - 1; layer >= 1; layer--) {
            Map<Integer, Integer> oneGoalDelta = goalDelta.get(layer);
            for (int goalFact : oneGoalDelta.keySet()) {
                int count = oneGoalDelta.get(goalFact);

                int bestDifficulty = Integer.MAX_VALUE;
                int producer = -1;

                for (int maybeProducer : operatorDelta.get(layer)) {
                    if (add2task[goalFact].contains(maybeProducer)) {

                        if (numprecs[maybeProducer] < bestDifficulty) {
                            bestDifficulty = numprecs[maybeProducer];
                            producer = maybeProducer;
                        }

                        /*
                        if (actionDifficulty[maybeProducer] < bestDifficulty) {
                            bestDifficulty = actionDifficulty[maybeProducer];
                            producer = maybeProducer;
                        }*/
                    }
                }

                if (!operators.ShopPrecActions.contains(producer)) {
                    numactions += count;
                }

                for (Integer aPrec : cRPG.precLists[producer]) {
                    int fl = firstLayerWithFact[aPrec];
                    if (fl > 0) {
                        Map<Integer, Integer> precDel = goalDelta.get(fl);//.add(aPrec);
                        int precCount = precDel.containsKey(aPrec) ? precDel.get(aPrec) : 0;
                        precDel.put(aPrec, precCount + count);
                    }
                }
            }
        }
        return numactions;
    }

    private String printLayerDelta(List<List<Integer>> operatorDelta, int[] firstLayerWithFact, List<Map<Integer, Integer>> goalDelta) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < operatorDelta.size(); i++) {
            if (i >= 0) {
                sb.append("Operator Delta " + i + ": ");
                boolean first = true;
                for (int j = 0; j < operatorDelta.get(i).size(); j++) {
                    int action = operatorDelta.get(i).get(j);
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", ");
                    }
                    if (action < operators.numActions) {
                        sb.append(operators.IndexToAction[action].mediumInfo());
                    } else
                        sb.append(cRPG.IndexToMethod.get(action).m.mediumInfo());
                }
            }

            sb.append("\n    Fact Delta " + i + ": ");
            boolean first = true;
            for (int j = 0; j < firstLayerWithFact.length; j++) {
                if (firstLayerWithFact[j] == i) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", ");
                    }
                    if (j < operators.numStateFeatures) {
                        sb.append(operators.IndexToLiteral[j].mediumInfo());
                    } else
                        sb.append(cRPG.IndexToTask.get(j).mediumInfo());
                }
            }

            sb.append("\n    Goal Delta " + i + ": ");
            first = true;
            Iterator<Integer> goalIter = goalDelta.get(i).keySet().iterator();
            while (goalIter.hasNext()) {
                int fact = goalIter.next();
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                if (fact < operators.numStateFeatures) {
                    sb.append(operators.IndexToLiteral[fact].mediumInfo());
                } else
                    sb.append(cRPG.IndexToTask.get(fact).mediumInfo());
                if (goalDelta.get(i).get(fact) > 1) {
                    sb.append(" * " + goalDelta.get(i).get(fact));
                }
            }

            sb.append("\n\n");
        }
        return sb.toString();
    }

    @Override
    public int getHeuristic() {
        return this.heuristicValue;
    }

    @Override
    public boolean goalRelaxedReachable() {
        return goalRelaxedReachable;
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork tn, ProgressionPlanStep ps, method m) {
        cRPG crpg = new cRPG();
        if (topDownReachability)
            crpg.reachability = (BitSet) this.reachability.clone();
        crpg.build(tn);
        return crpg;
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork tn, ProgressionPlanStep ps) {
        cRPG crpg = new cRPG();
        if (topDownReachability)
            crpg.reachability = (BitSet) this.reachability.clone();
        crpg.build(tn);
        return crpg;
    }
}
