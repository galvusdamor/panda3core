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

package de.uniulm.ki.panda3.translation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Kristof Mickeleit
 * Adds types to a domain or problem.
 * Example: pddl/IPC1/gripper
 */
public class TypeConverter {


    public static void type(String domain, String problem, String domainOutputFile, String problemOutputFile) throws Exception {
        domain = domain.toLowerCase();
        problem = problem.toLowerCase();
        if (!domain.contains("(:types ")) {
            System.out.print(" typing domain ...");
            processDomain(domain, domainOutputFile);
            System.out.print(" typing problem ...");
            processProblem(problem, problemOutputFile);
            System.out.print(" done ...");
        } else {
            System.out.println("Copying files....");
            FileWriter fw = new FileWriter(domainOutputFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(domain.toLowerCase());
            bw.close();


            fw = new FileWriter(problemOutputFile);
            bw = new BufferedWriter(fw);
            bw.write(problem.toLowerCase());
            bw.close();
        }
    }

    private static void processProblem(String txtFile, String outFile) throws Exception {
        FileWriter fw = new FileWriter(outFile);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("(");

        List<String> definitions = parsePlan(txtFile);
        Iterator<String> iterator = definitions.iterator();
        while( iterator.hasNext()) {
            String def = iterator.next();
            if (def.trim().startsWith("define")) {
                bw.write(def + "\n");
            } else if (def.trim().startsWith("(:domain")) {
                bw.write(def + "\n");
            } else if (def.trim().startsWith("(:objects")) {
                bw.write(def.substring(0, def.length()-1) + " - object)\n");
            } else if (def.trim().startsWith("(:init")) {
                bw.write(def + "\n");
            } else if (def.trim().startsWith("(:tasks")) {
                bw.write(def + "\n");
            } else if (def.trim().startsWith("(:goal")) {
                bw.write(def + "\n");
            } else {
                System.out.println("Error, definition type not found: " + def);
            }
        }
        bw.write(")");
        bw.close();
        fw.close();
    }

    public static void processDomain(String txtFile, String domainOut) throws Exception {
        FileWriter fw = new FileWriter(domainOut);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("(");
        List<String> definitions = parsePlan(txtFile);

        for (String def : definitions) {
            if (def.trim().startsWith("define")) {
                bw.write(def + "\n");
                bw.write("\t(:requirements :typing)\n");
                bw.write("\t(:types object)\n");
            } else if (def.trim().startsWith("(:predicates")) {
                bw.write("\t" + addType(def) + "\n");
            } else if (def.trim().startsWith("(:action")) {
                bw.write(transformAction(def));
            } else if (def.trim().startsWith("(:requirements")) {
                // TODO: just ignore for now. Most planners disregard this ...
            } else {
                System.out.println("Error, definition type not found: " + def);
            }
        }
        bw.write(")");
        bw.close();
        fw.close();
    }

    private static String transformAction(String action) {
        String result = "";
        String[] split = action.split("\n");
        for (String part : split) {
            if (part.trim().startsWith(":parameters")) {
                result += "\t" + addType(part) + "\n";
            } else {
                result += part + "\n";
            }
        }
        return result;
    }

    private static String addType(String def) {
        String[] split = def.split(" ");
        String result = split[0].trim();
        for (String part : Arrays.copyOfRange(split, 1, split.length)) {
            int index = part.indexOf("?");
            if (index != -1) {
                int pIndex = part.indexOf(")");
                if (pIndex != -1) {
                    part = new StringBuilder(part).insert(pIndex, " - object").toString();
                } else {
                    part = part + " - object ";
                }
            }
            result = result + " " + part;
        }
        return result.trim();
    }

    @SuppressWarnings("Duplicates")
    private static List<String> parsePlan(String planString) throws Exception {
        List<String> plan = new ArrayList<>();
        planString = planString.trim();
        while (planString.charAt(0) != '(')
            planString = planString.substring(1); // drop chars before first brace
        while (planString.charAt(planString.length()-1) != ')')
            planString = planString.substring(0, planString.length() - 1); // drop chars after last brace

        planString = planString.substring(1, planString.length() - 1);

        int klammerbilanz = 0;
        StringBuilder nextElement = new StringBuilder();

        for (int i = 0; i < planString.length(); i++) {
            nextElement.append(planString.charAt(i));
            if (planString.charAt(i) == '(') {
                klammerbilanz++;
            } else if (planString.charAt(i) == ')') {
                klammerbilanz--;
                if (klammerbilanz == 0) {
                    plan.add(nextElement.toString());
                    nextElement = new StringBuilder();
                }
            }
        }
        return plan;
    }
}
