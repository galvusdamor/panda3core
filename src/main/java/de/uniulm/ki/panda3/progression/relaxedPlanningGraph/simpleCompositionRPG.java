package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.proPlanStep;
import de.uniulm.ki.panda3.progression.htn.search.progressionNetwork;
import de.uniulm.ki.panda3.progression.proUtil.dotNode;
import de.uniulm.ki.panda3.progression.proUtil.dotit;
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import scala.Tuple2;
import scala.Tuple3;

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
public class simpleCompositionRPG implements htnGroundedProgressionHeuristic {

    /**
     * The following array stores for every fact in which fact layer it has been made true:
     * [6, 1, 0, -1] means that there are 4 facts, the first one has been made true in layer 6
     * the second in layer 1... The -1 for fact 4 means that this fact has never been made true
     */
    int[] firstLayerWithFact;

    /**
     * operatorDelta is a list of lists. Each inner list contains those actions that are applicable
     * in this layer for the *first time*, i.e. it is the delta of applicable actions
     */
    public List<List<Integer>> operatorDelta = new ArrayList<>();

    /**
     * goalDelta is a list of lists. Each inner list contains those goal facts that hold in the layer
     * for the *first time*, i.e. it is the delta of fulfilled goal conditions. Be aware that
     * goalDelta is *changed* during heuristic calculation.
     */
    List<List<Integer>> goalDelta = new ArrayList<>();

    int[] actionDifficulty;

    private boolean goalRelaxedReachable;

    // these represent reachable tasks
    private static HashMap<GroundTask, Integer> TaskLiteralToIndex;
    private static HashMap<Integer, GroundTask> IndexToTaskLiteral;

    private static HashMap<method, Integer> MethodToIndex;
    private static HashMap<Integer, method> IndexToMethod;

    private static List<Integer>[] prec;
    private static List<Integer>[] add;

    private static int numOperators;
    private static int numHtnStateFeatures;

    private simpleCompositionRPG() {
    }

    public simpleCompositionRPG(HashMap<Task, HashMap<GroundTask, List<method>>> methods, Set<GroundTask> allActions) {
        long time = System.currentTimeMillis();
        System.out.print("Init Simple Composition RPG heuristic");
        Set<GroundTask> allTasks = new HashSet<>();
        for (HashMap<GroundTask, List<method>> val : methods.values()) {
            for (GroundTask t : val.keySet()) {
                allTasks.add(t);
            }
        }
        int taskNo = operators.numStateFeatures;
        simpleCompositionRPG.IndexToTaskLiteral = new HashMap<>();
        simpleCompositionRPG.TaskLiteralToIndex = new HashMap<>();

        for (GroundTask a : allActions) {
            simpleCompositionRPG.IndexToTaskLiteral.put(taskNo, a);
            simpleCompositionRPG.TaskLiteralToIndex.put(a, taskNo);
            taskNo++;
        }

        for (GroundTask t : allTasks) {
            simpleCompositionRPG.IndexToTaskLiteral.put(taskNo, t);
            simpleCompositionRPG.TaskLiteralToIndex.put(t, taskNo);
            taskNo++;
        }
        simpleCompositionRPG.numHtnStateFeatures = taskNo;

        int methodID = operators.numActions;
        simpleCompositionRPG.MethodToIndex = new HashMap<>();
        simpleCompositionRPG.IndexToMethod = new HashMap<>();
        for (HashMap<GroundTask, List<method>> val : methods.values()) {
            for (List<method> val2 : val.values()) {
                for (method m : val2) {
                    assert (!simpleCompositionRPG.MethodToIndex.containsKey(m));
                    simpleCompositionRPG.MethodToIndex.put(m, methodID);
                    simpleCompositionRPG.IndexToMethod.put(methodID, m);
                    methodID++;
                }
            }
        }
        simpleCompositionRPG.numOperators = methodID;
        simpleCompositionRPG.prec = new List[simpleCompositionRPG.numOperators];
        simpleCompositionRPG.add = new List[simpleCompositionRPG.numOperators];

        for (int actionI = 0; actionI < operators.numActions; actionI++) {
            simpleCompositionRPG.prec[actionI] = new LinkedList<>();
            simpleCompositionRPG.add[actionI] = new LinkedList<>();
            int i = operators.prec[actionI].nextSetBit(0);
            while (i > -1) {
                simpleCompositionRPG.prec[actionI].add(i);
                i = operators.prec[actionI].nextSetBit(i + 1);
            }
            i = operators.add[actionI].nextSetBit(0);
            while (i > -1) {
                simpleCompositionRPG.add[actionI].add(i);
                i = operators.add[actionI].nextSetBit(i + 1);
            }

            // a new effect is added to each action: it makes the fact true that the corresponding fact is reachable
            i = simpleCompositionRPG.TaskLiteralToIndex.get(operators.IndexToAction[actionI]);
            simpleCompositionRPG.add[actionI].add(i);
        }
        for (int methodI = operators.numActions; methodI < simpleCompositionRPG.numOperators; methodI++) {
            simpleCompositionRPG.prec[methodI] = new LinkedList<>();
            simpleCompositionRPG.add[methodI] = new LinkedList<>();

            // the effect is the abstract task that has to be decomposed
            method someMethod = IndexToMethod.get(methodI);
            int taskID = simpleCompositionRPG.TaskLiteralToIndex.get(someMethod.m.groundAbstractTask());
            simpleCompositionRPG.add[methodI].add(taskID);

            // the precondition contains all subtasks
            for (GroundTask subtask : someMethod.tasks) {
                taskID = simpleCompositionRPG.TaskLiteralToIndex.get(subtask);
                simpleCompositionRPG.prec[methodI].add(taskID);
            }
        }

        // every action makes the corresponding task-fact true
        System.out.println(" (" + (System.currentTimeMillis() - time) + " ms)");
    }

    public void build(progressionNetwork tn) {
        actionDifficulty = new int[simpleCompositionRPG.numOperators];

        firstLayerWithFact = new int[simpleCompositionRPG.numHtnStateFeatures];
        for (int i = 0; i < operators.numStateFeatures; i++) {
            if (tn.state.get(i))
                firstLayerWithFact[i] = 0;
            else
                firstLayerWithFact[i] = -1;
        }

        // init facts concerning reachable task
        for (int i = operators.numStateFeatures; i < simpleCompositionRPG.numHtnStateFeatures; i++) {
            firstLayerWithFact[i] = -1;
        }

        // prepare goal
        List<Integer> htnGoal = new LinkedList<>();
        for (int i = 0; i < operators.goalList.length; i++) {
            htnGoal.add(operators.goalList[i]);
        }

        LinkedList<proPlanStep> goalFacts = new LinkedList<>();
        goalFacts.addAll(tn.getFirst());

        while (!goalFacts.isEmpty()) {
            proPlanStep ps = goalFacts.removeFirst();
            htnGoal.add(simpleCompositionRPG.TaskLiteralToIndex.get(ps.getTask()));
            goalFacts.addAll(ps.successorList);
        }

        operatorDelta.add(new LinkedList<Integer>()); // dummyTaskList

        LinkedList<Integer> unfulfilledGoals = new LinkedList<>();
        // todo !evaluate!: this might be a set or a list
        List<Integer> newGoals = new ArrayList<>();
        //Set<Integer> newGoals = new HashSet<>();


        goalDelta.add(newGoals);
        for (int goalFact : htnGoal) {
            if (firstLayerWithFact[goalFact] == 0) {
                newGoals.add(goalFact);
            } else {
                unfulfilledGoals.add(goalFact);
            }
        }

        // this is not very efficient
        // - getting the applicable actions from the direct predecessor search node would be a bit better
        // - grounding on the fly is difficult due to the altered representation. In combination with todo
        //   (ac-rep) one could overcome this problem
        // - iterative calculation of the graph should hopefully be good enough
        LinkedList<Integer> yetUnapplOperators = new LinkedList();
        for (int i = 0; i < simpleCompositionRPG.numOperators; i++)
            yetUnapplOperators.add(i);

        BitSet state = new BitSet(simpleCompositionRPG.numOperators);
        state.or(tn.state);

        for (int i = 1; true; i++) {
            BitSet nextState = (BitSet) state.clone();
            List<Integer> newApplicableActions = new ArrayList<>();
            operatorDelta.add(newApplicableActions);
            int maxJ = yetUnapplOperators.size();
            for (int j = 0; j < maxJ; j++) {
                int action = yetUnapplOperators.removeFirst();

                boolean isApplicable = true;
                for (int precI : simpleCompositionRPG.prec[action]) {
                    if (!state.get(precI)) {
                        isApplicable = false;
                        break;
                    }
                }

                if (isApplicable) {
                    actionDifficulty[action] = 0;
                    for (int precI : simpleCompositionRPG.prec[action]) {
                        actionDifficulty[action] += firstLayerWithFact[precI];
                    }

                    for (Integer addFact : simpleCompositionRPG.add[action]) {
                        if (firstLayerWithFact[addFact] == -1)
                            firstLayerWithFact[addFact] = i;
                        nextState.set(addFact);
                    }
                    newApplicableActions.add(action);
                } else
                    yetUnapplOperators.addLast(action);
            }

            if (!unfulfilledGoals.isEmpty()) {
                // todo !evaluate!: this might be a set or a list
                newGoals = new ArrayList<>();
                //newGoals = new HashSet<>();

                goalDelta.add(newGoals);
                int maxJ2 = unfulfilledGoals.size();
                for (int j = 0; j < maxJ2; j++) {
                    int remGoal = unfulfilledGoals.removeFirst();
                    if (firstLayerWithFact[remGoal] == i)
                        newGoals.add(remGoal);
                    else
                        unfulfilledGoals.addLast(remGoal);
                }
            } else {
                // todo: Daniel says: should be save to break (?)
                goalRelaxedReachable = true;
                break;
            }

            if (state.equals(nextState)) {
                goalRelaxedReachable = false;
                break;
            }
            state = nextState;
        }
    }

/*
    public int getHeuristic2() {
        int res = 0;
        for (int layer = goalDelta.size() - 1; layer >= 1; layer--) {
            for (int goalFact : goalDelta.get(layer)) {
                int producer = getProducer(layer, goalFact);
                res++;
                for (Integer aPrec : simpleCompositionRPG.prec[producer]) {
                    int fl = firstLayerWithFact[aPrec];
                    goalDelta.get(fl).add(aPrec);
                }
            }
        }
        return res;
    }

    private int getProducer(int layer, int fact) {
        for (int maybeProducer : operatorDelta.get(layer)) {
            if (simpleCompositionRPG.add[maybeProducer].contains(fact)) {
                // todo: DH says: there could be more than one - any strategy on which one to choose?
                return maybeProducer;
            }
        }
        // this should never happen
        System.out.println("ERROR: Found no producing action of fact " + fact + " in action-layer " + layer);
        return -1;
    }*/


    // This is the version given by Jörg Hoffmann in this 2001 JAIR
    // Be careful to use sets above for the goaldelta
    public int getHeuristic3() {
        int numactions = 0;
        List<Set<Integer>> markedTrue = new ArrayList<>();
        for (int i = 0; i < goalDelta.size(); i++) {
            markedTrue.add(new HashSet<Integer>());
        }

        for (int layer = goalDelta.size() - 1; layer >= 1; layer--) {
            for (int goalFact : goalDelta.get(layer)) {
                if (markedTrue.get(layer).contains(goalFact))
                    continue;

                int bestDifficulty = Integer.MAX_VALUE;
                int producer = -1;

                for (int maybeProducer : operatorDelta.get(layer)) {
                    if ((simpleCompositionRPG.add[maybeProducer].contains(goalFact))
                            && (actionDifficulty[maybeProducer] < bestDifficulty)) {
                        bestDifficulty = actionDifficulty[maybeProducer];
                        producer = maybeProducer;
                    }
                }
                numactions++;

                for (Integer aPrec : simpleCompositionRPG.prec[producer]) {
                    int fl = firstLayerWithFact[aPrec];
                    if ((fl > 0) && (!markedTrue.get(layer - 1).contains(aPrec)))
                        goalDelta.get(fl).add(aPrec);
                }
                Set<Integer> thislayer = markedTrue.get(layer);
                Set<Integer> lastLayer = markedTrue.get(layer - 1);
                for (Integer aAdd : simpleCompositionRPG.add[producer]) {
                    thislayer.add(aAdd);
                    lastLayer.add(aAdd);
                }
            }
        }
        return numactions;
    }

    // This is a MODIFIED version of Jörg Hoffmann's 2001 JAIR
    public int getHeuristic4() {
        int numactions = 0;
        for (int layer = goalDelta.size() - 1; layer >= 1; layer--) {
            for (int goalFact : goalDelta.get(layer)) {

                int bestDifficulty = Integer.MAX_VALUE;
                int producer = -1;

                for (int maybeProducer : operatorDelta.get(layer)) {
                    if ((simpleCompositionRPG.add[maybeProducer].contains(goalFact))
                            && (actionDifficulty[maybeProducer] < bestDifficulty)) {
                        bestDifficulty = actionDifficulty[maybeProducer];
                        producer = maybeProducer;
                    }
                }
                numactions++;

                for (Integer aPrec : simpleCompositionRPG.prec[producer]) {
                    int fl = firstLayerWithFact[aPrec];
                    if (fl > 0)
                        goalDelta.get(fl).add(aPrec);
                }
            }
        }
        return numactions;
    }

    // This is a MODIFIED version of Jörg Hoffmann's 2001 JAIR that dots its output
    public int getHeuristic() {
        List<List<dotNode>> operatorlayer = new ArrayList<>();
        for (int i = 0; i < goalDelta.size(); i++) {
            operatorlayer.add(new ArrayList<dotNode>());
        }
        List<List<dotNode>> literallayer = new ArrayList<>();
        for (int i = 0; i < goalDelta.size(); i++) {
            literallayer.add(new ArrayList<dotNode>());
        }

        List<Tuple3<dotNode, dotNode, Integer>> arcs = new ArrayList<>();

        int numactions = 0;
        for (int layer = goalDelta.size() - 1; layer >= 1; layer--) {
            for (int goalFact : goalDelta.get(layer)) {

                dotNode lit;
                if (goalFact < operators.numStateFeatures)
                    lit = new dotNode(goalFact, operators.IndexToLiteral[goalFact].shortInfo());
                else
                    lit = new dotNode(goalFact, simpleCompositionRPG.IndexToTaskLiteral.get(goalFact).shortInfo());

                literallayer.get(layer).add(lit);

                int bestDifficulty = Integer.MAX_VALUE;
                int producer = -1;

                for (int maybeProducer : operatorDelta.get(layer)) {
                    if ((simpleCompositionRPG.add[maybeProducer].contains(goalFact))
                            && (actionDifficulty[maybeProducer] < bestDifficulty)) {
                        bestDifficulty = actionDifficulty[maybeProducer];
                        producer = maybeProducer;
                    }
                }
                numactions++;

                dotNode op;
                if (producer < operators.numActions) {
                    op = new dotNode(producer, operators.IndexToAction[producer].shortInfo());
                } else
                    op = new dotNode(producer, simpleCompositionRPG.IndexToMethod.get(producer).m.shortInfo());
                operatorlayer.get(layer).add(op);

                arcs.add(new Tuple3<dotNode, dotNode, Integer>(lit, op, bestDifficulty));

                for (Integer aPrec : simpleCompositionRPG.prec[producer]) {
                    int fl = firstLayerWithFact[aPrec];
                    if (fl > 0)
                        goalDelta.get(fl).add(aPrec);
                }

            }
        }
        Tuple2<List<List<dotNode>>, List<Tuple2<dotNode, dotNode>>> dot = getDot();
        String s1 = dotit.dotit2(dot._1(), dot._2());

        List<List<dotNode>> cluster = new ArrayList<>();
        for (int i = 0; i < operatorlayer.size(); i++) {
            cluster.add(literallayer.get(i));
            cluster.add(operatorlayer.get(i));
        }

        String s2 = dotit.dotit3(cluster, arcs);
        return numactions;
    }

    private Tuple2<List<List<dotNode>>, List<Tuple2<dotNode, dotNode>>> getDot() {
        List<List<Integer>> actionlayer = new ArrayList<>();
        List<List<Integer>> methodlayer = new ArrayList<>();
        List<List<Integer>> literallayer = new ArrayList<>();
        for (int i = 0; i < operatorDelta.size() - 1; i++) {
            List<Integer> actions = new ArrayList<>();
            List<Integer> methods = new ArrayList<>();
            methodlayer.add(methods);
            actionlayer.add(actions);
//            for (int j = i; j <= i; j++) {
            for (int j = 0; j <= i; j++) {
                for (int op : operatorDelta.get(j)) {
                    if (op < operators.numActions) {
                        actions.add(op);
                    } else {
                        methods.add(op);
                    }
                }
            }
        }
        for (int i = 0; i < firstLayerWithFact.length; i++) {
            while (literallayer.size() <= firstLayerWithFact[i]) {
                literallayer.add(new ArrayList<Integer>());
            }
            literallayer.get(firstLayerWithFact[i]).add(i);
        }
        for (int i = 1; i < literallayer.size(); i++) {
            literallayer.get(i).addAll(literallayer.get(i - 1));
        }
        List<Tuple2<dotNode, dotNode>> arcs = new ArrayList<>();
        List<List<dotNode>> cluster = new ArrayList<>();
        List<dotNode> initialState = new ArrayList<>();
        cluster.add(initialState);
        for (int literalI : literallayer.get(0)) {
            if (operators.IndexToLiteral.length > literalI)
                initialState.add(new dotNode(literalI, operators.IndexToLiteral[literalI].shortInfo()));
            else
                initialState.add(new dotNode(literalI, simpleCompositionRPG.IndexToTaskLiteral.get(literalI).shortInfo()));
        }

        List<dotNode> lastState = initialState;
        for (int layer = 1; layer < operatorDelta.size() - 1; layer++) {
            List<dotNode> state = new ArrayList<>();
            for (int literalI : literallayer.get(layer)) {
                if (operators.IndexToLiteral.length > literalI)
                    state.add(new dotNode(literalI, operators.IndexToLiteral[literalI].shortInfo()));
                else
                    state.add(new dotNode(literalI, simpleCompositionRPG.IndexToTaskLiteral.get(literalI).shortInfo()));
            }

            List<dotNode> ops = new ArrayList<>();
            cluster.add(ops);
            cluster.add(state);

            dotOperators(actionlayer.get(layer), arcs, lastState, state, ops, operators.IndexToAction, null, "action");
            dotOperators(methodlayer.get(layer), arcs, lastState, state, ops, null, simpleCompositionRPG.IndexToMethod, "method");
            lastState = state;
        }
        return new Tuple2<>(cluster, arcs);
    }

    private void dotOperators(List<Integer> operatorlayer, List<Tuple2<dotNode, dotNode>> arcs, List<dotNode> lastState, List<dotNode> state, List<dotNode> ops, GroundTask[] indexToAction, HashMap<Integer, method> indexToMethod, String style) {
        for (int operatorI : operatorlayer) {
            String name;
            if (indexToAction != null)
                name = indexToAction[operatorI].shortInfo();
            else {
                method method = indexToMethod.get(operatorI);
                GroundedDecompositionMethod m = method.m;
                name = m.shortInfo();
            }
            dotNode op = new dotNode(operatorI, name);
            ops.add(op);
            for (int i = 0; i < simpleCompositionRPG.prec[operatorI].size(); i++) {
                for (dotNode l : lastState) {
                    if (l.id == simpleCompositionRPG.prec[operatorI].get(i)) {
                        arcs.add(new Tuple2<dotNode, dotNode>(l, op));
                        break;
                    }
                }
            }
            for (int i = 0; i < simpleCompositionRPG.add[operatorI].size(); i++) {
                for (dotNode l : state) {
                    if (l.id == simpleCompositionRPG.add[operatorI].get(i)) {
                        arcs.add(new Tuple2<dotNode, dotNode>(op, l));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean goalRelaxedReachable() {
        return goalRelaxedReachable;
    }

    @Override
    public htnGroundedProgressionHeuristic update(progressionNetwork tn, proPlanStep ps, method m) {
        simpleCompositionRPG simpleCompositionRPG = new simpleCompositionRPG();
        simpleCompositionRPG.build(tn);
        return simpleCompositionRPG;
    }

    @Override
    public htnGroundedProgressionHeuristic update(progressionNetwork tn, proPlanStep ps) {
        simpleCompositionRPG simpleCompositionRPG = new simpleCompositionRPG();
        simpleCompositionRPG.build(tn);
        return simpleCompositionRPG;
    }
}
