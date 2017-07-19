package de.uniulm.ki.panda3.progression.heuristics.sasp.IncrementalCalc;

import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntStack;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dh on 29.05.17.
 */
public class IncInfLmCut extends IncrementInformation {
    public List<int[]> cuts;
    public UUIntStack costs;

    public IncInfLmCut() {
        this.cuts = new LinkedList<>();
        this.costs = new UUIntStack(50);
    }
/*
    public boolean cutsAreDisjunctive() {
        for (int cutI = 0; cutI < cuts.size(); cutI++) {
            for (int cutJ = cutI + 1; cutJ < cuts.size(); cutJ++) {
                BitSet some = (BitSet) cuts.get(cutI).clone();
                if (some.intersects(cuts.get(cutJ)))
                    return false;
            }
        }
        return true;
    }

    public boolean costsGreaterZero(int[] costs, int[] opIndexToEffNode) {
        for (BitSet cut : cuts) {
            int op = cut.nextSetBit(0);
            while (op >= 0) {
                if (costs[opIndexToEffNode[op]] <= 0) {
                    return false;
                }
                op = cut.nextSetBit(op + 1);
            }
        }
        return true;
    }

    public boolean costsGreaterZero(int[] costs) {
        for (BitSet cut : cuts) {
            int op = cut.nextSetBit(0);
            while (op >= 0) {
                if (costs[op] <= 0) {
                    return false;
                }
                op = cut.nextSetBit(op + 1);
            }
        }
        return true;
    }*/
}
