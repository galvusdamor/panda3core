package de.uniulm.ki.panda3.progression.relaxedPlanningGraph.hierarchyAware;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.htnGroundedProgressionHeuristic;
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
public class cRpgHtn implements htnGroundedProgressionHeuristic {

    // STATIC STUFF
    private static int numOperators;
    private static int numHtnStateFeatures;

    // [f1, f2, ..., fm, a1, a2, c1, a3, ..., an, c2, ..., co]
    public static int[][] prec2task; // [1][4, 5, 7] means that the tasks 4, 5 and 7 all have precondition 1
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


    // Members of the current object
    private boolean goalRelaxedReachable;
    private int heuristicValue;

    public cRpgHtn() {

    }

    public cRpgHtn(HashMap<Task, HashMap<GroundTask, List<method>>> methods, Set<GroundTask> allActions) {
        long time = System.currentTimeMillis();
        System.out.print("Init composition RPG heuristic");

        cRpgHtn.numOperators = createMethodLookupTable(methods);
        createTaskLookupTable(allActions, getGroundTasks(methods));

        cRpgHtn.numHtnStateFeatures = operators.numStateFeatures + cRpgHtn.numOperators; // action-task-facts are true one layer after the action, this can done due to the 1-to-1 correspondence

        cRpgHtn.prec2task = new int[numHtnStateFeatures][]; // pointers from literals to tasks that have this literal as precondition
        cRpgHtn.precLists = new int[numOperators][];
        cRpgHtn.addLists = new int[numOperators][];

        List<Integer>[] invertedMapping = new List[numHtnStateFeatures];

        for (int i = 0; i < cRpgHtn.numHtnStateFeatures; i++) {
            invertedMapping[i] = new ArrayList<>();
        }

        for (int actionI = 0; actionI < operators.numActions; actionI++) {
            precLists[actionI] = operators.precList[actionI];
            for (int precI = 0; precI < operators.precList[actionI].length; precI++) {
                invertedMapping[operators.precList[actionI][precI]].add(actionI);
            }
        }

        // set number of action preconditions
        numprecs = new int[cRpgHtn.numOperators];
        for (int i = 0; i < operators.numActions; i++) {
            numprecs[i] = operators.precList[i].length;
        }

        for (int methodI : cRpgHtn.IndexToMethod.keySet()) {
            method m = cRpgHtn.IndexToMethod.get(methodI);
            numprecs[methodI] = m.tasks.length;
            precLists[methodI] = new int[numprecs[methodI]];

            for (int subtaskId = 0; subtaskId < m.tasks.length; subtaskId++) {
                GroundTask t = m.tasks[subtaskId];
                int taskIndex = cRpgHtn.TaskLiteralToIndex.get(t);
                precLists[methodI][subtaskId] = taskIndex;
                invertedMapping[taskIndex].add(methodI);
            }
            int compTaskIndex = cRpgHtn.TaskLiteralToIndex.get(m.m.groundAbstractTask());
            addLists[methodI] = new int[1];
            addLists[methodI][0] = compTaskIndex;
        }

        operatorsWithoutPrec = new ArrayList<>();
        for (int i = 0; i < cRpgHtn.numOperators; i++) {
            if (cRpgHtn.precLists[i].length == 0)
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
                cRpgHtn.addLists[i][j] = operators.addList[i][j];
            }
            cRpgHtn.addLists[i][j] = operators.numStateFeatures + i; // actions are located after the original state features and this is the i-th action
        }

        // every action makes the corresponding task-fact true
        System.out.println(" (" + (System.currentTimeMillis() - time) + " ms)");
    }

    private void createTaskLookupTable(Set<GroundTask> allActions, Set<GroundTask> allTasks) {
        int taskNo = operators.numStateFeatures;
        cRpgHtn.IndexToTaskLiteral = new HashMap<>();
        cRpgHtn.TaskLiteralToIndex = new HashMap<>();

        for (GroundTask a : allActions) {
            cRpgHtn.IndexToTaskLiteral.put(taskNo, a);
            cRpgHtn.TaskLiteralToIndex.put(a, taskNo);
            taskNo++;
        }

        for (GroundTask t : allTasks) {
            cRpgHtn.IndexToTaskLiteral.put(taskNo, t);
            cRpgHtn.TaskLiteralToIndex.put(t, taskNo);
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
        cRpgHtn.MethodToIndex = new HashMap<>();
        cRpgHtn.IndexToMethod = new HashMap<>();
        for (HashMap<GroundTask, List<method>> val : methods.values()) {
            for (List<method> val2 : val.values()) {
                for (method m : val2) {
                    assert (!cRpgHtn.MethodToIndex.containsKey(m));
                    cRpgHtn.MethodToIndex.put(m, methodID);
                    cRpgHtn.IndexToMethod.put(methodID, m);
                    methodID++;
                }
            }
        }
        return methodID;
    }

    @Override
    public String getName() {
        return "cRpgHtn";
    }

    public void build(ProgressionNetwork tn) {
        // The following array stores for every fact in which fact layer it has been made true:
        // [6, 1, 0, -1] means that there are 4 facts, the first one has been made true in layer 6
        // the second in layer 1... The -1 for fact 4 means that this fact has never been made true
        // contains STRIPS as well as HTN facts
        List<Integer>[] layersWithFact = new List[cRpgHtn.numHtnStateFeatures];

        // operatorDelta is a list of lists. Each inner list contains those actions that are applicable
        // in this layer for the *first time*, i.e. it is the delta of applicable actions
        List<List<Integer>> operatorDelta = new ArrayList<>();

        // goalDelta is a list of lists. Each inner list contains those goal facts that hold in the layer
        // for the *first time*, i.e. it is the delta of fulfilled goal conditions. Be aware that
        // goalDelta is *changed* during heuristic calculation.
        List<List<Integer>> goalDelta = new ArrayList<>();

        // is used to track how may preconditions are unfulfilled yet
        int[] numOfUnfulfilledPrecs = numprecs.clone();

        // Jörg Hoffmanns measure for choosing a supporter
        // an action's difficulty is the sum of the layers of its preconditions
        int[] actionDifficulty = new int[cRpgHtn.numOperators];

        // add literals in s0 to fringe
        LinkedList<Integer> changedLiterals = new LinkedList<>();
        for (int i = 0; i < operators.numStateFeatures; i++) {
            layersWithFact[i] = new LinkedList<>();
            if (tn.state.get(i)) {
                layersWithFact[i].add(0);
                changedLiterals.add(i);
            }
        }
        // init facts concerning reachable task
        for (int i = operators.numStateFeatures; i < cRpgHtn.numHtnStateFeatures; i++) {
            layersWithFact[i] = new LinkedList<>();
        }

        // prepare goal - it contains the STRIPS-goal as well as all tasks that appear in the initial HTN
        List<Integer> htnGoal = new LinkedList<>();
        for (int i = 0; i < operators.goalList.length; i++) { // these are the state-based goals
            htnGoal.add(operators.goalList[i]);
        }

        // add all tasks in the current network as goal
        LinkedList<ProgressionPlanStep> goalFacts = new LinkedList<>();
        goalFacts.addAll(tn.getFirst());
        while (!goalFacts.isEmpty()) {
            ProgressionPlanStep ps = goalFacts.removeFirst();
            htnGoal.add(cRpgHtn.TaskLiteralToIndex.get(ps.getTask()));
            goalFacts.addAll(ps.successorList);
        }

        LinkedList<Integer> unfulfilledGoals = new LinkedList<>();

        // todo !evaluate!: this might be a set or a list
        List<Integer> newGoals = new ArrayList<>();
        //HashSet<Integer> newGoals = new HashSet<>();

        goalDelta.add(newGoals); // todo: (operator-facts are not marked applicable yet) should only test non-operator-facts
        for (int goalFact : htnGoal) {
            if (layersWithFact[goalFact].size() > 0) {
                newGoals.add(goalFact);
            } else {
                unfulfilledGoals.add(goalFact);
            }
        }

        operatorDelta.add(new LinkedList<>()); // dummy list to make indices easier

        // start building the graph
        int layerId = 1;
        this.goalRelaxedReachable = true;
        while (!unfulfilledGoals.isEmpty()) {
            newGoals = new ArrayList<>();
            goalDelta.add(newGoals);
            if (changedLiterals.isEmpty()) {
                // there are unfulfilled goals, but the state did not change -> relaxed unsolvable
                this.goalRelaxedReachable = false;
                break;
            }

            List<Integer> newlyApplicableActions = new ArrayList<>();
            operatorDelta.add(newlyApplicableActions);
            if (layerId == 1) { // there might be actions without preconditions
                newlyApplicableActions.addAll(cRpgHtn.operatorsWithoutPrec);
            }

            // fact-loop
            while (!changedLiterals.isEmpty()) {
                final int addedLiteral = changedLiterals.removeFirst();
                for (int actionWithThisPrec : prec2task[addedLiteral]) {
                    numOfUnfulfilledPrecs[actionWithThisPrec]--;
                    actionDifficulty[actionWithThisPrec] += layerId;
                    if (numOfUnfulfilledPrecs[actionWithThisPrec] == 0) {
                        newlyApplicableActions.add(actionWithThisPrec);
                    }
                }
            }

            // operator-loop
            for (int newlyApplicableAction : newlyApplicableActions) {
                for (int effect : addLists[newlyApplicableAction]) {
                    changedLiterals.add(effect);
                    layersWithFact[effect].add(layerId);

                    // test if fact is in goal-list
                    if (unfulfilledGoals.remove(new Integer(effect))) {
                        newGoals.add(effect);
                        while (unfulfilledGoals.remove(new Integer(effect))) {
                            newGoals.add(effect);
                        }
                    }
                }
            }
            layerId++;
        }
        //printLayerDelta(operatorDelta, firstLayerWithFact);

        if (goalRelaxedReachable) {
            this.heuristicValue = calcHeu(layersWithFact, operatorDelta, actionDifficulty, goalDelta);
        } else {
            this.heuristicValue = Integer.MAX_VALUE;
        }
    }

    private int calcHeu(List<Integer>[] firstLayerWithFact, List<List<Integer>> operatorDelta, int[] actionDifficulty, List<List<Integer>> goalDelta) {
        //String s = printLayerDelta(operatorDelta, firstLayerWithFact);
        //int numactions = 0;
        /*
        for (int layer = goalDelta.size() - 1; layer >= 1; layer--) {
            for (int goalFact : goalDelta.get(layer)) {

                // todo: the extraction of the producer might be written more optimized
                for (int maybeProducer : operatorDelta.get(layer)) {
                    for (int eff : cRpgHtn.addLists[maybeProducer]) {
                        if ((eff == goalFact) && (actionDifficulty[maybeProducer] < bestDifficulty)) {
                            bestDifficulty = actionDifficulty[maybeProducer];
                            producer = maybeProducer;
                        }
                    }
                }
                numactions++;
                for (Integer aPrec : cRpgHtn.precLists[producer]) {
                    int fl = firstLayerWithFact[aPrec];
                    if (fl > 0) {
                        goalDelta.get(fl).add(aPrec);
                    }
                }
            }
        }*/
        return 0;
    }

    private String printLayerDelta(List<List<Integer>> operatorDelta, int[] firstLayerWithFact) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < operatorDelta.size(); i++) {
            if (i >= 0) {
                sb.append("Operator Layer " + i + ": ");
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
                        sb.append(cRpgHtn.IndexToMethod.get(action).m.mediumInfo());
                }
            }

            sb.append("\n    Fact Layer " + i + ": ");
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
                        sb.append(cRpgHtn.IndexToTaskLiteral.get(j).mediumInfo());
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
        cRpgHtn crpg = new cRpgHtn();
        crpg.build(tn);
        return crpg;
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork tn, ProgressionPlanStep ps) {
        cRpgHtn crpg = new cRpgHtn();
        crpg.build(tn);
        return crpg;
    }
}
