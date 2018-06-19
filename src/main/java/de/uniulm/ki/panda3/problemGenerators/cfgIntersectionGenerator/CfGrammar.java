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

package de.uniulm.ki.panda3.problemGenerators.cfgIntersectionGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 20.02.17.
 */
public class CfGrammar {
    List<String> terminal = null;
    List<String> nonterminal = null;
    List<String> rulesLeft = null;
    List<List<String>> rulesRight = null;
    String start = null;

    public CfGrammar(String path) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(path));
        while (br.ready()) {
            String line = br.readLine().trim();
            if ((line.trim().length() == 0) || (line.trim().startsWith("#"))) {
                continue;
            }
            String[] split = line.split("=");
            assert split.length == 2;
            String rightHandSide = split[1].trim();
            if (!split[0].trim().startsWith("S")) {
                assert (rightHandSide.startsWith("{") && rightHandSide.endsWith("}"));
                rightHandSide = rightHandSide.substring(1, rightHandSide.length() - 1).trim();
            }

            if (line.startsWith("T")) {
                assert terminal == null;
                terminal = readSet(rightHandSide);
            } else if (line.startsWith("N")) {
                assert nonterminal == null;
                nonterminal = readSet(rightHandSide);
            } else if (line.startsWith("P")) {
                assert rulesLeft == null;
                rulesLeft = new ArrayList<>();
                rulesRight = new ArrayList<>();
                String[] split2 = rightHandSide.split(",");
                for (String rule : split2) {
                    String[] split3 = rule.split("->");
                    rulesLeft.add(split3[0].trim());
                    ArrayList<String> subSym = new ArrayList<String>();
                    rulesRight.add(subSym);
                    if (split3[1].equals("epsilon")) {

                    } else if (!split3[1].contains("-")) {
                        subSym.add(split3[1]);
                    } else {
                        for (String s : split3[1].split("-")) {
                            subSym.add(s.trim());

                        }
                    }
                }
            } else if (line.startsWith("S")) {
                assert start == null;
                start = rightHandSide;
            }
        }
        br.close();
    }

    public CfGrammar(List<String> terminal, List<String> nonterminal, List<String> rulesLeft, List<List<String>> rulesRight, String start) {
        this.terminal = terminal;
        this.nonterminal = nonterminal;
        this.rulesLeft = rulesLeft;
        this.rulesRight = rulesRight;
        this.start = start;
    }

    private List<String> readSet(String rightHandSide) {
        List<String> res = new ArrayList<>();
        String[] split = rightHandSide.split(",");
        for (String elem : split) {
            res.add(elem.trim());
        }
        return res;
    }
}
