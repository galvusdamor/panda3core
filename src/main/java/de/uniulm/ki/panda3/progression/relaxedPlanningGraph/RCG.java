package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.proUtil.UUIntStack;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;

import java.util.*;

/**
 * Created by dhoeller on 26.07.16.
 */
public class RCG implements htnGroundedProgressionHeuristic {
    enum producerSelection {numOfPreconditions, actionDifficulty, firstCome}

    ;
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
    public static int[] numprecs;

    public static UUIntStack operatorsWithoutPrec;

    // [a1, a2, ..., an, m1, m2, ..., mp]
    private static int[][] precLists;
    public static int[][] addLists;  // [1][2, 5] means that action 1 adds state-features 2 and 5

    private static HashMap<method, Integer> MethodToIndex;
    private static method[] IndexToMethod;


    private static HashMap<GroundTask, Integer> TaskToIndex;
    private static GroundTask[] IndexToTask;

    private static TopDownReachabilityGraph tdRechability;

    private static method IndexToMethodGet(int index) {
        return IndexToMethod[index - operators.numActions];
    }

    private static GroundTask IndexToTaskGet(int index) {
        return IndexToTask[index - operators.numStateFeatures];
    }

    private static void IndexToTaskPut(int index, GroundTask t) {
        IndexToTask[index - operators.numStateFeatures] = t;
    }

    private static void IndexToMethodPut(int index, method m) {
        IndexToMethod[index - operators.numActions] = m;
    }

    // Members of the current object
    private boolean goalRelaxedReachable;
    private int heuristicValue;

    private RCG() { // only used by factory methods -> private
    }

    public static boolean topDownReachability = true;
    boolean orderingInvariants = false;

    producerSelection prod = producerSelection.numOfPreconditions;

    public RCG(HashMap<Task, HashMap<GroundTask, List<method>>> methods, List<ProgressionPlanStep> initialTasks, Set<GroundTask> allActions, boolean useTDReachability) {
        RCG.topDownReachability = useTDReachability;

        long time = System.currentTimeMillis();
        System.out.println("Init Relaxed Composition Graph (RCG) heuristic");

        RCG.numOperators = createMethodLookupTable(methods);
        RCG.numTasks = createTaskLookupTable(allActions, getGroundTasks(methods)) - operators.numStateFeatures;

        if (topDownReachability || orderingInvariants) {
            tdRechability = new TopDownReachabilityGraph(methods, initialTasks, RCG.numTasks, operators.numActions, TaskToIndex);
        }

        // action-task-facts are true one layer after the action, this can done due to the 1-to-1 correspondence
        RCG.numExtenedStateFeatures = operators.numStateFeatures + RCG.numTasks;

        RCG.prec2task = new int[numExtenedStateFeatures][]; // pointers from literals to tasks that have this literal as precondition
        RCG.precLists = new int[numOperators][];
        RCG.add2task = new Set[numExtenedStateFeatures]; // pointers from literals to tasks that add it
        RCG.addLists = new int[numOperators][];

        List<Integer>[] inverseMapping = new List[numExtenedStateFeatures];

        for (int i = 0; i < RCG.numExtenedStateFeatures; i++) {
            inverseMapping[i] = new ArrayList<>();
        }

        for (int actionI = 0; actionI < operators.numActions; actionI++) {
            precLists[actionI] = operators.precList[actionI];
            for (int precI = 0; precI < operators.precList[actionI].length; precI++) {
                inverseMapping[operators.precList[actionI][precI]].add(actionI);
            }
        }

        // set number of operator (i.e. action and method) preconditions
        numprecs = new int[RCG.numOperators];
        for (int i = 0; i < operators.numActions; i++) {
            numprecs[i] = operators.precList[i].length;
        }

        for (int methodI = operators.numActions; methodI < numOperators; methodI++) {
            method m = RCG.IndexToMethodGet(methodI);
            numprecs[methodI] = m.numDistinctSubTasks;
            precLists[methodI] = new int[m.subtasks.length];

            for (int subtaskId = 0; subtaskId < m.subtasks.length; subtaskId++) {
                GroundTask t = m.subtasks[subtaskId];
                int taskIndex = RCG.TaskToIndex.get(t);
                precLists[methodI][subtaskId] = taskIndex;
                if (!inverseMapping[taskIndex].contains(methodI)) // this is necessary since there might be methods that have the same subtask twice
                    inverseMapping[taskIndex].add(methodI);
            }

            int compTaskIndex = RCG.TaskToIndex.get(m.m.groundAbstractTask());
            addLists[methodI] = new int[1];
            addLists[methodI][0] = compTaskIndex;
        }

        operatorsWithoutPrec = new UUIntStack();
        for (int i = 0; i < RCG.numOperators; i++) {
            if (RCG.precLists[i].length == 0) {
                operatorsWithoutPrec.push(i);
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
                RCG.addLists[i][j] = operators.addList[i][j];
            }
            RCG.addLists[i][j] = operators.numStateFeatures + i; // actions are located after the original state features and this is the i-th action
        }

        // generate lists mapping literal to lists of operators having it as add-effect
        for (int i = 0; i < numExtenedStateFeatures; i++) {
            RCG.add2task[i] = new HashSet<>();
        }

        for (int i = 0; i < RCG.numOperators; i++) {
            for (int addEffect : addLists[i]) {
                add2task[addEffect].add(i);
            }
        }

        // every action makes the corresponding task-fact true
        System.out.println(" (" + (System.currentTimeMillis() - time) + " ms)");
    }

    private int createTaskLookupTable(Set<GroundTask> allActions, Set<GroundTask> allTasks) {
        int taskNo = operators.numStateFeatures;
        RCG.IndexToTask = new GroundTask[allActions.size() + allTasks.size()];
        RCG.TaskToIndex = new HashMap<>();

        for (GroundTask a : allActions) {
            RCG.IndexToTaskPut(taskNo, a);
            RCG.TaskToIndex.put(a, taskNo);
            taskNo++;
        }

        for (GroundTask t : allTasks) {
            RCG.IndexToTaskPut(taskNo, t);
            RCG.TaskToIndex.put(t, taskNo);
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
        RCG.MethodToIndex = new HashMap<>();

        // count methods, create array
        int anzMethods = 0;
        for (HashMap<GroundTask, List<method>> val : methods.values()) {
            for (List<method> val2 : val.values()) {
                anzMethods += val2.size();
            }
        }
        RCG.IndexToMethod = new method[anzMethods];

        for (HashMap<GroundTask, List<method>> val : methods.values()) {
            for (List<method> val2 : val.values()) {
                for (method m : val2) {
                    assert (!RCG.MethodToIndex.containsKey(m));
                    RCG.MethodToIndex.put(m, methodID);
                    RCG.IndexToMethodPut(methodID, m);
                    methodID++;
                }
            }
        }
        return methodID;
    }

    @Override
    public String getName() {
        return "Relaxed Composition Graph";
    }

    public void build(ProgressionNetwork tn) {
        // The following array stores for every fact in which fact layer it has been made true:
        // [6, 1, 0, -1] means that there are 4 facts, the first one has been made true in layer 6
        // the second in layer 1... The -1 for fact 4 means that this fact has never been made true
        // contains STRIPS as well as HTN facts
        int[] firstLayerWithFact = new int[RCG.numExtenedStateFeatures];

        // operatorDelta is a list of lists. Each inner list contains those actions that are applicable
        // in this layer for the *first time*, i.e. it is the delta of applicable actions
        List<UUIntStack> operatorDelta = new ArrayList<>();

        // goalDelta is a list of lists. Each inner list contains those goal facts that hold in the layer
        // for the *first time*, i.e. it is the delta of fulfilled goal conditions. Be aware that
        // goalDelta is *changed* during heuristic calculation.
        //List<Map<Integer, Integer>> goalDelta = new ArrayList<>();
        List<UUIntStack> goalDelta = new ArrayList<>();
        int[] goalWeight = new int[numExtenedStateFeatures]; // due to the HTN setting, goals may be necessary more than once

        // is used to track how may preconditions are unfulfilled yet
        int[] numOfUnfulfilledPrecs = numprecs.clone();

        // Jörg Hoffmanns measure for choosing a supporter
        // an action's difficulty is the sum of the layers of its preconditions
        int[] actionDifficulty = new int[RCG.numOperators];

        // add literals in s0 to fringe
        UUIntStack changedLiterals = new UUIntStack();
        for (int i = 0; i < operators.numStateFeatures; i++) {
            if (tn.state.get(i)) {
                firstLayerWithFact[i] = 0;
                changedLiterals.push(i);
            } else
                firstLayerWithFact[i] = -1;
        }
        // init facts concerning reachable task
        for (int i = operators.numStateFeatures; i < RCG.numExtenedStateFeatures; i++) {
            firstLayerWithFact[i] = -1;
        }

        // prepare goal - it contains the STRIPS-goal as well as all tasks that appear in the initial HTN
        UUIntStack stripsAndHtnGoals = new UUIntStack();
        for (int i = 0; i < operators.goalList.length; i++) { // these are the state-based goals
            stripsAndHtnGoals.push(operators.goalList[i]);
        }

        // add all tasks in the current network as goal
        Set<GroundTask> tasksInTNI = new HashSet<>(); // these are used for the top-down reachability analysis

        BitSet reachableActions = null; // this is used in top down reachability
        if (topDownReachability) {
            reachableActions = new BitSet(operators.numActions);
        }

        LinkedList<ProgressionPlanStep> temp = new LinkedList<>();
        temp.addAll(tn.getFirstAbstractTasks());
        temp.addAll(tn.getFirstPrimitiveTasks());
        while (!temp.isEmpty()) {
            ProgressionPlanStep ps = temp.removeFirst();
            tasksInTNI.add(ps.getTask());
            int t = RCG.TaskToIndex.get(ps.getTask());
            stripsAndHtnGoals.push(t);
            if (topDownReachability) {
                reachableActions.or(RCG.tdRechability.getReachableActions(t));
            }
            temp.addAll(ps.successorList);
        }

        // a map from an int representing a goal fact to the number of times it is needed
        Map<Integer, Integer> unfulfilledGoals = new HashMap<>();
        stripsAndHtnGoals.resetIterator();
        while (stripsAndHtnGoals.hasNext()) {
            int goalFact = stripsAndHtnGoals.next();
            if (firstLayerWithFact[goalFact] < 0) {
                int count = unfulfilledGoals.containsKey(goalFact) ? unfulfilledGoals.get(goalFact) : 0;
                unfulfilledGoals.put(goalFact, count + 1);
            }
        }

        operatorDelta.add(new UUIntStack()); // dummy list to make indices easier
        goalDelta.add(new UUIntStack(0));

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
            UUIntStack newGoalDelta = new UUIntStack();
            goalDelta.add(newGoalDelta);
            UUIntStack newOperatorDelta = new UUIntStack();
            operatorDelta.add(newOperatorDelta);

            // in first layer, add actions without preconditions
            if (layerId == 1) {
                RCG.operatorsWithoutPrec.resetIterator();
                while (RCG.operatorsWithoutPrec.hasNext()) {
                    int op = RCG.operatorsWithoutPrec.next();
                    if ((!topDownReachability) ||
                            (op >= operators.numActions) // it is a method
                            || (reachableActions.get(op)) // or a reachable action
                            ) {
                        newOperatorDelta.push(op);
                    }
                }
            }

            // fact-loop
            while (!changedLiterals.isEmpty()) {
                final int addedLiteral = changedLiterals.pop();
                for (int actionWithThisPrec : prec2task[addedLiteral]) {
                    if ((topDownReachability) &&
                            (actionWithThisPrec < operators.numActions) // aka it is an action
                            && (!reachableActions.get(actionWithThisPrec)) // that is not reachable anymore via tdg
                            ) {
                        continue; // unreachable via hierarchy
                    }
                    numOfUnfulfilledPrecs[actionWithThisPrec]--;
                    actionDifficulty[actionWithThisPrec] += layerId;
                    if (numOfUnfulfilledPrecs[actionWithThisPrec] == 0) {
                        newOperatorDelta.push(actionWithThisPrec);
                    }
                }
            }

            // operator-loop
            newOperatorDelta.resetIterator();
            while (newOperatorDelta.hasNext()) {
                int newlyApplicableAction = newOperatorDelta.next();
                for (int effect : addLists[newlyApplicableAction]) {
                    if (firstLayerWithFact[effect] < 0) {
                        changedLiterals.push(effect);
                        firstLayerWithFact[effect] = layerId;

                        // test if fact is in goal-list
                        if (unfulfilledGoals.containsKey(effect)) {
                            int add = unfulfilledGoals.remove(effect);
                            goalWeight[effect] += add;
                            newGoalDelta.push(effect);
                        }
                    }
                }
            }
            layerId++;
        }
        //printLayerDelta(operatorDelta, firstLayerWithFact, goalDelta);

        if (goalRelaxedReachable) {
            this.heuristicValue = calcHeu(firstLayerWithFact, operatorDelta, goalWeight, actionDifficulty, goalDelta);
        } else {
            this.heuristicValue = Integer.MAX_VALUE;
        }
    }

    private int calcHeu(int[] firstLayerWithFact, List<UUIntStack> operatorDelta, int[] goalWeight, int[] actionDifficulty, List<UUIntStack> goalDelta) {
        int numactions = 0;
        for (int layer = goalDelta.size() - 1; layer >= 1; layer--) {
            UUIntStack oneGoalDelta = goalDelta.get(layer);

            oneGoalDelta.resetIterator();
            while (oneGoalDelta.hasNext()) {
                int goalFact = oneGoalDelta.next();
                int count = goalWeight[goalFact];
                int bestDifficulty = Integer.MAX_VALUE;
                int producer = -1;

                UUIntStack opDelta = operatorDelta.get(layer);
                opDelta.resetIterator();
                while (opDelta.hasNext()) {
                    int maybeProducer = opDelta.next();
                    if (add2task[goalFact].contains(maybeProducer)) {

                        if (prod == producerSelection.numOfPreconditions) {
                            if (numprecs[maybeProducer] < bestDifficulty) {
                                bestDifficulty = RCG.precLists[maybeProducer].length;
                                producer = maybeProducer;
                            }
                        } else if (prod == producerSelection.actionDifficulty) {
                            if (actionDifficulty[maybeProducer] < bestDifficulty) {
                                bestDifficulty = actionDifficulty[maybeProducer];
                                producer = maybeProducer;
                            }
                        } else {
                            producer = maybeProducer;
                            break;
                        }
                    }
                }

                if (!operators.ShopPrecActions.contains(producer)) {
                    numactions += count;
                }

                for (int aPrec : RCG.precLists[producer]) {
                    int flayer = firstLayerWithFact[aPrec];
                    if (flayer > 0) {
                        if (goalWeight[aPrec] == 0) {
                            UUIntStack delta = goalDelta.get(flayer);
                            delta.push(aPrec);
                        }
                        if (aPrec > operators.numActions) // this is an HTN-precondition
                            goalWeight[aPrec] += count;
                        else // a normal precondition
                            goalWeight[aPrec]++;
                    }
                }
            }
        }
        return numactions;
    }

    private String printLayerDelta(List<UUIntStack> operatorDelta, int[] firstLayerWithFact, List<Map<Integer, Integer>> goalDelta) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < operatorDelta.size(); i++) {
            if (i >= 0) {
                sb.append("Operator Delta " + i + ": \n");
                boolean first = true;
                UUIntStack opDelta = operatorDelta.get(i);
                opDelta.resetIterator();
                while (opDelta.hasNext()) {
                    int action = opDelta.next();
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", \n");
                    }
                    sb.append(" - ");
                    if (action < operators.numActions) {
                        sb.append(operators.IndexToAction[action].mediumInfo());
                    } else
                        sb.append(RCG.IndexToMethodGet(action).m.mediumInfo());
                }
            }

            sb.append("\n\nFact Delta " + i + ": ");
            boolean first = true;
            for (int j = 0; j < firstLayerWithFact.length; j++) {
                if (firstLayerWithFact[j] == i) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", \n");
                    }
                    sb.append(" - ");
                    if (j < operators.numStateFeatures) {
                        sb.append(operators.IndexToLiteral[j].mediumInfo());
                    } else
                        sb.append(RCG.IndexToTaskGet(j).mediumInfo());
                }
            }

            sb.append("\n\nGoal Delta " + i + ": ");
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
                    sb.append(RCG.IndexToTaskGet(fact).mediumInfo());
                if (goalDelta.get(i).get(fact) > 1) {
                    sb.append(" * " + goalDelta.get(i).get(fact));
                }
            }

            sb.append("\n\n\n");
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
        RCG crpg = new RCG();
        crpg.build(tn);
        return crpg;
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork tn, ProgressionPlanStep ps) {
        RCG crpg = new RCG();
        crpg.build(tn);
        return crpg;
    }
}