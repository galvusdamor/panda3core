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

package de.uniulm.ki.panda3.progression.htn.search.fringe;

import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;

import java.util.PriorityQueue;

/**
 * Created by dh on 26.07.17.
 */
public class QueueBasedFringe<T> implements IFringe<T> {
    PriorityQueue<T> queue = new PriorityQueue<>();

    @Override
    public T poll() {
        return queue.poll();
    }

    @Override
    public void add(T node, boolean prefered) {
        queue.add(node);
    }

    @Override
    public void add(T node) {
        queue.add(node);
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public int size() {
        return queue.size();
    }
}
