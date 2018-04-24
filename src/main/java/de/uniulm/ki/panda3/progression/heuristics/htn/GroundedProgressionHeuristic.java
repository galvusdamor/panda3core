package de.uniulm.ki.panda3.progression.heuristics.htn;

import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;

import java.util.BitSet;

/**
 * Created by dhoeller on 25.07.16.
 */
public abstract class GroundedProgressionHeuristic {

    public abstract String getName();

    public boolean supportsHelpfulActions = false;

    public BitSet helpfulOps() {
        return null;
    }

    public abstract void build(ProgressionNetwork tn);

    public abstract GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, ProMethod m);

    public abstract GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps);

    public abstract int getHeuristic();

    public abstract boolean goalRelaxedReachable();
}
