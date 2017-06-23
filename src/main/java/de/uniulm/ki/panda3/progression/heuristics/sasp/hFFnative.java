package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntPairPriorityQueue;

import java.util.BitSet;

/**
 * Created by dh on 22.06.17.
 */
public class hFFnative extends SasHeuristic {
    private final SasHeuristics heuristic;
    SasPlusProblem p;
    private int[] unsatPrecs;
    private int numGoals;
    private int[] hValOp;
    private int[] hValProp;
    private int[] reachedBy;

    public hFFnative(SasPlusProblem p, SasHeuristics heuristic) {
        this.heuristic = heuristic;
        this.p = p;
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        this.unsatPrecs = p.numPrecs.clone();
        this.numGoals = g.cardinality();

        this.hValOp = p.costs.clone();
        this.hValProp = new int[p.numOfStateFeatures];
        for (int i = 0; i < hValProp.length; i++)
            hValProp[i] = cUnreachable;
        this.reachedBy = new int[p.numOfStateFeatures];

        UUIntPairPriorityQueue queue = new UUIntPairPriorityQueue();
        for (int f = s0.nextSetBit(0); f >= 0; f = s0.nextSetBit(f + 1)) {
            queue.add(0, f);
            hValProp[f] = 0;
        }

        while (!queue.isEmpty()) {
            int[] pair = queue.minPair();
            int pVal = pair[0];
            int prop = pair[1];
            if (hValProp[prop] < pVal)
                continue;
            if (g.get(prop) && (--numGoals == 0)) {
                if (heuristic == SasHeuristics.hAdd)
                    return getAddVal(g);
                else
                    return getFFVal(g);
            }
            for (int op : p.precToTask[prop]) {
                hValOp[op] += pVal; // depends on heurisitc
                if (--unsatPrecs[op] == 0) {
                    for (int f : p.addLists[op]) {
                        if (hValOp[op] < hValProp[f]) {
                            hValProp[f] = hValOp[op];
                            reachedBy[f] = op; // only used by FF
                            queue.add(hValProp[f], f);
                        }
                    }
                }
            }
        }
        return cUnreachable;
    }

    private int getAddVal(BitSet g) {
        int hVal = 0;
        for (int f = g.nextSetBit(0); f >= 0; f = g.nextSetBit(f + 1)) {
            assert hValProp[f] != cUnreachable;
            hVal += hValProp[f];
        }

        return hVal;
    }

    private int getFFVal(BitSet g) {
        BitSet markedFs = new BitSet();
        BitSet markedOps = new BitSet();
        for (int f = g.nextSetBit(0); f >= 0; f = g.nextSetBit(f + 1)) {
            assert hValProp[f] != cUnreachable;
            markRelaxedPlan(markedFs, markedOps, f);
        }

        int hGoal = 0;
        for (int op = markedOps.nextSetBit(0); op >= 0; op = markedOps.nextSetBit(op + 1)) {
            hGoal += hValOp[op];
        }
        return hGoal;
    }

    private void markRelaxedPlan(BitSet markedFs, BitSet markedOps, int f) {
        if (!markedFs.get(f)) {
            markedFs.set(f);
            if (reachedBy[f] > 0) {
                for (int prec : p.precLists[reachedBy[f]]) {
                    markRelaxedPlan(markedFs, markedOps, prec);
                }
                markedOps.set(reachedBy[f]);
            }
        }
    }
}
