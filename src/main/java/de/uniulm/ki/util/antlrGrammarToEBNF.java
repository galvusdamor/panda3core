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

package de.uniulm.ki.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by dh on 15.10.17.
 */
public class antlrGrammarToEBNF {
    public static void main(String[] args) throws Exception {
        String filename = "/home/dh/IdeaProjects/panda3core_with_planning_graph/src/main/java/de/uniulm/ki/panda3/symbolic/parser/hddl/antlrHDDL.g4";
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();
        System.out.println("%\n% the following definition is automatically generated.\n% do not change here! changes will be overwritten on next build!\n%\n");
        while (br.ready()) {
            if (line.trim().equals("grammar antlrHDDL;")) {
            } else if (line.trim().length() == 0) {
            } else if (line.trim().equals("//")) {
            } else if (line.trim().startsWith("/*")) {
            } else if (line.trim().startsWith("*")) {
            } else if (line.trim().startsWith("// @MODIFIED")) {
                System.out.println("\\modifiedPDDL{}");
            } else if (line.trim().toLowerCase().equals("// @ignore")) {
                line = ignoreNext(br, line);
                continue;
            } else if (line.trim().startsWith("// @LABEL")) {
                line = line.trim().substring("// @LABEL".length()).trim();
                line = "\\grammarLabel" + line;
                System.out.println(line);
                while (!line.endsWith("}")) {
                    line = br.readLine();
                    line = line.trim().substring("//".length());
                    System.out.println(line);
                }
            } else if (line.trim().startsWith("// @EXAMPLE")) {
                line = line.trim().substring("// @EXAMPLE".length()).trim();
                line = "\\grammarExample" + line;
                System.out.println(line);
                while (!line.endsWith("}")) {
                    line = br.readLine();
                    line = line.trim().substring("//".length());
                    System.out.println(line);
                }
            } else if (line.trim().startsWith("// @PDDL")) {
                System.out.println("\\originalPDDL{}");
            } else if (line.trim().startsWith("// @HDDL")) {
                System.out.println("\\newForHTN{}");
            } else if (line.trim().startsWith("// ")) {
                line = line.trim().substring("// ".length());
                line = "\\grammarSection{" + line + "}";
                System.out.println(line);
            } else {
                System.out.println("\\begin{verbatim}");
                while ((br.ready()) && (line.trim().length() > 0) && (!line.trim().startsWith("//"))) {
                    System.out.println(line.replaceAll("\\@HIGHLIGHT", "<--"));
                    line = br.readLine();
                }
                System.out.println("\\end{verbatim}");
                continue;
            }
            line = br.readLine();

        }
        br.close();
    }

    private static String ignoreNext(BufferedReader br, String line) throws IOException {
        while (line.trim().startsWith("//")) {
            line = br.readLine();
        }
        while (!line.trim().startsWith("//")) {
            line = br.readLine();
        }
        return line;
    }
}
