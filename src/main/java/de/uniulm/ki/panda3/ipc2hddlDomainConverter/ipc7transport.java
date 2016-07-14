package de.uniulm.ki.panda3.ipc2hddlDomainConverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhoeller on 14.07.16.
 */
public class ipc7transport {
    public static void main(String[] str) throws Exception {
        //str = new String[1];
        //str[0] = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/p20.pddl";

        System.out.println("This program reads a IPC7 transport problem file and writes a pure-strips version as well as an HTN version.");
        if (str.length < 1) {
            System.out.println("Please give domain");
            return;
        }

        String file = str[0];

        BufferedReader br = new BufferedReader(new FileReader(file));
        BufferedWriter stripsw = new BufferedWriter(new FileWriter(file.replace(".pddl", "-strips.lisp")));

        List<String> preamble = new ArrayList<>();
        List<String> goal = new ArrayList<>();
        List<String> middle = new ArrayList<>();
        List<String> end = new ArrayList<>();

        boolean readingPreamble = true;
        boolean readingMiddle = false;
        boolean readingGoal = false;
        boolean readingEnd = false;

        while (br.ready()) {
            String line = br.readLine();
            if ((!readingPreamble) && (line.trim().startsWith("(=") || line.trim().startsWith(";") || line.trim().startsWith("(:metric")))
                continue;

            if (readingPreamble) {
                if (line.trim().startsWith("(:init")) {
                    readingPreamble = false;
                    readingMiddle = true;
                } else {
                    preamble.add(line);
                }
            }
            if (readingMiddle) {
                if (line.trim().startsWith("(:goal")) {
                    readingMiddle = false;
                    readingGoal = true;
                } else {
                    middle.add(line);
                }
            }
            if (readingGoal) {
                if (line.trim().startsWith(")")) {
                    readingGoal = false;
                    readingEnd = true;
                } else {
                    goal.add(line);
                }
            }
            if (readingEnd) {
                end.add(line);
            }

            if (line.trim().length() > 0)
                stripsw.write(line + "\n");
        }
        br.close();
        stripsw.close();

        BufferedWriter htnw = new BufferedWriter(new FileWriter(file.replace(".pddl", "-htn.lisp")));

        for (String s : preamble) {
            htnw.write(s + "\n");
        }

        htnw.write(" (:htn\n" + "  :tasks (and\n");
        for (int i = 1; i < goal.size(); i++) {
            String s = goal.get(i);
            htnw.write(" " + s.replace("(at", "(deliver"));
            if (i == goal.size() - 1) {
                htnw.write(")");
            }
            htnw.write("\n");
        }
        htnw.write("  :ordering ( )\n" + "  :constraints ( ))\n");

        for (String s : middle) {
            htnw.write(s + "\n");
        }

        for (int i = 1; i < end.size(); i++) {
            String s = end.get(i);
            htnw.write(s + "\n");
        }

        htnw.close();
    }
}
