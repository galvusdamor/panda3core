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
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntStack;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;

import java.util.BitSet;

/**
 * Created by dh on 15.05.17.
 */
public class hFFRtg extends RTGBaseCalc {
    int[] emptyAchieverList;

    public hFFRtg(SasPlusProblem p) {
        super(p);
        this.evalBestAchievers = true;
        this.trackPCF = false;
        this.earlyAbord = true;
        emptyAchieverList = new int[this.waitingForNodes.length];
        for (int i = 0; i < emptyAchieverList.length; i++)
            emptyAchieverList[i] = -1;
    }

    int[] achiever;

    @Override
    protected void evalAchiever(int nodeId, int bestAchiever) {
        assert bestAchiever >= 0;
        achiever[nodeId] = bestAchiever;
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        achiever = emptyAchieverList.clone();

        super.calcHeu(s0, g);

        UUIntStack fringe = new UUIntStack();
        int f = g.nextSetBit(0);
        while (f >= 0) {
            fringe.push(f);
            f = g.nextSetBit(f + 1);
        }
        return calcFF(s0, fringe);
    }

    private int calcFF(BitSet s0, UUIntStack fringe) {
        int h = 0;
        BitSet done = new BitSet();
        while (!fringe.isEmpty()) {
            int f = fringe.pop();
            if (done.get(f) || s0.get(f) || waitingForNodes[f].length == 0)
                continue;
            if (isAndNode.get(f)) {
                h += this.costs[f];
                for (int n : waitingForNodes[f])
                    fringe.push(n);
            } else if (achiever[f] > -1)
                fringe.push(achiever[f]);
            done.set(f);
        }
        return h;
    }

    @Override
    int eAND() {
        return 0;
    }

    @Override
    int eOR() {
        return Integer.MAX_VALUE;
    }

    @Override
    int combineAND(int x, int y) {
        return x + y;
    }

    @Override
    int combineOR(int x, int y) {
        return Integer.min(x, y);
    }
}
