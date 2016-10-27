package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;

/**
 * Created by dh on 19.09.16.
 */
public class greedyProgression implements htnGroundedProgressionHeuristic {
    int heuristic = 0;

    public greedyProgression() {

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
    public htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, method m) {
        this.build(newTN);
        return this;
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
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
