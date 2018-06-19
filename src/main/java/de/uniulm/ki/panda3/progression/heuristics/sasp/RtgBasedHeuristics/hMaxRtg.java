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

package de.uniulm.ki.panda3.progression.heuristics.sasp.RtgBasedHeuristics;

import de.uniulm.ki.panda3.progression.heuristics.sasp.RtgBasedHeuristics.RTGBaseCalc;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;

/**
 * Created by dh on 01.05.17.
 */
public class hMaxRtg extends RTGBaseCalc {
    public hMaxRtg(SasPlusProblem p, boolean trackPCF) {
        super(p, trackPCF);
    }

    public hMaxRtg(SasPlusProblem p) {
        super(p);
    }

    @Override
    int eAND() {
        return Integer.MIN_VALUE;
    }

    @Override
    int eOR() {
        return Integer.MAX_VALUE;
    }

    @Override
    int combineAND(int x, int y) {
        return Integer.max(x, y);
    }

    @Override
    int combineOR(int x, int y) {
        return Integer.min(x, y);
    }
}
