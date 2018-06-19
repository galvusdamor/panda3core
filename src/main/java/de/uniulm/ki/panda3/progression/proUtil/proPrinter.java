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

import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;

import java.util.BitSet;

/**
 * Created by dhoeller on 02.07.16.
 */
public class proPrinter {

    //public static int numStateFeatures;
    //public static GroundLiteral[] IndexToLiteral;

    public static String actionTupleToStr(Task task, BitSet prec, BitSet add, BitSet del, int numStateFeatures, GroundLiteral[] IndexToLiteral) {
        StringBuilder sb = new StringBuilder();
        sb.append(actionToStr(task));
        sb.append(" = {");
        sb.append(bitsetToStr(prec, numStateFeatures, IndexToLiteral));
        sb.append("}, {");
        sb.append(bitsetToStr(add, numStateFeatures, IndexToLiteral));
        sb.append("}, {");
        sb.append(bitsetToStr(del, numStateFeatures, IndexToLiteral));
        sb.append("}");
        return sb.toString();
    }

    public static String literalToStr(GroundLiteral groundLiteral) {
        StringBuilder sb = new StringBuilder();
        sb.append(groundLiteral.predicate().name());
        for (int i = 0; i < groundLiteral.parameter().size(); i++) {
            sb.append("-");
            sb.append(groundLiteral.parameter().apply(i).name());
        }
        return sb.toString();
    }

    public static String actionToStr(Task groundTask) {
        StringBuilder sb = new StringBuilder();
        sb.append(groundTask.name());
        /*for (int i = 0; i < groundTask.arguments().size(); i++) {
            sb.append("-");
            sb.append(groundTask.arguments().apply(i).name());
        }*/
        return sb.toString();
    }

    public static String bitsetToStr(BitSet bitset, int numStateFeatures, GroundLiteral[] IndexToLiteral) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < numStateFeatures; i++) {
            if (bitset.get(i)) {
                if (first)
                    first = false;
                else
                    sb.append(", ");
                sb.append(IndexToLiteral[i].predicate().name());
                for (int j = 0; j < IndexToLiteral[i].parameter().size(); j++) {
                    sb.append("-");
                    sb.append(IndexToLiteral[i].parameter().apply(j).name());
                }
            }
        }
        return sb.toString();
    }
}
