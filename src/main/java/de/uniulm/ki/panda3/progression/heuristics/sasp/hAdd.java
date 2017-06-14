package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;

/**
 * Created by dh on 01.05.17.
 */
public class hAdd extends RelaxedTaskGraph {
    public hAdd(SasPlusProblem p) {
        super(p);
    }

    @Override
    int eAND() {
        return 0;
    }

    @Override
    int eOR() {
        return Integer.MAX_VALUE;
    }

    @Override
    int combineAND(int x, int y) {
        return x + y;
    }

    @Override
    int combineOR(int x, int y) {
        return Integer.min(x, y);
    }
}
