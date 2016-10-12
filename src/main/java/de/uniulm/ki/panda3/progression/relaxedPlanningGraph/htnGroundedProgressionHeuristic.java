package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;

/**
 * Created by dhoeller on 25.07.16.
 */
public interface htnGroundedProgressionHeuristic {

    String getName();

    void build(ProgressionNetwork tn);

    htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, method m);

    htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps);

    int getHeuristic();

    boolean goalRelaxedReachable();
}
