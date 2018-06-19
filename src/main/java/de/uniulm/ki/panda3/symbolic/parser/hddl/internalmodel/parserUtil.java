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

package de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel;

import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.logic.Variable;
import scala.collection.Seq;

/**
 * Created by dhoeller on 29.06.15.
 */
public class parserUtil {
    public static Variable getVarByName(Seq<Variable> parameters, String name) {
        Variable methodVar = null;
        for (int j = 0; j < parameters.size(); j++) {
            if (parameters.apply(j).name().equals(name)) {
                methodVar = parameters.apply(j);
                break;
            }
        }
        return methodVar;
    }

    public static Task taskByName(String taskname, Seq<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.apply(i);
            if (t.name().equals(taskname)) {
                return t;
            }
        }
        return null;
    }

}
