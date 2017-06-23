package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntPairPriorityQueue;

import java.util.BitSet;

/**
 * Created by dh on 22.06.17.
 */
public class hLmCutnative extends SasHeuristic {
    private final SasHeuristics heuristic;
    private final int[] maxPrecInit;
    private final int[] hValInit;

    SasPlusProblem p;
    private int[] unsatPrecs;
    private int numGoals;

    private int[] hVal;
    private int[] maxPrec;
    private int maxPrecG;
    private int[] costs;

    public hLmCutnative(SasPlusProblem p, SasHeuristics heuristic) {
        this.heuristic = heuristic;
        this.p = p;
        this.maxPrecInit = new int[p.numOfOperators];
        for (int i = 0; i < maxPrecInit.length; i++)
            maxPrecInit[i] = -1;
        this.hValInit = new int[p.numOfStateFeatures];
        for (int i = 0; i < hValInit.length; i++)
            hValInit[i] = cUnreachable;
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        return hMax(s0, g);
    }

    public int hMax(BitSet s0, BitSet g) {
        this.costs = p.costs.clone();
        this.unsatPrecs = p.numPrecs.clone();
        this.hVal = hValInit.clone();
        this.maxPrec = maxPrecInit.clone();
        this.numGoals = g.cardinality();

        UUIntPairPriorityQueue queue = new UUIntPairPriorityQueue();
        for (int f = s0.nextSetBit(0); f >= 0; f = s0.nextSetBit(f + 1)) {
            queue.add(0, f);
            hVal[f] = 0;
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
                if ((maxPrec[op] == -1) || (hVal[maxPrec[op]] < hVal[prop]))
                    hVal[maxPrec[op]] = hVal[prop];
                if (--unsatPrecs[op] == 0) {
                    for (int f : p.addLists[op]) {
                        if ((hVal[maxPrec[op]] + costs[op]) < hVal[f]) {
                            hVal[f] = hVal[maxPrec[op]] + costs[op];
                            queue.add(hVal[f], f);
                        }
                    }
                }
            }
        }
        return cUnreachable;
    }


    public int costUpdate(BitSet operators, int decreaseBy, BitSet g) {
        UUIntPairPriorityQueue queue = new UUIntPairPriorityQueue();
        for (int op = operators.nextSetBit(0); op >= 0; op = operators.nextSetBit(op + 1)) {
            costs[op] -= decreaseBy;
            for (int f : p.addLists[op]) {
                hVal[f] = hVal[maxPrec[op]] + costs[op];
                queue.add(hVal[f], f);
            }
        }

        while (!queue.isEmpty()) {
            int[] pair = queue.minPair();
            int pVal = pair[0];
            int prop = pair[1];
            if (hVal[prop] < pVal) // we have prop decreased -> this is ok
                continue;
            for (int op : p.precToTask[prop]) {
                if (prop == maxPrec[op]) { // this may change the costs of the operator and all its successors
                    int opMaxPrec = -1;
                    int val = Integer.MIN_VALUE;
                    for (int f : p.precLists[op]) {
                        if (hVal[f] > val) {
                            opMaxPrec = f;
                            val = hVal[f];
                        }
                    }
                    maxPrec[op] = opMaxPrec;
                    for (int f : p.addLists[op]) {
                        if ((hVal[maxPrec[op]] + costs[op]) < hVal[f]) {
                            hVal[f] = hVal[maxPrec[op]] + costs[op];
                            queue.add(hVal[f], f);
                        }
                    }
                }
            }
        }
        return getMaxVal(g);
    }

    private int getMaxVal(BitSet g) {
        int hVal = 0;
        for (int f = g.nextSetBit(0); f >= 0; f = g.nextSetBit(f + 1)) {
            assert this.hVal[f] != cUnreachable;
            if (hVal > this.hVal[f]) {
                hVal = this.hVal[f];
                maxPrecG = f;
            }
        }

        return hVal;
    }
}
