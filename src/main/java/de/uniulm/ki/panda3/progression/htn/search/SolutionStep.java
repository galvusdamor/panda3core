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

import de.uniulm.ki.panda3.symbolic.domain.SimpleDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dh on 15.05.17.
 */
public class SolutionStep {

    private SolutionStep predecessor = null;

    // my step
    private int action;
    private SimpleDecompositionMethod method;

    protected int length;
    protected int primitiveCount;
    protected int shopCount;

    public SolutionStep() {
        length = 0;
        primitiveCount = 0;
        shopCount = 0;
    }

    public SolutionStep(SolutionStep predSol, int action) {
        this.predecessor = predSol;
        this.action = action;
        this.length = predSol.length + 1;
        this.primitiveCount = predSol.primitiveCount;
        this.shopCount = predSol.shopCount;

        if (!ProgressionNetwork.ShopPrecActions.contains(action))
            primitiveCount++;
        else
            shopCount++;
    }

    public SolutionStep(SolutionStep predSol, SimpleDecompositionMethod method) {
        this.predecessor = predSol;
        this.method = method;
        this.length = predSol.length + 1;
        this.primitiveCount = predSol.primitiveCount;
        this.shopCount = predSol.shopCount;
    }

    @Override
    public String toString() {

        if (predecessor != null) {
            String me;
            if (this.method != null) {
                me = this.method.name() + " @ " + this.method.abstractTask().shortInfo();
            } else {
                me = ProgressionNetwork.flatProblem.opNames[this.action];
            }
            String other = "";
            other = predecessor.toString();
            if (other.length() > 0)
                me = "\n" + me;
            return other + me;
        } else
            return ""; // the first node is a dummy
    }

    public Task[] toPrimitiveSequence() {
        Task[] solution = new Task[primitiveCount + shopCount];
        int currentPos = solution.length - 1;

        SolutionStep currentStep = this;
        while (currentStep.predecessor != null) {
            if (currentStep.method == null) {
                solution[currentPos--] = ProgressionNetwork.indexToTask[currentStep.action];
            }
            currentStep = currentStep.predecessor;
        }

        return solution;
    }


    public boolean isFirst() {
        return (predecessor == null);
    }

    public int getLength() {
        return this.length - this.shopCount;
    }

    public int getPrimitiveCount() {
        return primitiveCount;
    }

    public List<Object> getSolution() {
        if (this.isFirst())
            return new LinkedList<>();
        else {
            List<Object> l = predecessor.getSolution();
            Object o;
            if (this.method != null)
                o = this.method;
            else o = this.action;
            ((LinkedList) l).add(o);
            return l;
        }
    }
}
