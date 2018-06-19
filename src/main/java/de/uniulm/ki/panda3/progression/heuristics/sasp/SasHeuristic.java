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

package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.progression.heuristics.sasp.IncrementalCalc.IncrementInformation;

import java.util.BitSet;

/**
 * Created by dh on 02.05.17.
 */
public abstract class SasHeuristic {
    public static final int cUnreachable = Integer.MAX_VALUE;
    public BitSet helpfulOps;

    public enum SasHeuristics {hFilter, hMax, hAdd, hFF, hFFwithHA, hCG, hLmCut, hLmCutOpt, hIncLmCut, noSearch}

    protected boolean isIncremental = false;

    public boolean isIncremental() {
        return this.isIncremental;
    }

    public IncrementInformation getIncInf() {
        return null;
    }

    public int calcHeu(int lastAction, IncrementInformation inc, BitSet s0, BitSet g) {
        return this.calcHeu(s0, g);
    }

    public abstract int calcHeu(BitSet s0, BitSet g);

    public boolean debug = false;

    protected void debugOut(String s) {
        if (debug)
            System.out.print(s);
    }
}
