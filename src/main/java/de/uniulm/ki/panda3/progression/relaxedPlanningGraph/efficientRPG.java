package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan;

import java.util.*;

/**
 * Created by dhoeller on 29.06.16.
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
public class efficientRPG {

    public static int numActions;
    public static int numStateFeatures;

    public static BitSet[] prec;
    public static BitSet[] add;
    public static int[][] precList;
    public static int[][] addList;
    public static int[] goalList;

    /**
     * The following array stores for every fact in which fact layer it has been made true:
     * [6, 1, 0, -1] means that there are 4 facts, the first one has been made true in layer 6
     * the second in layer 1... The -1 for fact 4 means that this fact has never been made true
     */
    int[] firstLayerWithFact;

    /**
     * actionDelta is a list of lists. Each inner list contains those actions that are applicable
     * in this layer for the *first time*, i.e. it is the delta of applicable actions
     */
    public List<List<Integer>> actionDelta = new ArrayList<>();

    /**
     * goalDelta is a list of lists. Each inner list contains those goal facts that hold in the layer
     * for the *first time*, i.e. it is the delta of fulfilled goal conditions. Be aware that
     * goalDelta is *changed* during heuristic calculation.
     */
    List<List<Integer>> goalDelta = new ArrayList<>();

    public Boolean goalRelaxedReachable = null;

    public void build(BitSet s0) {
        this.build(s0, null);
    }

    public void build(BitSet s0, EfficientPlan htn) {

        // init the field "firstLayerWithFact"
        firstLayerWithFact = new int[numStateFeatures];
        for (int i = 0; i < numStateFeatures; i++) {
            if (s0.get(i))
                firstLayerWithFact[i] = 0;
            else
                firstLayerWithFact[i] = -1;
        }
        actionDelta.add(new LinkedList<Integer>()); // dummyTaskList

        LinkedList<Integer> unfulfilledGoals = new LinkedList<>();
        List<Integer> newGoals = new ArrayList<>();
        goalDelta.add(newGoals);
        for (int goalFact : goalList) {
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
        LinkedList<Integer> yetUnapplActions = new LinkedList();
        for (int i = 0; i < numActions; i++)
            yetUnapplActions.add(i);

        BitSet state = (BitSet) s0.clone();
        for (int i = 1; true; i++) {
            BitSet nextState = (BitSet) state.clone();
            List<Integer> newApplicableActions = new ArrayList<>();
            actionDelta.add(newApplicableActions);
            int maxJ = yetUnapplActions.size();
            for (int j = 0; j < maxJ; j++) {
                int action = yetUnapplActions.removeFirst();

                BitSet temp = (BitSet) state.clone();
                temp.and(prec[action]);

                if (temp.equals(prec[action])) {
                    for (Integer addFact : addList[action]) {
                        if (firstLayerWithFact[addFact] == -1)
                            firstLayerWithFact[addFact] = i;
                    }
                    nextState.or(add[action]);
                    newApplicableActions.add(action);
                } else
                    yetUnapplActions.addLast(action);
            }

            if (!unfulfilledGoals.isEmpty()) {
                newGoals = new ArrayList<>();
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

    public int getFF() {
        int res = 0;
        for (int layer = goalDelta.size() - 1; layer >= 1; layer--) {
            for (int goalFact : goalDelta.get(layer)) {
                int producer = getProducer(layer, goalFact);
                res++;
                for (int i = 0; i < precList[producer].length; i++) {
                    int aPrec = precList[producer][i];
                    int fl = firstLayerWithFact[aPrec];
                    goalDelta.get(fl).add(aPrec);
                }
            }
        }
        return res;
    }

    private int getProducer(int layer, int fact) {
        for (int maybeProducer : actionDelta.get(layer)) {
            for (int i = 0; i < addList[maybeProducer].length; i++) {
                if (addList[maybeProducer][i] == fact) {
                    // todo: DH says: there could be more than one - any strategy on which one to choose?
                    return maybeProducer;
                }
            }
        }
        // this should never happen
        System.out.println("ERROR: Found no producing action of fact " + fact + " in action-layer " + layer);
        return -1;
    }
}
