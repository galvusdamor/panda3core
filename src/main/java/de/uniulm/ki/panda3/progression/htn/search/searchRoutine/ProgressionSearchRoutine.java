package de.uniulm.ki.panda3.progression.htn.search.searchRoutine;

import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;

import java.util.List;

/**
 * Created by dh on 15.09.16.
 */
public abstract class ProgressionSearchRoutine {

    protected String getInfoStr(int searchnodes, int fringesize, int bestMetric, ProgressionNetwork n, long searchtime) {
        return "nodes/sec: " + Math.round(searchnodes / ((System.currentTimeMillis() - searchtime) / 1000.0))
                + " - generated nodes: " + searchnodes
                + " - fringe size: " + fringesize
                //+ " - best heuristic: " + bestMetric
                + " - current modification depth: " + n.solution.size()
                + " - current heuristic: " + n.metric;
    }

    public abstract List<Object> search(ProgressionNetwork firstSearchNode);

    public abstract List<Object> search(ProgressionNetwork firstSearchNode, InformationCapsule info, TimeCapsule timing);
}
