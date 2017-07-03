package de.uniulm.ki.panda3.progression.heuristics.sasp.ExplorationQueueBasedHeuristics;

import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntPairPriorityQueue;

import java.util.*;

/**
 * Created by dh on 22.06.17.
 */
public class hMaxEq extends SasHeuristic {
    private final int[] maxPrecInit;
    private final int[] hValInit;
    private final int[] precLessOps;

    SasPlusProblem p;
    private int[] unsatPrecs;
    private int numGoals;

    private int[] hVal;
    private int[] maxPrec;

    @Override
    public String toString() {
        return "hMax based on exploration queue";
    }

    public hMaxEq(SasPlusProblem p) {
        this.p = p;
        this.maxPrecInit = new int[p.numOfOperators];
        for (int i = 0; i < maxPrecInit.length; i++)
            maxPrecInit[i] = -1;
        this.hValInit = new int[p.numOfStateFeatures];
        for (int i = 0; i < hValInit.length; i++)
            hValInit[i] = cUnreachable;

        // get actions without preconditions
        List<Integer> tempPrecLess = new ArrayList<>();
        for (int i = 0; i < p.numOfOperators; i++) {
            if (p.addLists[i].length == 0)
                tempPrecLess.add(i);
        }
        precLessOps = new int[tempPrecLess.size()];
        for (int i = 0; i < tempPrecLess.size(); i++)
            precLessOps[i] = tempPrecLess.get(i);
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        this.unsatPrecs = p.numPrecs.clone();
        this.maxPrec = maxPrecInit.clone();
        this.hVal = hValInit.clone();
        this.numGoals = g.cardinality();

        UUIntPairPriorityQueue queue = new UUIntPairPriorityQueue();
        for (int f = s0.nextSetBit(0); f >= 0; f = s0.nextSetBit(f + 1)) {
            queue.add(0, f);
            hVal[f] = 0;
        }
        // actions without preconditions
        for (int a = 0; a < precLessOps.length; a++) {
            for (int f : p.addLists[a]) {
                if (hVal[f] > p.costs[a]) {
                    hVal[f] = p.costs[a];
                    queue.add(hVal[f], f);
                }
            }
        }

        while (!queue.isEmpty()) {
            int[] pair = queue.minPair();
            int pVal = pair[0];
            int prop = pair[1];
            if (hVal[prop] < pVal)
                continue;

            if (g.get(prop) && (--numGoals == 0)) {
                return getMaxVal(g);
            }
            for (int op : p.precToTask[prop]) {
                if ((maxPrec[op] == -1) || (hVal[maxPrec[op]] < hVal[prop])) {
                    maxPrec[op] = prop;
                }
                if (--unsatPrecs[op] == 0) {
                    assert allPrecsTrue(op);
                    for (int f : p.addLists[op]) {
                        if ((hVal[maxPrec[op]] + p.costs[op]) < hVal[f]) {
                            hVal[f] = hVal[maxPrec[op]] + p.costs[op];
                            queue.add(hVal[f], f);
                        }
                    }
                }
            }
        }
        return cUnreachable;
    }


    private int getMaxVal(BitSet g) {
        int hVal = 0;
        for (int f = g.nextSetBit(0); f >= 0; f = g.nextSetBit(f + 1)) {
            assert this.hVal[f] != cUnreachable;
            if (hVal > this.hVal[f]) {
                hVal = this.hVal[f];
            }
        }

        return hVal;
    }

    private boolean allPrecsTrue(int op) {
        for (int prec : p.precLists[op]) {
            if (hVal[prec] == cUnreachable)
                return false;
        }
        return true;
    }

}
