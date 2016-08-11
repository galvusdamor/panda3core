package de.uniulm.ki.panda3.translation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dhoeller on 10.08.16.
 */
public class formatConverterRonToOurs {

    public static void main(String[] strs) throws Exception {
        if (false) {
            System.out.println("This program translates HTN domain and problem representations from the format used by " +
                    "Ron Alford to the format used by PANDA (starting with version 3).");
            System.out.println("Usage: <program> inFileName outFileName");
            System.out.println("The program will detect automatically whether it is a domain or problem file.");
        }
        //String inFile = "/home/dhoeller/Schreibtisch/rons-domains/blocksworld/domain/domain.hpddl";
        //String outFile = "/home/dhoeller/Schreibtisch/rons-domains/blocksworld/domain/domain.hpddl-2";

        String inFile = "/home/dhoeller/Schreibtisch/rons-domains/multiarm-blocksworld/problems/pfile_10_100.pddl";
        String outFile = "/home/dhoeller/Schreibtisch/rons-domains/multiarm-blocksworld/problems/pfile_10_100.pddl-2";

        String txtFile = readFile(inFile);

        if (txtFile.contains(":domain")) { // yes, then it's a PROBLEM ;-)
            processProblem(outFile, txtFile);
        } else {
            processDomain(outFile, txtFile);
        }
    }

    private static void processProblem(String problemOut, String txtFile) throws Exception {
        FileWriter fw = new FileWriter(problemOut);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("(");

        List<String> definitions = parsePlan(txtFile);
        String init = "";
        String goal = "";
        List<String> tasks = new ArrayList<>();
        for (String def : definitions) {
            if (def.trim().startsWith("define")) {
                bw.write(def + "\n");
            } else if (def.trim().startsWith("(:domain")) {
                bw.write(def + "\n");
            } else if (def.trim().startsWith("(:objects")) {
                bw.write(def + "\n");
            } else if (def.trim().startsWith("(:init")) {
                init = def;
            } else if (def.trim().startsWith("(:tasks")) {
                tasks.add(def);
            } else if (def.trim().startsWith("(:goal")) {
                goal = def;
            } else {
                System.out.println("Error, definition type not found: " + def);
            }
        }
        bw.write(" (:htn\n");
        bw.write("  :tasks (and\n");

        for (String def : tasks) {
            bw.write(transformInitialTN(def) + "\n");
        }
        bw.write("  )\n");
        bw.write("  :ordering ( )\n");
        bw.write("  :constraints ( )\n");
        bw.write(" )\n");

        bw.write(init + "\n");
        bw.write(goal + "\n");
        bw.write(")");
        bw.close();
        fw.close();
    }

    static String initTaskRegEx = "\\(\\:tasks[\\s]+(\\([a-zA-Z0-9]+[\\s]*\\([^\\)]+\\)\\))\\)";
    static Pattern pInitTN = Pattern.compile(initTaskRegEx);

    private static String transformInitialTN(String def) {
        StringBuilder res = new StringBuilder();
        Matcher mTasks = pInitTN.matcher(def);

        int offset = 0;
        while (mTasks.find(offset)) { // should be one, but anyway...
            res.append("    ");
            res.append(mTasks.group(1));
            offset = mTasks.end();
        }
        return res.toString();
    }

    private static void processDomain(String domainOut, String txtFile) throws Exception {
        FileWriter fw = new FileWriter(domainOut);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("(");
        List<String> definitions = parsePlan(txtFile);
        List<String> methods = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        for (String def : definitions) {
            if (def.trim().startsWith("define")) {
                bw.write(def + "\n");
            } else if (def.trim().startsWith("(:requirements")) {
                bw.write(def + "\n");
            } else if (def.trim().startsWith("(:types")) {
                bw.write(def + "\n");
            } else if (def.trim().startsWith("(:predicates")) {
                bw.write(def + "\n");
            } else if (def.trim().startsWith("(:tasks")) {
                bw.write(transformTasks(def) + "\n");
            } else if (def.trim().startsWith("(:method")) {
                methods.add(def);
            } else if (def.trim().startsWith("(:action")) {
                actions.add(def);
            } else {
                System.out.println("Error, definition type not found: " + def);
            }
        }

        // add new methods
        for (String def : actions) {
            methods.add(addNewMethods(def));
        }

        // write rest of domain
        for (String def : methods) {
            bw.write(transformMethods(def) + "\n");
        }
        for (String def : actions) {
            bw.write(transformActions(def) + "\n");
        }
        bw.write(")");
        bw.close();
        fw.close();
    }

    static int newMethodID = 1;
    static String acNameRegEx = "\\(\\:action ([a-zA-Z0-9_-]*)";
    static String meNameRegEx = "\\:task \\(([^)]*)\\)";
    static Pattern pActionName = Pattern.compile(acNameRegEx);
    static Pattern pTaskName = Pattern.compile(meNameRegEx);

    private static String addNewMethods(String def) {
        Matcher mAction = pActionName.matcher(def);
        if (!mAction.find()) {
            System.out.println("Error: could not find action name");
            return null;
        }
        String actionName = mAction.group(1);

        Matcher mTask = pTaskName.matcher(def);
        if (!mTask.find()) {
            System.out.println("Error: could not find task name");
            return null;
        }
        String taskName = mTask.group(1);
        return "(:method newMethod" + (newMethodID++) + "\n" +
                "    :task (" + taskName + ")\n" +
                "    :tasks ((" + actionName + ")))";
    }

    static String delTaskLineRegEx = "[\\s]*:task \\([^)]*\\)";

    private static String transformActions(String def) {
        return def.replaceAll(delTaskLineRegEx, "");
    }

    private static String transformMethods(String def) {
        int paramPos = def.indexOf(":parameters");
        int taskPos = def.indexOf(":task");
        if (taskPos < paramPos) {
            int endLineParam = def.indexOf("\n", paramPos);
            int endLineTask = def.indexOf("\n", taskPos);

            String parameters = def.substring(paramPos, endLineParam);
            String task = def.substring(taskPos, endLineTask);

            String newDef = def.substring(0, taskPos);
            newDef += parameters;
            newDef += def.substring(endLineTask, paramPos);
            newDef += task;
            newDef += def.substring(endLineParam);
            assert (newDef.length() == def.length());
            def = newDef;
        }
        return def.replaceAll("\\:tasks \\(\\(", ":tasks (and (");
    }

    static String taskDefRegEx = "\\(([a-zA-Z0-9_-]+)( [^)]+)?";
    static Pattern pTask = Pattern.compile(taskDefRegEx);

    private static String transformTasks(String def) {
        StringBuilder res = new StringBuilder();
        Matcher mTasks = pTask.matcher(def);
        int offset = 0;
        while (mTasks.find(offset)) {
            res.append("  (:task ");
            res.append(mTasks.group(1));
            res.append(" :parameters (");
            String paramStr = mTasks.group(2);
            if (paramStr != null) {
                res.append(paramStr.trim()); // parameters
            }
            res.append("))\n");
            offset = mTasks.end();
        }
        return res.toString();
    }

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
}