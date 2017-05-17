package de.uniulm.ki.panda3.progression.htn.search.searchRoutine;

import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;

import java.util.List;

/**
 * Created by dh on 15.09.16.
 */
public abstract class ProgressionSearchRoutine {

    public long wallTime = -1;

    protected String getInfoStr(int searchnodes, int fringesize, int bestMetric, ProgressionNetwork n, long searchtime) {
        return "nodes/sec: " + Math.round(searchnodes / ((System.currentTimeMillis() - searchtime) / 1000.0))
                + " - generated nodes: " + searchnodes
                + " - fringe size: " + fringesize
                //+ " - best heuristic: " + bestMetric
                + " - current modification depth: " + n.solution.getLength()
                + " - current heuristic: " + n.metric;
    }

    public abstract SolutionStep search(ProgressionNetwork firstSearchNode);

    public abstract SolutionStep search(ProgressionNetwork firstSearchNode, InformationCapsule info, TimeCapsule timing);

    public String SearchName(){
        return "unknown";
    }
}
