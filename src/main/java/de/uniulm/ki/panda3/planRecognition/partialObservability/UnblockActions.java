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

package de.uniulm.ki.panda3.planRecognition.partialObservability;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 01.02.17.
 */
public class UnblockActions {
    enum context {declaration, precondition, effect}

    public static void main(String[] args) throws Exception {
        //String inDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/07-partial-observable/02-unblock-actions/";
        //String inDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/kitchen-partobs/06-unlocked";
        String inDir = "/home/dh/Schreibtisch/ICAPS-ExpsFromSamwise/05-plan-rec-instances";
        File dir = new File(inDir);
        for (File f : dir.listFiles()) {
            BufferedReader br = new BufferedReader(new FileReader(f));
            List<String> lines = new ArrayList<>();
            boolean found = false;
            int lastLit = 1;
            while (br.ready()) {
                String line = br.readLine() + "\n";
                lines.add(line);
                if (line.trim().startsWith("(l1)"))
                    found = true;
                else if (found && (line.trim().startsWith("(l"))) {
                    lastLit++;
                } else if (found) {
                    found = false;
                }
            }
            br.close();


            List<String> newLines = new ArrayList<>();
            context c = context.declaration;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.indexOf("precondition") > -1)
                    c = context.precondition;
                else if (line.indexOf("effect") > -1)
                    c = context.effect;
                if ((c == context.precondition) && line.contains("(l" + lastLit + ")")) {
                    line = line.replaceAll("\\(l" + lastLit + "\\)", "");
                }
                if (line.trim().length() > 0)
                    newLines.add(line);
            }
            lines = newLines;

            newLines = new ArrayList<>();
            for (int i = 0; i < lines.size() - 1; i++) {
                String l1 = lines.get(i).trim();
                String l2 = lines.get(i + 1).trim();
                if (l1.equals("(and") && l2.equals(")")){
                    i++;
                    newLines.add("()\n");
                }
                else
                    newLines.add(lines.get(i));
            }
            newLines.add(")\n");
            lines = newLines;

            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            for (String line : lines)
                bw.write(line);
            bw.close();
        }

    }
}
