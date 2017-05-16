package de.uniulm.ki.panda3.progression.htn.search;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;

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
    public List<method> methods;
    public BitSet r;
    public BitSet g;
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
