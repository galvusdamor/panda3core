package de.uniulm.ki.panda3.progression.heuristics.sasp.RtgBasedHeuristics;

import de.uniulm.ki.panda3.progression.heuristics.sasp.RtgBasedHeuristics.RTGBaseCalc;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;

/**
 * Created by dh on 01.05.17.
 */
public class hMaxRtg extends RTGBaseCalc {
    public hMaxRtg(SasPlusProblem p, boolean trackPCF) {
        super(p, trackPCF);
    }

    public hMaxRtg(SasPlusProblem p) {
        super(p);
    }

    @Override
    int eAND() {
        return Integer.MIN_VALUE;
    }

    @Override
    int eOR() {
        return Integer.MAX_VALUE;
    }

    @Override
    int combineAND(int x, int y) {
        return Integer.max(x, y);
    }

    @Override
    int combineOR(int x, int y) {
        return Integer.min(x, y);
    }
}
