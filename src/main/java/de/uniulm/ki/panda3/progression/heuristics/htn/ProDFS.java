package de.uniulm.ki.panda3.progression.heuristics.htn;

import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;

/**
 * Created by dh on 19.09.16.
 */
public class ProDFS extends GroundedProgressionHeuristic {
    int heuristic = 0;

    public ProDFS() {

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
        ProDFS h = new ProDFS();
        h.build(newTN);
        return h;
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        ProDFS h = new ProDFS();
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
