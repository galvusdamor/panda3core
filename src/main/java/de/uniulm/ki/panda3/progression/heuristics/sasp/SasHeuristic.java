package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.progression.heuristics.sasp.IncrementalCalc.IncrementInformation;

import java.util.BitSet;

/**
 * Created by dh on 02.05.17.
 */
public abstract class SasHeuristic {
    public static final int cUnreachable = Integer.MAX_VALUE;
    public BitSet helpfulOps;

<<<<<<< Updated upstream
    public enum SasHeuristics {hFilter, hMax, hAdd, hFF, hFFwithHA, hCG, hLmCut, hLmCutOpt, hIncLmCut, noSearch}
=======
    public enum SasHeuristics {hFilter, hMax, hAdd, hFF, hFFwithHA, hCG, hhCG, hLmCut, hLmCutOpt, hIncLmCut}
>>>>>>> Stashed changes

    protected boolean isIncremental = false;

    public boolean isIncremental() {
        return this.isIncremental;
    }

    public IncrementInformation getIncInf() {
        return null;
    }

    public int calcHeu(int lastAction, IncrementInformation inc, BitSet s0, BitSet g) {
        return this.calcHeu(s0, g);
    }

    public abstract int calcHeu(BitSet s0, BitSet g);

    public boolean debug = false;

    protected void debugOut(String s) {
        if (debug)
            System.out.print(s);
    }
}
