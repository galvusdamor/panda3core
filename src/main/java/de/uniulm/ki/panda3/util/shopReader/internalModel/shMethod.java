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

package de.uniulm.ki.panda3.util.shopReader.internalModel;

import java.util.*;

/**
 * Created by dh on 11.08.17.
 */
public class shMethod {
    static HashSet allNames = new HashSet();
    public final String[] decompTask;
    public final List<List<String[]>[]> ifThen = new ArrayList<>();

    public shMethod(String[] decompTask) {
        this.decompTask = decompTask;
    }

    public void addIfThen(List<String[]>[] ifThen) {
        this.ifThen.add(ifThen);
    }

    Set<String> taskVars = null;

    public Set<String> addedVarsInLayer(int l) {
        if (taskVars == null) {
            taskVars = varsOfTask();
        }
        HashSet<String> res = new HashSet<>();
        List<String[]> prec = ifThen.get(l)[0];
        List<String[]> tn = ifThen.get(l)[1];
        for (String[] onePrec : prec) {
            for (int i = 2; i < onePrec.length; i++) {
                if (!taskVars.contains(onePrec[i]))
                    res.add(onePrec[i]);
            }
        }
        for (String[] oneTask : tn) {
            for (int i = 1; i < oneTask.length; i++) {
                if (!taskVars.contains(oneTask[i]))
                    res.add(oneTask[i]);
            }
        }

        return res;
    }

    public Set<String> varsOfTask() {
        taskVars = new HashSet<>();
        for (int i = 1; i < decompTask.length; i++)
            taskVars.add(decompTask[i]);
        return taskVars;
    }

    public String getName() {
        String name = "m-" + decompTask[0] + "-";
        int i = 1;
        while (allNames.contains(name + i)) {
            i++;
        }
        name = name + i;
        allNames.add(name);
        return name;
    }
}
