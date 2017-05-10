package de.uniulm.ki.panda3.progression.htn.search;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by dhoeller on 22.07.16.
 */
public class ProgressionPlanStep {
    private final Task task;
    public Set<ProgressionPlanStep> successorList = new HashSet<>();
    public final boolean isPrimitive;

    public int action;
    public List<method> methods;

    public Task getTask() {
        return task;
    }

    public ProgressionPlanStep(Task task) {
        this.task = task;
        this.isPrimitive = task.isPrimitive();
    }

    @Override
    public String toString() {
        return super.toString() + "-" + task.shortInfo();
    }
}
