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

package de.uniulm.ki.panda3.progression.htn.search.searchRoutine;

import de.uniulm.ki.panda3.progression.htn.search.SolutionStep;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;

/**
 * Created by dh on 15.09.16.
 */
public abstract class ProgressionSearchRoutine {

    public long wallTime = -1;

    protected String getInfoStr(int searchnodes, int fringesize, int greedyness, ProgressionNetwork n, long searchtime) {
        return "nodes/sec: " + Math.round(searchnodes / ((System.currentTimeMillis() - searchtime) / 1000.0))
                + " - generated nodes: " + searchnodes
                + " - fringe size: " + fringesize
                //+ " - best heuristic: " + bestMetric
                + " - current modification depth: " + n.solution.getLength()
                + " - "
                //if (greedyness > 1)
                //s += greedyness + "*";
                + "g(s)+h(s)= " + n.metric;
    }

    public abstract SolutionStep search(ProgressionNetwork firstSearchNode);

    public abstract SolutionStep search(ProgressionNetwork firstSearchNode, InformationCapsule info, TimeCapsule timing);

    public String SearchName() {
        return "unknown";
    }
}
