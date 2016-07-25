package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.search.proPlanStep;
import de.uniulm.ki.panda3.progression.htn.search.progressionNetwork;

import java.util.BitSet;

/**
 * Created by dhoeller on 25.07.16.
 */
public interface htnGroundedProgressionHeuristic {

    void build(progressionNetwork tn);

    htnGroundedProgressionHeuristic update(progressionNetwork newTN, proPlanStep ps, method m);

    htnGroundedProgressionHeuristic update(progressionNetwork newTN, proPlanStep ps);

    int getHeuristic();

    boolean goalRelaxedReachable();
}
