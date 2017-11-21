package de.uniulm.ki.panda3.progression.heuristics.htn;

import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;

/**
 * Created by dh on 19.09.16.
 */
public class gphDFS extends GroundedProgressionHeuristic {
    int heuristic = 0;

    public gphDFS() {

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
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, ProMethod m) {
        gphDFS h = new gphDFS();
        h.build(newTN);
        return h;
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        gphDFS h = new gphDFS();
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
