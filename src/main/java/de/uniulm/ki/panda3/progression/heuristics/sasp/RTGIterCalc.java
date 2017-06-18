package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntPairPriorityQueue;

import java.util.BitSet;

/**
 * Created by dh on 15.06.17.
 */
public abstract class RTGIterCalc extends RelaxedTaskGraph {

    public RTGIterCalc(SasPlusProblem p) {
        super(p);
        this.earlyAbord = false;
    }

    public int s0Changed(int[] added, int[] deleted, BitSet g) {
        UUIntPairPriorityQueue update = new UUIntPairPriorityQueue();

        for (int f : added) {
            update.add(f, 0);
        }
        for (int f : deleted) {
            update.add(f, cUnreachable);
        }

        updateHeuristic(update);
        return getGoalVal(g);
    }

    public int costChanged(BitSet operators, BitSet g) {
        UUIntPairPriorityQueue update = new UUIntPairPriorityQueue();
        for (int o = operators.nextSetBit(0); o >= 0; o = operators.nextSetBit(o + 1)) {
            int node = opIndexToEffNode[o]; // a change of operator costs changes cost of its effect node
            if (updateAndNode(node)) {
                update.add(node, hVal[node]);
            }
        }

        updateHeuristic(update);
        return getGoalVal(g);
    }

    public void recomputeAll(BitSet s0) {
        for (int i = 0; i < hVal.length; i++) {
            hVal[i] = cUnreachable;
        }
        UUIntPairPriorityQueue updated = new UUIntPairPriorityQueue();

        // add s0 nodes
        for (int f = s0.nextSetBit(0); f >= 0; f = s0.nextSetBit(f + 1)) {
            hVal[f] = 0;
            updated.add(f, 0);
        }

        // add actions without preconditions
        for (int node : precTnodes)
            updated.add(node, 0);

        updateHeuristic(updated);
    }

    private void updateHeuristic(UUIntPairPriorityQueue updated) {
        while (!updated.isEmpty()) {
            int node = updated.minPair()[1];
            for (int i = 0; i < whoIsWaitingForMe[node].length; i++) {
                int waitingNode = whoIsWaitingForMe[node][i];
                if (isAndNode.get(waitingNode)) {
                    if (updateAndNode(waitingNode))
                        updated.add(waitingNode, hVal[waitingNode]);
                } else {
                    if (updateOrNode(waitingNode))
                        updated.add(waitingNode, hVal[waitingNode]);
                }
            }
        }
    }

    protected boolean updateAndNode(int index) {
        int relPrec = -1;
        int costs = eAND();
        for (int i = 0; i < waitingForNodes[index].length; i++) {
            if (waitingForNodes[index][i] == cUnreachable) {
                hVal[index] = cUnreachable;
                // todo: unset pcf and pcfInvert
                return false;
            }
            int oldVal = costs;
            costs = combineAND(costs, hVal[waitingForNodes[index][i]]);
            if (oldVal != costs)
                relPrec = waitingForNodes[index][i];
        }
        if (hVal[index] == costs + this.costs[index]) {
            return false;
        } else { // update stuff
            hVal[index] = costs + this.costs[index];
            int op = precNodeToOp[index];
            if ((trackPCF) && (op > -1)) {
                opReachable.set(op);// mark operator as reached
                pcf[op] = relPrec; // mark which precondition has been the limiting factor
                pcfInvert[relPrec].push(op);
            }
            return true;
        }
    }

    private boolean updateOrNode(int index) {
        int costs = eOR();
        int bestAchiever = -1;
        for (int i = 0; i < waitingForNodes[index].length; i++) {
            if (waitingForNodes[index][i] == cUnreachable) {
                continue;
            }
            int oldVal = costs;
            costs = combineOR(costs, hVal[waitingForNodes[index][i]]);
            if (oldVal != costs)
                bestAchiever = waitingForNodes[index][i];
        }
        boolean changed;
        if (bestAchiever >= 0) {
            changed = (hVal[index] == costs + this.costs[index]);
            hVal[index] = costs + this.costs[index];
            if (evalBestAchievers) {
                this.evalAchiever(index, bestAchiever);
            }

        } else { // no achiever found
            changed = (hVal[index] == cUnreachable);
            // todo: update bestAchiever
            hVal[index] = cUnreachable;
        }
        return changed;
    }

    private int getGoalVal(BitSet goal) {
        int hValGoal = eAND();
        for (int f = goal.nextSetBit(0); f >= 0; f = goal.nextSetBit(f + 1)) {
            if (hVal[f] == cUnreachable) {
                return cUnreachable;
            }
            int oldVal = hValGoal;
            hValGoal = combineAND(hValGoal, hVal[f]);
            if (oldVal != hValGoal)
                this.goalPCF = f;

        }
        return hValGoal;
    }
}
