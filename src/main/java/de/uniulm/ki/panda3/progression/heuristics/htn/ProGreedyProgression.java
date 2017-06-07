package de.uniulm.ki.panda3.progression.heuristics.htn;

import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;

/**
 * Created by dh on 19.09.16.
 */
public class ProGreedyProgression implements GroundedProgressionHeuristic {
    int heuristic = 0;

    public ProGreedyProgression() {

    }

    @Override
    public String getName() {
        return "Greedy Progression";
    }

    @Override
    public void build(ProgressionNetwork tn) {
        this.heuristic = -1 * (tn.numProgressionSteps);
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, ProMethod m) {
        this.build(newTN);
        return this;
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        this.build(newTN);
        return this;
    }

    @Override
    public int getHeuristic() {
        return heuristic;
    }

    @Override
    public boolean goalRelaxedReachable() {
        return true;
    }
}
