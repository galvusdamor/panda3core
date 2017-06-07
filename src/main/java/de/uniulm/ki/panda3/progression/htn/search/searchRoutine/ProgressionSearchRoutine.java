package de.uniulm.ki.panda3.progression.htn.search.searchRoutine;

import de.uniulm.ki.panda3.progression.htn.representation.SolutionStep;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;

/**
 * Created by dh on 15.09.16.
 */
public abstract class ProgressionSearchRoutine {

    public long wallTime = -1;

    protected String getInfoStr(int searchnodes, int fringesize, int greedyness, ProgressionNetwork n, long searchtime) {
        return "nodes/sec: " + Math.round(searchnodes / ((System.currentTimeMillis() - searchtime) / 1000.0))
                + " - generated nodes: " + searchnodes
                + " - fringe size: " + fringesize
                //+ " - best heuristic: " + bestMetric
                + " - current modification depth: " + n.solution.getLength()
                + " - "
                //if (greedyness > 1)
                //s += greedyness + "*";
                + "g(s)+h(s)= " + n.metric;
    }

    public abstract SolutionStep search(ProgressionNetwork firstSearchNode);

    public abstract SolutionStep search(ProgressionNetwork firstSearchNode, InformationCapsule info, TimeCapsule timing);

    public String SearchName() {
        return "unknown";
    }
}
