package de.uniulm.ki.panda3.progression.heuristics.sasp.IncrementalCalc;

import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntStack;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dh on 29.05.17.
 */
public class IncInfLmCut extends IncrementInformation {
    public List<BitSet> cuts;
    public UUIntStack costs;

    public IncInfLmCut() {
        this.cuts = new LinkedList<>();
        this.costs = new UUIntStack(25);
    }

    public boolean disjunct() {
        for (int i = 0; i < cuts.size(); i++) {
            for (int j = i + 1; j < cuts.size(); j++) {
                BitSet some = (BitSet) cuts.get(i).clone();
                if (some.intersects(cuts.get(j)))
                    return false;
            }
        }
        return true;
    }

    public boolean costsGrZero(int[] costs, int[] opIndexToEffNode) {
        for (BitSet cut : cuts) {
            int i = cut.nextSetBit(0);
            while (i >= 0) {
                if (costs[opIndexToEffNode[i]] <= 0) {
                    return false;
                }
                i = cut.nextSetBit(i + 1);
            }
        }
        return true;
    }
}
