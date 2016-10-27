package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;

import java.util.*;

/**
 * Created by dhoeller on 26.07.16.
 * <p/>
 * (iter) The rpg gets its set of grounded actions from the symbolic graph. So it does not ground
 * itself. This might cause the algorithm to test to much actions. It is planned to implement
 * a version that iteratively adapts the existing structures - this should also overcome the
 * problem given here
 * <p/>
 * (ac-rep) The representation of actions' preconditions and effects via Bit-Vectors might be suboptimal
 * whenever there are only a few state features effected - maybe implement a version that only
 * represents the states as Bit-Vectors, but uses Booleans for preconditions and effects.
 * (see also the planningInstance-Class)
 */
public class cRPG implements htnGroundedProgressionHeuristic {

    // STATIC STUFF
    private static int numOperators;
    private static int numHtnStateFeatures;

    // [f1, f2, ..., fm, a1, a2, c1, a3, ..., an, c2, ..., co]
    public static int[][] prec2task; // [1][4, 5, 7] means that the tasks 4, 5 and 7 all have precondition 1
    public static Set<Integer>[] add2task; // 1 -> [4, 5, 7] means that the tasks 4, 5 and 7 all add fact no. 1
    public static int[] numprecs;

    public static List<Integer> operatorsWithoutPrec;

    // [a1, a2, ..., an, m1, m2, ..., mp]
    private static int[][] precLists;
    public static int[][] addLists;  // [1][2, 5] means that action 1 adds state-features 2 and 5

    // these are maps because its indices to not start with 0
    private static HashMap<method, Integer> MethodToIndex;
    private static HashMap<Integer, method> IndexToMethod;

    private static HashMap<GroundTask, Integer> TaskLiteralToIndex;
    private static HashMap<Integer, GroundTask> IndexToTaskLiteral;

    private static Map<GroundTask, Set<Integer>> reachableFrom;

    // Members of the current object
    private boolean goalRelaxedReachable;
    private BitSet reachability;
    private int heuristicValue;

    public cRPG() {

    }

    public cRPG(HashMap<Task, HashMap<GroundTask, List<method>>> methods, Set<GroundTask> allActions) {

        long time = System.currentTimeMillis();
        System.out.print("Init composition RPG heuristic");

        cRPG.numOperators = createMethodLookupTable(methods);
        this.reachability = new BitSet(cRPG.numOperators);
        this.reachability.set(0, this.reachability.size() - 1, true);
        createTaskLookupTable(allActions, getGroundTasks(methods));

        Map<GroundTask, Set<GroundTask>> reachableTasksFrom = new HashMap<>();
        //Map<GroundTask, Set<GroundTask>> parent = new HashMap<>();
        //LinkedList<GroundTask> doTasks = new LinkedList<>();

        long time42 = System.currentTimeMillis();
        for (Task k : methods.keySet()) {
            HashMap<GroundTask, List<method>> methodLists = methods.get(k);
            for (GroundTask t : methodLists.keySet()) {
                if (!reachableTasksFrom.containsKey(t)) {
                    reachableTasksFrom.put(t, new HashSet<>());
                }
                Set<GroundTask> subtasks = reachableTasksFrom.get(t);
                List<method> methodsT = methodLists.get(t);
                for (method mths : methodsT) {
                    for (GroundTask gt : mths.tasks) {
                        subtasks.add(gt);
                    }
                }
            }
        }
        System.out.println("\nfirst " + (System.currentTimeMillis() - time42));
        time42 = System.currentTimeMillis();

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
        System.out.println("sec " + (System.currentTimeMillis() - time42));
        time42 = System.currentTimeMillis();

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

        System.out.println("third " + (System.currentTimeMillis() - time42));
        cRPG.reachableFrom = reachableFrom;

        cRPG.numHtnStateFeatures = operators.numStateFeatures + cRPG.numOperators; // action-task-facts are true one layer after the action, this can done due to the 1-to-1 correspondence

        cRPG.prec2task = new int[numHtnStateFeatures][]; // pointers from literals to tasks that have this literal as precondition
        cRPG.add2task = new Set[numHtnStateFeatures]; // pointers from literals to tasks that add it
        cRPG.precLists = new int[numOperators][];
        cRPG.addLists = new int[numOperators][];

        List<Integer>[] invertedMapping = new List[numHtnStateFeatures];

        for (int i = 0; i < cRPG.numHtnStateFeatures; i++) {
            invertedMapping[i] = new ArrayList<>();
        }

        for (int actionI = 0; actionI < operators.numActions; actionI++) {
            precLists[actionI] = operators.precList[actionI];
            for (int precI = 0; precI < operators.precList[actionI].length; precI++) {
                invertedMapping[operators.precList[actionI][precI]].add(actionI);
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
                int taskIndex = cRPG.TaskLiteralToIndex.get(t);
                precLists[methodI][subtaskId] = taskIndex;
                invertedMapping[taskIndex].add(methodI);
            }
            int compTaskIndex = cRPG.TaskLiteralToIndex.get(m.m.groundAbstractTask());
            addLists[methodI] = new int[1];
            addLists[methodI][0] = compTaskIndex;
        }

        operatorsWithoutPrec = new ArrayList<>();
        for (int i = 0; i < cRPG.numOperators; i++) {
            if (cRPG.precLists[i].length == 0)
                operatorsWithoutPrec.add(i);
        }

        // copy temporal lists to array
        for (int i = 0; i < invertedMapping.length; i++) {
            List<Integer> mapping = invertedMapping[i];
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
        for (int i = 0; i < numHtnStateFeatures; i++) {
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

    private void createTaskLookupTable(Set<GroundTask> allActions, Set<GroundTask> allTasks) {
        int taskNo = operators.numStateFeatures;
        cRPG.IndexToTaskLiteral = new HashMap<>();
        cRPG.TaskLiteralToIndex = new HashMap<>();

        for (GroundTask a : allActions) {
            cRPG.IndexToTaskLiteral.put(taskNo, a);
            cRPG.TaskLiteralToIndex.put(a, taskNo);
            taskNo++;
        }

        for (GroundTask t : allTasks) {
            cRPG.IndexToTaskLiteral.put(taskNo, t);
            cRPG.TaskLiteralToIndex.put(t, taskNo);
            taskNo++;
        }
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
        int[] firstLayerWithFact = new int[cRPG.numHtnStateFeatures];

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
        for (int i = operators.numStateFeatures; i < cRPG.numHtnStateFeatures; i++) {
            firstLayerWithFact[i] = -1;
        }

        // prepare goal - it contains the STRIPS-goal as well as all tasks that appear in the initial HTN
        List<Integer> htnGoal = new LinkedList<>();
        for (int i = 0; i < operators.goalList.length; i++) { // these are the state-based goals
            htnGoal.add(operators.goalList[i]);
        }

        // add all tasks in the current network as goal
        Set<GroundTask> allTasksInNet = new HashSet<>();

        LinkedList<ProgressionPlanStep> tasksInHtn = new LinkedList<>();
        tasksInHtn.addAll(tn.getFirstAbstractTasks());
        tasksInHtn.addAll(tn.getFirstPrimitiveTasks());
        while (!tasksInHtn.isEmpty()) {
            ProgressionPlanStep ps = tasksInHtn.removeFirst();
            allTasksInNet.add(ps.getTask());
            htnGoal.add(cRPG.TaskLiteralToIndex.get(ps.getTask()));
            tasksInHtn.addAll(ps.successorList);
        }

        //LinkedList<Integer> unfulfilledGoals = new LinkedList<>();
        Map<Integer, Integer> unfulfilledGoals = new HashMap<>();

        // todo !evaluate!: this might be a set or a list
        //List<Integer> newGoals = new ArrayList<>();
        //HashSet<Integer> newGoals = new HashSet<>();
        HashMap<Integer, Integer> newGoals = new HashMap<>();

        goalDelta.add(newGoals); // todo: (operator-facts are not marked applicable yet) should only test non-operator-facts
        for (int goalFact : htnGoal) {
            if (firstLayerWithFact[goalFact] == 0) {
                int count = newGoals.containsKey(goalFact) ? newGoals.get(goalFact) : 0;
                newGoals.put(goalFact, count + 1);
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
            newGoals = new HashMap<>();
            goalDelta.add(newGoals);
            if (changedLiterals.isEmpty()) {
                // there are unfulfilled goals, but the state did not change -> relaxed unsolvable
                this.goalRelaxedReachable = false;
                break;
            }

            List<Integer> newlyApplicableActions = new LinkedList<>();
            operatorDelta.add(newlyApplicableActions);
            if (layerId == 1) { // there might be actions without preconditions
                newlyApplicableActions.addAll(cRPG.operatorsWithoutPrec);
            }

            // fact-loop
            while (!changedLiterals.isEmpty()) {
                final int addedLiteral = changedLiterals.removeFirst();
                for (int actionWithThisPrec : prec2task[addedLiteral]) {
                    if (!this.reachability.get(actionWithThisPrec)) {
                        continue; // unreachable via hierarchy
                    }
                    numOfUnfulfilledPrecs[actionWithThisPrec]--;
                    actionDifficulty[actionWithThisPrec] += layerId;
                    if (numOfUnfulfilledPrecs[actionWithThisPrec] == 0) {
                        newlyApplicableActions.add(actionWithThisPrec);
                    }
                }
            }

            // operator-loop
            for (int newlyApplicableAction : newlyApplicableActions) {
                boolean reachable = false;
                reachabilityloop:
                for (GroundTask htnTask : allTasksInNet) {
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
                if (reachable) {
                    for (int effect : addLists[newlyApplicableAction]) {
                        if (firstLayerWithFact[effect] < 0) {
                            changedLiterals.add(effect);
                            firstLayerWithFact[effect] = layerId;

                            // test if fact is in goal-list
                            if (unfulfilledGoals.containsKey(effect)) {
                                int add = unfulfilledGoals.remove(effect);
                                int count = newGoals.containsKey(effect) ? newGoals.get(effect) : 0;
                                newGoals.put(effect, count + add);
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
        //printLayerDelta(operatorDelta, firstLayerWithFact);

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

    private String printLayerDelta(List<List<Integer>> operatorDelta, int[] firstLayerWithFact, List<List<Integer>> goalDelta) {
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
                        sb.append(cRPG.IndexToTaskLiteral.get(j).mediumInfo());
                }
            }

            sb.append("\n    Goal Delta " + i + ": ");
            first = true;
            for (int j = 0; j < goalDelta.get(i).size(); j++) {
                int fact = goalDelta.get(i).get(j);
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                if (fact < operators.numStateFeatures) {
                    sb.append(operators.IndexToLiteral[fact].mediumInfo());
                } else
                    sb.append(cRPG.IndexToTaskLiteral.get(fact).mediumInfo());
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
        crpg.reachability = (BitSet) this.reachability.clone();
        crpg.build(tn);
        return crpg;
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork tn, ProgressionPlanStep ps) {
        cRPG crpg = new cRPG();
        crpg.reachability = (BitSet) this.reachability.clone();
        crpg.build(tn);
        return crpg;
    }
}
