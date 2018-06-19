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

package de.uniulm.ki.panda3.util.fastIntegerDataStructures;

/**
 * Created by dh on 26.01.17.
 */
public class UUIntStack {

    private int size;
    private intContainer currentContainer;

    private int iIter;
    private intContainer containerIter;


    public UUIntStack() {
        this(100);
    }

    public UUIntStack(int size) {
        this.size = size;
        this.currentContainer = new intContainer();
        this.currentContainer.elements = new int[size];
    }

    public void clear() {
        this.currentContainer.lastContainer = null;
        this.currentContainer.lastIndexSet = -1;
    }

    public boolean isEmpty() {
        return ((this.currentContainer.lastContainer == null) && (this.currentContainer.lastIndexSet == -1));
    }

    public void resetIterator() {
        containerIter = currentContainer;
        while (containerIter.lastContainer != null)
            containerIter = containerIter.lastContainer;
        iIter = 0;
    }

    public boolean hasNext() {
        if (containerIter != currentContainer)
            return true;
        return (iIter <= containerIter.lastIndexSet);
    }

    public int next() {
        if ((containerIter != currentContainer) && (iIter == size)) {
            containerIter = containerIter.nextContainer;
            iIter = 0;
        }
        if (iIter <= containerIter.lastIndexSet) {
            return containerIter.elements[iIter++];
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public void push(int i) {
        if (currentContainer.lastIndexSet == currentContainer.elements.length - 1) {
            intContainer newContainer = new intContainer();
            newContainer.elements = new int[size];
            newContainer.lastContainer = currentContainer;
            this.currentContainer.nextContainer = newContainer;
            this.currentContainer = newContainer;
        }
        currentContainer.elements[++currentContainer.lastIndexSet] = i;
    }

    public int pop() {
        if (this.currentContainer.lastIndexSet >= 0) {
            return this.currentContainer.elements[this.currentContainer.lastIndexSet--];
        } else if (this.currentContainer.lastContainer != null) { // the index is -1, but there is another list
            this.currentContainer = this.currentContainer.lastContainer;
            this.currentContainer.nextContainer = null;
            return this.currentContainer.elements[this.currentContainer.lastIndexSet--];
        } else return Integer.MIN_VALUE;
    }

    public int top() {
        if (this.currentContainer.lastIndexSet >= 0) {
            return this.currentContainer.elements[this.currentContainer.lastIndexSet];
        } else if (this.currentContainer.lastContainer != null) { // the index is -1, but there is another list
            this.currentContainer = this.currentContainer.lastContainer;
            return this.currentContainer.elements[this.currentContainer.lastIndexSet];
        } else return Integer.MIN_VALUE;
    }

    class intContainer {
        int[] elements;
        int lastIndexSet = -1;
        intContainer lastContainer = null;
        intContainer nextContainer = null;
    }
}
