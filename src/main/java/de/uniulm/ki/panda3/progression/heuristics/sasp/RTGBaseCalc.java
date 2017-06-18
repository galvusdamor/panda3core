package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntPairPriorityQueue;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;

import java.util.*;

/**
 * Created by dh on 28.04.17.
 */
public abstract class RTGBaseCalc extends RelaxedTaskGraph {

    public RTGBaseCalc(SasPlusProblem p) {
        super(p);
    }

    public RTGBaseCalc(SasPlusProblem p, boolean trackPCF) {
        super(p, trackPCF);
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        if (this.trackPCF) {
            pcf = new int[pcf.length];
            for (int i = 0; i < pcfInvert.length; i++)
                pcfInvert[i].clear();
            opReachable.clear();
        }

        currentWaitingForCount = initialWaitingForCount.clone();
        for (int i = 0; i < hVal.length; i++) {
            hVal[i] = Integer.MAX_VALUE;
        }
        UUIntPairPriorityQueue activatable = new UUIntPairPriorityQueue();

        // init queue by adding s0

        for (int nextF = s0.nextSetBit(0); nextF >= 0; nextF = s0.nextSetBit(nextF + 1)) {
            activatable.add(this.initPair(nextF, 0));
            currentWaitingForCount[nextF] = 0;
        }

        // dummy prec-nodes of actions that do not have preconditions
        for (int prec : precTnodes) {
            activatable.add(this.initPair(prec, 0 + costs[prec]));
            currentWaitingForCount[prec] = 0;
        }
        return calcHeuLoop(activatable, (BitSet) g.clone(), this.earlyAbord);
    }

    private int calcHeuLoop(UUIntPairPriorityQueue activatable, BitSet goal, boolean earlyAbord) {
        int hValGoal = eAND();
        boolean goalReached = false;

        while (!activatable.isEmpty()) {
            int newNode = activatable.minPair()[1];
            if (goal.get(newNode)) {
                goal.set(newNode, false);
                int old = hValGoal;
                hValGoal = combineAND(hValGoal, hVal[newNode]);
                if (old != hValGoal)
                    this.goalPCF = newNode;
                if (goal.isEmpty()) {
                    goalReached = true;
                    if (earlyAbord)
                        break;
                }
            }
            for (int i = 0; i < whoIsWaitingForMe[newNode].length; i++) {
                int waitingNode = whoIsWaitingForMe[newNode][i];
                currentWaitingForCount[waitingNode]--;
                if (currentWaitingForCount[waitingNode] == 0) {
                    activatable.add(this.calcPair(waitingNode));
                }
            }
        }
        if (goalReached)
            return hValGoal;
        else
            return Integer.MAX_VALUE;
    }


    int[] initPair(int index, int hMaxVal) {
        hVal[index] = hMaxVal;
        int[] res = new int[2];
        res[0] = hVal[index];
        res[1] = index;
        return res;
    }

    int[] calcPair(int index) {
        int[] res = new int[2];
        res[1] = index;

        int predCosts;
        if (isAndNode.get(index)) {
            int relPrec = -1;

            predCosts = eAND();
            for (int i = 0; i < waitingForNodes[index].length; i++) {
                int old = predCosts;
                predCosts = combineAND(predCosts, hVal[waitingForNodes[index][i]]);
                if (old != predCosts)
                    relPrec = waitingForNodes[index][i];
            }

            int op = precNodeToOp[index];
            if ((trackPCF) && (op > -1)) {
                opReachable.set(op);// mark operator as reached
                pcf[op] = relPrec; // mark which precondition has been the limiting factor
                pcfInvert[relPrec].push(op);
            }
        } else {
            predCosts = eOR();
            int bestAchiver = -1;
            for (int i = 0; i < waitingForNodes[index].length; i++) {
                int old = predCosts;
                predCosts = combineOR(predCosts, hVal[waitingForNodes[index][i]]);
                if (old != predCosts)
                    bestAchiver = waitingForNodes[index][i];
            }
            assert bestAchiver >= 0;
            if (evalBestAchievers) {
                this.evalAchiever(index, bestAchiver);
            }
        }

        hVal[index] = predCosts + costs[index];
        res[0] = hVal[index];
        return res;
    }
}
