package de.uniulm.ki.panda3.progression.heuristics.htn;

import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;

/**
 * Created by dh on 19.09.16.
 */
public class ProBFS implements GroundedProgressionHeuristic {
    int heuristic = 0;

    public ProBFS() {

    }

    @Override
    public String getName() {
        return "simulated BFS";
    }

    @Override
    public void build(ProgressionNetwork tn) {
        this.heuristic = tn.solution.getLength();
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, ProMethod m) {
        ProBFS h = new ProBFS();
        h.build(newTN);
        return h;
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        ProBFS h = new ProBFS();
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
