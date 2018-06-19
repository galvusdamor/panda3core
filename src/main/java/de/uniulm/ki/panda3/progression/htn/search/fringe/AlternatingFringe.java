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

import java.util.PriorityQueue;

/**
 * Created by dh on 26.07.17.
 */
public class AlternatingFringe<T> implements IFringe<T> {
    PriorityQueue<T> std = new PriorityQueue<>();
    PriorityQueue<T> pref = new PriorityQueue<>();

    boolean prefRound = false;

    @Override
    public T poll() {
        if (pref.isEmpty())
            return std.poll();
        if (std.isEmpty())
            return pref.poll();
        prefRound = !prefRound;
        if (prefRound)
            return pref.poll();
        else return std.poll();
    }

    @Override
    public void add(T node, boolean prefered) {
        if (prefered)
            pref.add(node);
        else
            std.add(node);
    }

    @Override
    public void add(T node) {
        std.add(node);
    }


    @Override
    public boolean isEmpty() {
        return std.isEmpty() && pref.isEmpty();
    }

    @Override
    public void clear() {
        std.clear();
        pref.clear();
    }

    @Override
    public int size() {
        return std.size() + pref.size();
    }
}
