package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;

/**
 * Created by dh on 19.09.16.
 */
public class proDFS implements htnGroundedProgressionHeuristic {
    int heuristic = 0;

    public proDFS() {

    }

    @Override
    public String getName() {
        return "simulated DFS";
    }

    @Override
    public void build(ProgressionNetwork tn) {
        this.heuristic = -(tn.numProgressionSteps + tn.numDecompositionSteps);
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, method m) {
        proDFS h = new proDFS();
        h.build(newTN);
        return h;
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        proDFS h = new proDFS();
        h.build(newTN);
        return h;
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
