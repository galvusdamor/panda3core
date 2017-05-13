package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.proUtil.UUIntStack;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import jdk.nashorn.internal.runtime.BitVector;

import java.util.*;

/**
 * Created by dhoeller on 26.07.16.
 */
public class RCG implements htnGroundedProgressionHeuristic {
    public static enum producerSelection {
        numOfPreconditions, actionDifficulty, firstCome;

        public static producerSelection parse(String text) {
            if (text.equals("#prec")) return numOfPreconditions;
            if (text.equals("action-difficulty")) return actionDifficulty;
            if (text.equals("fcfs")) return firstCome;
            throw new IllegalArgumentException("Unknown selector " + text);
        }
    }

    public static enum heuristicExtraction {
        ff, multicount;

        public static heuristicExtraction parse(String text) {
            if (text.equals("ff")) return ff;
            if (text.equals("multicount")) return multicount;
            throw new IllegalArgumentException("Unknown extraction method " + text);
        }

    }

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


    private static HashMap<Task, Integer> TaskToIndex;
    private static Task[] IndexToTask;

    private static TopDownReachabilityGraph tdRechability;

    private static method IndexToMethodGet(int index) {
        return IndexToMethod[index - ProgressionNetwork.flatProblem.numOfOperators];
    }

    private static Task IndexToTaskGet(int index) {
        return IndexToTask[index - ProgressionNetwork.flatProblem.numOfStateFeatures];
    }

    private static void IndexToTaskPut(int index, Task t) {
        IndexToTask[index - ProgressionNetwork.flatProblem.numOfStateFeatures] = t;
    }

    private static void IndexToMethodPut(int index, method m) {
        IndexToMethod[index - ProgressionNetwork.flatProblem.numOfOperators] = m;
    }

    // Members of the current object
    private boolean goalRelaxedReachable;
    private int heuristicValue;

    private RCG() { // only used by factory methods -> private
    }

    public static boolean topDownReachability = true;
    boolean orderingInvariants = false;

    static producerSelection prod = producerSelection.actionDifficulty;
    static heuristicExtraction heuEx = heuristicExtraction.multicount;

    public RCG(HashMap<Task, List<method>> methods, List<ProgressionPlanStep> initialTasks, Set<Task> allActions, boolean useTDReachability,
               producerSelection selectionStrategy, heuristicExtraction heuEx) {
        RCG.prod = selectionStrategy;
        RCG.heuEx = heuEx;
        RCG.topDownReachability = useTDReachability;

        long time = System.currentTimeMillis();
        System.out.println("Init Relaxed Composition Graph (RCG) heuristic");

        RCG.numOperators = createMethodLookupTable(methods);
        RCG.numTasks = createTaskLookupTable(allActions, getGroundTasks(methods)) - ProgressionNetwork.flatProblem.numOfStateFeatures;

        if (topDownReachability || orderingInvariants) {
            tdRechability = new TopDownReachabilityGraph(methods, initialTasks, RCG.numTasks, RCG.numOperators);
            //tdRechability.calcOrderingInvariants(RCG.numTasks,methods, IndexToTask);
        }

        // action-task-facts are true one layer after the action, this can done due to the 1-to-1 correspondence
        RCG.numExtenedStateFeatures = ProgressionNetwork.flatProblem.numOfStateFeatures + RCG.numTasks;

        RCG.prec2task = new int[numExtenedStateFeatures][]; // pointers from literals to tasks that have this literal as precondition
        RCG.precLists = new int[numOperators][];
        RCG.add2task = new Set[numExtenedStateFeatures]; // pointers from literals to tasks that add it
        RCG.addLists = new int[numOperators][];

        List<Integer>[] inverseMapping = new List[numExtenedStateFeatures];

        for (int i = 0; i < RCG.numExtenedStateFeatures; i++) {
            inverseMapping[i] = new ArrayList<>();
        }

        for (int actionI = 0; actionI < ProgressionNetwork.flatProblem.numOfOperators; actionI++) {
            precLists[actionI] = ProgressionNetwork.flatProblem.precLists[actionI];
            for (int precI = 0; precI < ProgressionNetwork.flatProblem.precLists[actionI].length; precI++) {
                inverseMapping[ProgressionNetwork.flatProblem.precLists[actionI][precI]].add(actionI);
            }
        }

        // set number of operator (i.e. action and method) preconditions
        numprecs = new int[RCG.numOperators];
        for (int i = 0; i < ProgressionNetwork.flatProblem.numOfOperators; i++) {
            numprecs[i] = ProgressionNetwork.flatProblem.precLists[i].length;
        }

        for (int methodI = ProgressionNetwork.flatProblem.numOfOperators; methodI < numOperators; methodI++) {
            method m = RCG.IndexToMethodGet(methodI);
            numprecs[methodI] = m.numDistinctSubTasks;
            precLists[methodI] = new int[m.subtasks.length];

            for (int subtaskId = 0; subtaskId < m.subtasks.length; subtaskId++) {
                Task t = m.subtasks[subtaskId];
                int taskIndex = RCG.TaskToIndex.get(t);
                precLists[methodI][subtaskId] = taskIndex;
                if (!inverseMapping[taskIndex].contains(methodI)) // this is necessary since there might be methods that have the same subtask twice
                    inverseMapping[taskIndex].add(methodI);
            }

            int compTaskIndex = RCG.TaskToIndex.get(m.m.abstractTask());
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
        for (int i = 0; i < ProgressionNetwork.flatProblem.numOfOperators; i++) {
            addLists[i] = new int[ProgressionNetwork.flatProblem.addLists[i].length + 1];
            int j;
            for (j = 0; j < ProgressionNetwork.flatProblem.addLists[i].length; j++) {
                RCG.addLists[i][j] = ProgressionNetwork.flatProblem.addLists[i][j];
            }
            RCG.addLists[i][j] = ProgressionNetwork.flatProblem.numOfStateFeatures + i; // actions are located after the original state features and this is the i-th action
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

    private int createTaskLookupTable(Set<Task> allActions, Set<Task> allTasks) {
        int taskNo = ProgressionNetwork.flatProblem.numOfStateFeatures;
        RCG.IndexToTask = new Task[allActions.size() + allTasks.size()];
        RCG.TaskToIndex = new HashMap<>();

        for (Task a : allActions) {
            RCG.IndexToTaskPut(taskNo, a);
            RCG.TaskToIndex.put(a, taskNo);
            taskNo++;
        }

        for (Task t : allTasks) {
            RCG.IndexToTaskPut(taskNo, t);
            RCG.TaskToIndex.put(t, taskNo);
            taskNo++;
        }
        return taskNo;
    }

    public static Set<Task> getGroundTasks(HashMap<Task, List<method>> methods) {
        Set<Task> allTasks = new HashSet<>();
        for (Task t : methods.keySet()) {
            allTasks.add(t);
        }
        return allTasks;
    }

    /**
     * Assigns an index to each method and fills the data structures that are used for the translation.
     *
     * @param methods
     * @return
     */
    private int createMethodLookupTable(HashMap<Task, List<method>> methods) {
        int methodID = ProgressionNetwork.flatProblem.numOfOperators;
        RCG.MethodToIndex = new HashMap<>();

        // count methods, create array
        int anzMethods = 0;
        for (List<method> val2 : methods.values()) {
            anzMethods += val2.size();
        }
        RCG.IndexToMethod = new method[anzMethods];

        for (List<method> val2 : methods.values()) {
            for (method m : val2) {
                assert (!RCG.MethodToIndex.containsKey(m));
                RCG.MethodToIndex.put(m, methodID);
                RCG.IndexToMethodPut(methodID, m);
                methodID++;
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
        for (int i = 0; i < ProgressionNetwork.flatProblem.numOfStateFeatures; i++) {
            if (tn.state.get(i)) {
                firstLayerWithFact[i] = 0;
                changedLiterals.push(i);
            } else
                firstLayerWithFact[i] = -1;
        }
        // init facts concerning reachable task
        for (int i = ProgressionNetwork.flatProblem.numOfStateFeatures; i < RCG.numExtenedStateFeatures; i++) {
            firstLayerWithFact[i] = -1;
        }

        // prepare goal - it contains the STRIPS-goal as well as all tasks that appear in the initial HTN
        UUIntStack stripsAndHtnGoals = new UUIntStack();
        for (int i = 0; i < ProgressionNetwork.flatProblem.gList.length; i++) { // these are the state-based goals
            stripsAndHtnGoals.push(ProgressionNetwork.flatProblem.gList[i]);
        }

        // add all tasks in the current network as goal
        Set<Task> tasksInTNI = new HashSet<>(); // these are used for the top-down reachability analysis

        BitSet reachableActions = null; // this is used in top down reachability
        if (topDownReachability) {
            reachableActions = new BitSet(ProgressionNetwork.flatProblem.numOfOperators);
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
                            (op >= ProgressionNetwork.flatProblem.numOfOperators) // it is a method
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
                            (actionWithThisPrec < ProgressionNetwork.flatProblem.numOfOperators) // aka it is an action
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

        BitVector trueI;
        BitVector trueImm = new BitVector(numExtenedStateFeatures);
        for (int layer = goalDelta.size() - 1; layer >= 1; layer--) {
            trueI = trueImm;
            trueImm = new BitVector(numExtenedStateFeatures);
            UUIntStack oneGoalDelta = goalDelta.get(layer);
            UUIntStack postponed = new UUIntStack();
            oneGoalDelta.resetIterator();

            boolean switched = false;
            boolean loop = oneGoalDelta.hasNext();
            while (loop) {
                int goalFact;
                if (heuEx == heuristicExtraction.ff) { // ordinary FF extraction
                    goalFact = oneGoalDelta.next();
                    loop = oneGoalDelta.hasNext();
                    if (trueI.isSet(goalFact))
                        continue;
                } else { // count HTN actions more than once
                    if (oneGoalDelta.hasNext()) {
                        goalFact = oneGoalDelta.next();
                        if (goalFact < ProgressionNetwork.flatProblem.numOfStateFeatures) {
                            postponed.push(goalFact);
                            continue;
                        }
                    } else {
                        if (!switched) { // at first time, there must be an initialization
                            postponed.resetIterator();
                            switched = true;
                        }
                        if (postponed.hasNext()) {
                            goalFact = postponed.next();
                            if ((goalFact < ProgressionNetwork.flatProblem.numOfStateFeatures) && (trueI.isSet(goalFact)))
                                continue; // fact already there
                        } else {
                            loop = false;
                            continue;
                        }
                    }
                }

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

                if (!ProgressionNetwork.ShopPrecActions.contains(producer)) {
                    for (int i = 0; i < addLists[producer].length; i++) {
                        if ((goalFact < ProgressionNetwork.flatProblem.numOfStateFeatures) || (heuEx == heuristicExtraction.ff)) {
                            trueI.set(addLists[producer][i]);
                            trueImm.set(addLists[producer][i]);
                        }
                    }

                    if (heuEx == heuristicExtraction.ff)
                        numactions++;
                    else
                        numactions += count;
                }

                for (int aPrec : RCG.precLists[producer]) {
                    if (trueImm.isSet(aPrec))
                        continue;
                    int flayer = firstLayerWithFact[aPrec];
                    if (flayer > 0) {
                        if (goalWeight[aPrec] == 0) {
                            UUIntStack delta = goalDelta.get(flayer);
                            delta.push(aPrec);
                        }
                        if (aPrec > ProgressionNetwork.flatProblem.numOfStateFeatures) // this is an HTN-related precondition
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
                    if (action < ProgressionNetwork.flatProblem.numOfOperators) {
                        sb.append(ProgressionNetwork.indexToTask[action].mediumInfo());
                    } else
                        sb.append(RCG.IndexToMethodGet(action).m.name());
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
                    if (j < ProgressionNetwork.flatProblem.numOfStateFeatures) {
                        sb.append("fact" +  j);
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
                if (fact < ProgressionNetwork.flatProblem.numOfStateFeatures) {
                    sb.append("fact" +  fact);
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
