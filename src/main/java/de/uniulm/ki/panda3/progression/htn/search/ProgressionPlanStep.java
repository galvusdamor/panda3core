package de.uniulm.ki.panda3.progression.htn.search;

import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.*;

/**
 * Created by dhoeller on 22.07.16.
 */
public class ProgressionPlanStep {
    private final Task task;
    public final Integer taskIndex;
    public Set<ProgressionPlanStep> successorList = new HashSet<>();
    public final boolean isPrimitive;

    public int action;
    public List<ProMethod> methods;
    public BitSet reachableTasks;
    public BitSet goalFacts;
    public boolean done;

    public Task getTask() {
        return task;
    }

    public ProgressionPlanStep(Task task) {
        this.task = task;
        this.isPrimitive = task.isPrimitive();
        this.taskIndex = ProgressionNetwork.taskToIndex.get(task);
    }

    @Override
    public String toString() {
        return super.toString() + "-" + task.shortInfo();
    }

}
