package de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedComposition;

import de.uniulm.ki.panda3.progression.TDGReachabilityAnalysis.IActionReachability;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dh on 16.08.17.
 */
public abstract class RelaxedCompositionEncoding extends SasPlusProblem {
    public int methodCosts = 0;
    public int numOfNonHtnActions;
    public IActionReachability tdRechability;
    public void generateTaskCompGraph(HashMap<Task, List<ProMethod>> methods, List<ProgressionPlanStep> initialTasks){};

    public abstract BitSet initS0();

    public abstract void setReachable(BitSet bSet, int i);

    public abstract void setReached(BitSet bSet, int i);
}
