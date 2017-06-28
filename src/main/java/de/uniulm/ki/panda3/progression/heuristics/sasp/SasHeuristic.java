package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;

import java.util.BitSet;

/**
 * Created by dh on 02.05.17.
 */
public abstract class SasHeuristic {

    protected boolean isIncremental = false;
    protected int lastAction;
    protected IncrementInformation increment;

    public enum SasHeuristics {hMax, hAdd, hFF, hLmCut, hIncLmCut}

    public SasHeuristic(SasPlusProblem p) {
    }

    private SasHeuristic() {
    }

    public boolean isIncremental(){
        return isIncremental;
    }

    public void setIncrement(int lastAction, IncrementInformation i){
        this.increment = i;
        this.lastAction = lastAction;
    }

    public IncrementInformation getIncrement(){
        return this.increment;
    }

    public abstract int calcHeu(int[] s0, int[] g);

    public abstract int calcHeu(BitSet s0, BitSet g);

    public boolean debug = false;
    protected void debugOut(String s) {
        if(debug)
        System.out.print(s);
    }
}
