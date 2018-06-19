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

package de.uniulm.ki.panda3.progression.htn.representation;

import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;

import java.util.List;

/**
 * Created by dhoeller on 22.07.16.
 */
public class ProSubtaskNetwork {
    private final ProgressionPlanStep[] steps;
    private final List<ProgressionPlanStep> firsts;
    private final List<ProgressionPlanStep> lasts;

    public ProSubtaskNetwork(ProgressionPlanStep[] steps, List<ProgressionPlanStep> firsts, List<ProgressionPlanStep> lasts) {
        this.steps = steps;
        this.firsts = firsts;
        this.lasts = lasts;
    }

    public List<ProgressionPlanStep> getLastNodes() {
        return this.lasts;
    }

    public List<ProgressionPlanStep> getFirstNodes() {
        return this.firsts;
    }

    public int size() {
        return steps.length;
    }
}
