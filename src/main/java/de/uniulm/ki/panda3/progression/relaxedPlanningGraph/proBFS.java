package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;

/**
 * Created by dh on 19.09.16.
 */
public class proBFS implements htnGroundedProgressionHeuristic {
    int heuristic = 0;

    public proBFS() {

    }

    @Override
    public String getName() {
        return "simulated BFS";
    }

    @Override
    public void build(ProgressionNetwork tn) {
        this.heuristic = tn.solution.size();
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, method m) {
        proBFS h = new proBFS();
        h.build(newTN);
        return h;
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        proBFS h = new proBFS();
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
