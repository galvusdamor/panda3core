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

    public static void main(String[] args) throws Exception {

        String inFile = "/home/mick/Projects/panda3/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC1/gripper/domain/domain.pddl";
        //String inFile = "/home/mick/Projects/panda3/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/Depots/domain/Depots.pddl";
        String outFile = "/home/mick/Projects/panda3/domain.pddl";
        String pInfile = "/home/mick/Projects/panda3/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC1/gripper/problems/prob08.pddl";
        //String pInfile = "/home/mick/Projects/panda3/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/Depots/problems/pfile3";
        String pOutFile = "/home/mick/Projects/panda3/problem.pddl";
        type(inFile, pInfile, outFile, pOutFile);
    }

    public static void type(String domainInFile, String problemInFile, String domainOutputFile, String problemOutputFile) throws Exception {
        String domain = readFile(domainInFile);
        String problem = readFile(problemInFile);
        if (!domain.contains("(:types ")) {
            processDomain(domain, domainOutputFile);
            processProblem(problem, problemOutputFile);
        } else {
            System.out.println("Copying files....");
            Files.copy(Paths.get(domainInFile), Paths.get(domainOutputFile));
            Files.copy(Paths.get(problemInFile), Paths.get(problemOutputFile));
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
                bw.write("\t(:types object - object)\n");
            } else if (def.trim().startsWith("(:predicates")) {
                bw.write("\t" + addType(def) + "\n");
            } else if (def.trim().startsWith("(:action")) {
                bw.write(transformAction(def));
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

    @SuppressWarnings("Duplicates")
    private static String readFile(String filename) throws Exception {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        while (br.ready()) {
            sb.append(br.readLine() + "\n");
        }
        br.close();
        fr.close();
        return sb.toString();
    }
}
