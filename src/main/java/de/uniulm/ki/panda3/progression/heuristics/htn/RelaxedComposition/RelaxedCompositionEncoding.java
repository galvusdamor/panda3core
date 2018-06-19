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
    public int methodCosts = 1;
    int numOfNonHtnActions;
    public IActionReachability tdRechability;
    public void generateTaskCompGraph(HashMap<Task, List<ProMethod>> methods, List<ProgressionPlanStep> initialTasks){};

    public abstract BitSet initS0();

    public abstract void setReachable(BitSet bSet, int i);

    public abstract void setReached(BitSet bSet, int i);
}
