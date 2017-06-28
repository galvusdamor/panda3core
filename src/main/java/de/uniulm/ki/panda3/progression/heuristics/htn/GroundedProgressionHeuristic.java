package de.uniulm.ki.panda3.progression.heuristics.htn;

import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;

/**
 * Created by dhoeller on 25.07.16.
 */
public interface GroundedProgressionHeuristic {

    String getName();

    void build(ProgressionNetwork tn);

    GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, ProMethod m);

    GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps);

    int getHeuristic();

    boolean goalRelaxedReachable();
}
