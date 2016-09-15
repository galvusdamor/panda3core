package de.uniulm.ki.panda3.progression.htn.search;

import java.util.List;

/**
 * Created by dh on 15.09.16.
 */
public abstract class ProgressionSearchRoutine {

    protected String getInfoStr(int searchnodes, int fringesize, int bestMetric, progressionNetwork n, long searchtime) {
        return "nodes/sec: " + Math.round(searchnodes / ((System.currentTimeMillis() - searchtime) / 1000.0)) + " - generated nodes: " + searchnodes + " - fringe size: " + fringesize + " - best heuristic: " + bestMetric
                + " - current heuristic: " + n.solution.size() + " - " + n.metric;
    }

    public abstract List<Object> search(progressionNetwork firstSearchNode);
}
