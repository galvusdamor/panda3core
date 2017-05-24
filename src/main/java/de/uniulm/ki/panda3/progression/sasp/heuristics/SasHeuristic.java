package de.uniulm.ki.panda3.progression.sasp.heuristics;

import de.uniulm.ki.panda3.progression.sasp.SasPlusProblem;

import java.util.BitSet;

/**
 * Created by dh on 02.05.17.
 */
public abstract class SasHeuristic {

    public enum SasHeuristics {hMax, hAdd, hLmCut}

    public SasHeuristic(SasPlusProblem p) {
    }

    private SasHeuristic() {
    }

    public abstract int calcHeu(int[] s0, int[] g);

    public abstract int calcHeu(BitSet s0, BitSet g);

    public boolean debug = false;
    protected void debugOut(String s) {
        if(debug)
        System.out.print(s);
    }
}
