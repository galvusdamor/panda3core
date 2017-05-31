package de.uniulm.ki.panda3.progression.relaxedPlanningGraph.hierarchyAware;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.PriorityQueueSearch;
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.ProgressionSearchRoutine;
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.SolutionStep;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.htnGroundedProgressionHeuristic;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.proBFS;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by dh on 20.09.16.
 */
public class delRelaxedHTN implements htnGroundedProgressionHeuristic {
    private final HashMap<Task, HashMap<GroundTask, List<method>>> methods;
    private int heuristicValue;
    private boolean relaxedRechable;
    private final Set<GroundTask> allActions;

    public delRelaxedHTN(HashMap<Task, HashMap<GroundTask, List<method>>> methods, Set<GroundTask> allActions) {
        this.methods = methods;
        this.allActions = allActions;
    }

    @Override
    public String getName() {
        return "delRelexedHTN";
    }

    @Override
    public void build(ProgressionNetwork initialNode) {
        //initialNode = initialNode.clone();
        //initialNode.heuristic = new simpleCompositionRPG(operators.methods, allActions);
        //initialNode.heuristic = new RCG(methods, this.allActions);
        //initialNode.heuristic = new greedyProgression();
        initialNode.heuristic = new proBFS();
        initialNode.heuristic.build(initialNode);
        initialNode.metric = initialNode.heuristic.getHeuristic();

        ProgressionSearchRoutine routine;
        assert(false);
        routine = new PriorityQueueSearch(false, true, false, PriorityQueueSearch.abstractTaskSelection.random);
        //routine = new EnforcedHillClimbing();
        //routine = new CompleteEnforcedHillClimbing();

        SolutionStep sol = routine.search(initialNode);
        if (sol != null) {
            this.heuristicValue = sol.getLength();
            this.relaxedRechable = true;
        } else {
            this.heuristicValue = Integer.MAX_VALUE;
            this.relaxedRechable = false;
        }
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, method m) {
        delRelaxedHTN drhtn = new delRelaxedHTN(this.methods, this.allActions);
        drhtn.build(newTN);
        return drhtn;
    }

    @Override
    public htnGroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        delRelaxedHTN drhtn = new delRelaxedHTN(this.methods, this.allActions);
        drhtn.build(newTN);
        return drhtn;
    }

    @Override
    public int getHeuristic() {
        return this.heuristicValue;
    }

    @Override
    public boolean goalRelaxedReachable() {
        return this.relaxedRechable;
    }
}
