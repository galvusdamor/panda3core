// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
