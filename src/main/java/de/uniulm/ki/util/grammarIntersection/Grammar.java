package de.uniulm.ki.util.grammarIntersection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 20.02.17.
 */
public class Grammar {
    List<String> terminal = null;
    List<String> nonterminal = null;
    List<String> rulesLeft = null;
    List<List<String>> rulesRight = null;
    String start = null;

    public Grammar(String path) throws Exception {
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

    private List<String> readSet(String rightHandSide) {
        List<String> res = new ArrayList<>();
        String[] split = rightHandSide.split(",");
        for (String elem : split) {
            res.add(elem.trim());
        }
        return res;
    }
}
