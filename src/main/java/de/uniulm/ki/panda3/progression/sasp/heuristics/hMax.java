package de.uniulm.ki.panda3.progression.sasp.heuristics;

import de.uniulm.ki.panda3.progression.sasp.SasPlusProblem;

/**
 * Created by dh on 01.05.17.
 */
public class hMax extends RelaxedTaskGraph {
    public hMax(SasPlusProblem p, boolean trackPCF) {
        super(p, trackPCF);
    }

    public hMax(SasPlusProblem p) {
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
