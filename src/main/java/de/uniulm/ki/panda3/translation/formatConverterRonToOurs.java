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

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dhoeller on 10.08.16.
 */
public class formatConverterRonToOurs {

    public static void main(String[] strs) throws Exception {
        File f = new File("/home/gregor/Workspace/Panda3/panda3core/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hddl");
        assert(f.isDirectory());
        for (File pd : f.listFiles())
            for (File ed : pd.listFiles())
                for (File pf : ed.listFiles()) {
                    if (pf.getAbsolutePath().endsWith("hddl")) continue;
                    String oldName = pf.getAbsolutePath();
                    String newName = oldName.replaceFirst("\\.pddl",".hddl").replaceFirst("\\.hpddl",".hddl");
                    System.out.println(newName);
                    System.out.println(oldName);
                    main2(new String[]{oldName,newName});
                }
    }



    public static void main2(String[] strs) throws Exception {
        if (false) {
            System.out.println("This program translates HTN domain and problem representations from the format used by " +
                    "Ron Alford to the format used by PANDA (starting with version 3).");
            System.out.println("Usage: <program> inFileName outFileName");
            System.out.println("The program will detect automatically whether it is a domain or problem file.");
        }
        //String inFile = "/home/dh/Schreibtisch/test-ron/domain-block.hpddl";
        //String outFile = "/home/dh/Schreibtisch/test-ron/domain-block.hpddl-2";

        //String inFile = "/home/dh/Schreibtisch/test-ron/pfile_005.pddl";
        //String outFile = "/home/dh/Schreibtisch/test-ron/pfile_005.pddl-2";

        //String inFile = "C:\\Projekte\\panda3\\src\\test\\resources\\de\\uniulm\\ki\\panda3\\symbolic\\parser\\hddl\\towers\\domain\\domain.hpddl";
        //String outFile = "C:\\Projekte\\panda3\\src\\test\\resources\\de\\uniulm\\ki\\panda3\\symbolic\\parser\\hddl\\towers\\domain\\domain.hpddl-2";

        String inFile = strs[0];
        String outFile = strs[1];

        String txtFile = readFile(inFile);

        if (txtFile.contains(":domain")) { // yes, then it's a PROBLEM ;-)
            processProblem(outFile, txtFile, new HashMap<>());
        } else {
            processDomain(outFile, txtFile);
        }
    }

    public static void processProblem(String problemOut, String txtFile, Map<String,String> taskReplacement) throws Exception {
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
        bw.write("  :ordered-tasks (and\n");

        for (String def : tasks) {
            bw.write(transformInitialTN(def, taskReplacement) + "\n");
        }
        bw.write("  )\n");
        bw.write("  :ordering ( )\n");
        bw.write("  :constraints ( )\n");
        bw.write(" )\n");

        bw.write(transformGoalToInit(init, goal) + "\n");
        bw.write(goal + "\n");
        bw.write(")");
        bw.close();
        fw.close();
    }

    private static String transformGoalToInit(String init, String goal) {
        String res;
        String init2 = init.trim();
        res = init2.substring(0, init2.length() - 1);
        if (!goal.equals("")) {
            String goal2 = goal.trim();
            if ((!goal2.startsWith("(:goal (and")) || (!goal2.endsWith("))"))) {
                System.out.println("ERROR: Could not transform goal");
            }
            goal2 = goal2.substring("(:goal (and".length() + 1, goal2.length() - 2).trim();
            goal2 = goal2.replaceAll("\\(", "(goal_");
            res = res + "\n" + goal2;
        }
        return res + ")";
    }

    static String initTaskRegEx = "\\(\\:tasks[\\s]+(\\([a-zA-Z0-9]+[\\s]*\\([^\\)]+\\)\\))\\)";
    static Pattern pInitTN = Pattern.compile(initTaskRegEx);

    private static String transformInitialTN(String def, Map<String,String> taskReplacement) {
        StringBuilder res = new StringBuilder();
        Matcher mTasks = pInitTN.matcher(def);

        int offset = 0;
        while (mTasks.find(offset)) { // should be one, but anyway...
            res.append("    ");
            String taskLine = mTasks.group(1);
            // execute all task replacements
            for (Map.Entry<String,String> entry : taskReplacement.entrySet())
                taskLine = taskLine.replaceAll(entry.getKey() + " ", entry.getValue() + " ");
            res.append(taskLine);
            offset = mTasks.end();
        }
        return res.toString();
    }

    public static Map<String, String> processDomain(String domainOut, String txtFile) throws Exception {
        FileWriter fw = new FileWriter(domainOut);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("(");
        List<String> definitions = parsePlan(txtFile);
        List<String> methods = new ArrayList<>();
        List<String> newMethods = new ArrayList<>();
        List<String> actions = new ArrayList<>();

        for (String def : definitions)
            if (def.trim().startsWith("(:action"))
                actions.add(def);

        Map<String, String> taskReplacementMap = new HashMap<>();

        // add new methods
        for (String def : actions) {
            newMethods.add(addNewMethods(def, taskReplacementMap));
        }

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
                bw.write(transformTasks(def, taskReplacementMap) + "\n");
            } else if (def.trim().startsWith("(:method")) {
                methods.add(def);
            } else if (def.trim().startsWith("(:action")) {
                // we already did this
            } else {
                System.out.println("Error, definition type not found: " + def);
            }
        }


        // write rest of domain
        for (String def : methods) {
            bw.write(transformMethods(def, taskReplacementMap) + "\n");
        }
        // write the newly created methods without doing anything
        for (String m : newMethods)
            bw.write(m);
        for (String def : actions) {
            bw.write(transformActions(def) + "\n");
        }
        bw.write(")");
        bw.close();
        fw.close();

        return taskReplacementMap;
    }

    static int newMethodID = 1;
    static String acNameRegEx = "\\(\\:action ([a-zA-Z0-9_-]*)";
    static String parameterRegEx = "\\:parameters \\(([^)]*)\\)";
    static String meNameRegEx = "\\:task \\(([^)]*)\\)";
    static Pattern pActionName = Pattern.compile(acNameRegEx);
    static Pattern pTaskName = Pattern.compile(meNameRegEx);
    static Pattern pParameter = Pattern.compile(parameterRegEx);

    private static String addNewMethods(String def, Map<String, String> taskReplacementMap) {
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
        String task = mTask.group(1);
        String taskName = task.split(" ")[0];
        String replacementTaskName = taskName + "_abstract";
        String parameters = task.substring(taskName.length());
        Matcher mParameter = pParameter.matcher(def);
        if (!mParameter.find()) {
            System.out.println("Error: could not find parameters!");
            return null;
        }
        String aParameters = mParameter.group().substring(":parameters ".length());

        taskReplacementMap.put(taskName, replacementTaskName);

        // delete typing for method's subtasks
        String typed = aParameters.substring(1, aParameters.length() - 1);
        String[] split = typed.split(" ");
        String typeless = "";
        for (int i = 0; i < split.length; i ++) {
            if (!split[i].startsWith("?")) continue;
            typeless += split[i] + " ";
        }
        typeless = typeless.trim();

        return "\n (:method newMethod" + (newMethodID++) + "\n" +
                "  :parameters " + aParameters + "\n" +
                "  :task (" + replacementTaskName + " " + parameters + ")\n" +
                "  :ordered-subtasks (" + actionName + " " + typeless + "))\n";
    }

    static String delTaskLineRegEx = "[\\s]*:task \\([^)]*\\)";

    private static String transformActions(String def) {
        return def.replaceAll(delTaskLineRegEx, "");
    }

    private static String transformMethods(String def, Map<String, String> taskReplacementMap) {
        int paramPos = def.indexOf(":parameters");
        int taskPos = def.indexOf(":task");
        if (paramPos != -1) {
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
        } else {
            def = def.substring(0, taskPos) + "    :parameters ()\n" + def.substring(taskPos);
        }
        List<String> taskList = new ArrayList<>();
        if (def.indexOf("\n", taskPos) != -1)
        taskList.add(def.substring(taskPos, def.indexOf("\n", taskPos)));
        else taskList.add(def.substring(taskPos, def.lastIndexOf(")")));
        int tasksStart = def.indexOf(":tasks");
        int tasksEnd = def.lastIndexOf(")");
        if (tasksStart != -1) {
            String tasks = def.substring(tasksStart, tasksEnd);
            String taskRegrex = "\\(([^)]*)\\)";
            Pattern taskPattern = Pattern.compile(taskRegrex);
            String s = tasks.substring(tasks.indexOf("(") + 1, tasks.lastIndexOf(")"));
            Matcher tMatcher = taskPattern.matcher(s);
            while (tMatcher.find()) {
                taskList.add(tMatcher.group());
            }
        }
        for (String actionName : taskReplacementMap.keySet()) {
            String replacement = taskReplacementMap.get(actionName);
            def = def.replaceAll(actionName + " ", replacement + " ");
        }

        return def.replaceAll("\\:tasks \\(\\(", ":ordered-tasks (and (");
    }

    static String taskDefRegEx = "\\(([a-zA-Z0-9_-]+)( [^)]+)?";
    static Pattern pTask = Pattern.compile(taskDefRegEx);

    private static String transformTasks(String def, Map<String, String> taskReplacementMap) {
        StringBuilder res = new StringBuilder();
        Matcher mTasks = pTask.matcher(def);
        int offset = 0;
        while (mTasks.find(offset)) {
            String tName = mTasks.group(1);
            if (taskReplacementMap.containsKey(tName)) {
                tName = taskReplacementMap.get(tName);
            }
            res.append("  (:task ");
            res.append(tName);
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
