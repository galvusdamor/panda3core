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

package de.uniulm.ki.panda3.progression.proUtil;

/**
 * Created by dhoeller on 27.07.16.
 */
public class dotNode implements Comparable<dotNode> {

    static int getID = 0;

    public int interalID = getID++;
    public final int id;
    public final String visibleName;
    private String style = "";

    public dotNode(int identifier, String visibleName) {
        this.id = identifier;
        this.visibleName = visibleName;
    }

    public dotNode(int identifier, String visibleName, String style) {
        this(identifier, visibleName);
        this.style = style;
    }

    @Override
    public int compareTo(dotNode other) {
        return (this.id - other.id);
    }
}
