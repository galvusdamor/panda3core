package de.uniulm.ki.panda3.progression.heuristics.sasp.ExplorationQueueBasedHeuristics;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;

import java.util.BitSet;

/**
 * Created by dh on 23.08.17.
 */
public class hFilter extends hMaxEq {
    public hFilter(SasPlusProblem p) {
        super(p);
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        if (super.calcHeu(s0, g) == cUnreachable)
            return cUnreachable;
        else
            return 0;
    }
}
