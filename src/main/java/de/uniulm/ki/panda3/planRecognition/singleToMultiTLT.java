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

package de.uniulm.ki.panda3.planRecognition;

import java.io.*;
import java.util.*;

/**
 * Created by dh on 29.09.16.
 */
public class singleToMultiTLT {
    public static void main(String[] args) throws Exception {
        String problemDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/01-problems";
        String outDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/07-multigoal";

        //Random ran = new Random(Integer.parseInt(args[0]));
        Random ran = new Random(57);

        int num = ran.nextInt(3) + 1;
        int[] indices = new int[num];
        List<List<String>> plans = new ArrayList<>();
        List<Set<String>> constSets = new ArrayList<>();
        findCompatible(ran, indices, plans, constSets);
        String fileID = getFileID(indices);

        writeScript(outDir, ran, num, indices, plans, fileID);

        BufferedWriter bw = new BufferedWriter(new FileWriter(outDir + "/p-m" + fileID + ".lisp"));
        List<String> tlts = new ArrayList<>();
        List<String>[] locsOfPlan = new List[num];
        List<String> restOfFile = new ArrayList<>();

        File probFiles = new File(problemDir);
        for (int j = 0; j < indices.length; j++) {
            boolean first = (j == 0);
            File problemFiles = getFile(probFiles, indices[j]);
            BufferedReader problemReader = new BufferedReader(new FileReader(problemFiles));
            String line = "";
            // copy until problem-depended part
            while (problemReader.ready() && !line.trim().startsWith(";; problem-dependent part of the state")) {
                line = problemReader.readLine();
                if (first) // need to write once
                    bw.write(line + "\n");
            }
            // copy problem-depended part
            line = problemReader.readLine();
            while (problemReader.ready() && !line.trim().startsWith(")")) {
                if ((first) || (!line.trim().startsWith(";")))
                    bw.write(line + "\n");
                line = problemReader.readLine();
            }
            // find tlt
            while (problemReader.ready() && !line.trim().startsWith("(:htn")) {
                line = problemReader.readLine();
            }
            tlts.add(line);

            while (problemReader.ready() && !line.trim().startsWith("(:init")) {
                line = problemReader.readLine();
            }

            locsOfPlan[j] = new ArrayList<>();
            while (problemReader.ready() && !line.trim().startsWith(";; non-problem-dependent part of the state")) {
                line = problemReader.readLine();
                locsOfPlan[j].add(line);
            }
            if (first) {
                restOfFile.add("   ;;\n");
                restOfFile.add(line + "\n");
                while (problemReader.ready()) {
                    line = problemReader.readLine();
                    restOfFile.add(line + "\n");
                }
            }
        }

        // write combined initial task
        bw.write(" )\n\n" +
                ";; (:htn :tasks (mtlt)) ;; generic tlt\n" +
                " (:htn :tasks (and\n");
        for (String tlt : tlts) {
            bw.write("   ");
            bw.write(tlt.substring(" (:htn :tasks ".length(), tlt.lastIndexOf(")")));
            bw.write("\n");
        }
        bw.write(" ))\n\n (:init\n");

        // extract problem-dependend locations
        Map<String, String>[] locationMaps = new Map[num];
        Set<String> allKeySet = new HashSet<>();
        for (int j = 0; j < num; j++) {
            locationMaps[j] = new HashMap<>();
            for (String line : locsOfPlan[j]) {
                String line2 = line.trim();
                if (!line2.startsWith("(atloc")) { // write NON-locations
                    if (!line2.startsWith(";"))
                        bw.write(line + "\n");
                } else {
                    line2 = line2.substring("(atloc ".length(), line2.length() - 1);
                    String[] split = line2.split(" ");
                    if (!(split.length == 2)) {
                        System.out.println("Split has not 2 elements " + line2);
                    } else {
                        locationMaps[j].put(split[0], split[1]);
                        allKeySet.add(split[0]);
                    }
                }
            }
        }

        // write adapted locations
        for (String key : allKeySet) {
            List<String> values = new ArrayList<>();
            for (int j = 0; j < num; j++) {
                if (locationMaps[j].containsKey(key)) {
                    values.add(locationMaps[j].get(key));
                }
            }
            if (values.size() == 1) { // there is a single occurance
                writeLoc(bw, key, values.get(0));
            } else { // more than one! which one to write?
                int numPlans = 0;
                int whichOne = -1;

                for (int j = 0; j < num; j++) {
                    if (constSets.get(j).contains(key)) {
                        numPlans++;
                        whichOne = j;
                    }
                }
                if (numPlans == 0) {
                    writeLoc(bw, key, values.get(0));
                } else if (numPlans == 1) {
                    writeLoc(bw, key, locationMaps[whichOne].get(key));
                } else {
                    System.out.println("Found constant in more than one plan - this should not happen");
                }
            }
        }

        for (String s : restOfFile) {
            bw.write(s);
        }
        bw.close();

    }

    private static void writeLoc(BufferedWriter bw, String key, String value) throws IOException {
        bw.write("   (atloc ");
        bw.write(key);
        bw.write(" ");
        bw.write(value);
        bw.write(")\n");
    }

    private static String getFileID(int[] indices) {
        boolean first = true;
        String fileID = "";
        System.out.print("Combining problems ");
        for (int j : indices) {
            if (first) {
                first = false;
            } else {
                System.out.print(", ");
                fileID += "-";
            }
            fileID += j;
            System.out.print(j);
        }
        System.out.println();
        return fileID;
    }

    private static void writeScript(String outDir, Random ran, int num, int[] indices, List<List<String>> plans, String fileID) throws IOException {
        String problemFileName = "p-m" + fileID;
        FileWriter fw = new FileWriter(outDir + "/makePrefix-m" + fileID + ".sh");
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < num; i++) {
            String s = "# " + indices[i] + ": " + formatPlan(plans.get(i)) + "\n";
            bw.write(s);
        }
        String overallPlan = "";
        int k = 0;
        boolean allEmpty = false;
        while (!allEmpty) {
            int rest = 0;
            for (List<String> plan : plans) {
                rest += plan.size();
            }

            String pLength = "-";
            if (k == 0) {
                pLength += "no-pref";
            } else if (rest == 1) {
                pLength += "full-pref";
            } else {
                pLength += k;
            }

            if (k > 0) {
                String nextStep = "";
                while (nextStep.length() == 0) {
                    int n = ran.nextInt(plans.size());
                    if (plans.get(n).size() > 0)
                        nextStep = plans.get(n).remove(0);
                }
                overallPlan += "(" + nextStep + ")";
            }
            k++;

            String cmd = " -prefix ";
            bw.write("echo problem-m" + fileID + pLength + "\n");
            bw.write("java -jar PANDAaddPrefix.jar -domain domain.lisp d-m" + fileID + pLength + ".hddl");
            bw.write(" -problem " + problemFileName + ".lisp " + problemFileName + pLength + ".hddl" + cmd);
            bw.write("\"");
            bw.write(overallPlan);
            bw.write("\"");
            bw.write("\n");

            if (rest == 1) {
                allEmpty = true;
                cmd = " -verify ";
                pLength = "-verify";
                bw.write("echo problem-m" + fileID + pLength + "\n");
                bw.write("java -jar PANDAaddPrefix.jar -domain domain.lisp d-m" + fileID + pLength + ".hddl");
                bw.write(" -problem " + problemFileName + ".lisp " + problemFileName + pLength + ".hddl" + cmd);
                bw.write("\"");
                bw.write(overallPlan);
                bw.write("\"");
                bw.write("\n");
            }
        }
        bw.close();
        fw.close();
    }

    private static boolean isNumeric(String n) {
        for (int i = 0; i < n.length(); i++) {
            if (!((n.charAt(i) == '0')
                    || (n.charAt(i) == '1')
                    || (n.charAt(i) == '2')
                    || (n.charAt(i) == '3')
                    || (n.charAt(i) == '4')
                    || (n.charAt(i) == '5')
                    || (n.charAt(i) == '6')
                    || (n.charAt(i) == '7')
                    || (n.charAt(i) == '8')
                    || (n.charAt(i) == '9')))
                return false;
        }
        return true;
    }

    private static File getFile(File probF, int index) {
        for (File f : probF.listFiles()) {
            int nr = -1;
            String name = f.getName().substring(2, 2 + 4);
            if (!isNumeric(name)) {
                name = f.getName().substring("makePrefix-".length(), "makePrefix-".length() + 4);
            }
            nr = Integer.parseInt(name);
            if (nr == index)
                return f;
        }
        System.out.println("Did not find experiment file nr " + index);
        return null;
    }

    private static String formatPlan(List<String> plan) {
        StringBuilder sb = new StringBuilder();
        for (String action : plan) {
            sb.append("(");
            sb.append(action);
            sb.append(")");
        }
        return sb.toString();
    }

    private static void findCompatible(Random ran, int[] indices, List<List<String>> plans, List<Set<String>> constSets) throws Exception {
        String[] locs = new String[]{"texaco1", "strong", "park-ridge", "rochester-general", "marketplace",
                "airport", "brighton-high", "mendon-pond", "twelve-corners", "pittsford-plaza",
                "brighton-dump", "henrietta-dump"};
        Set<String> locset = new HashSet<>();
        String striptDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/02-scripts";
        File f = new File(striptDir);

        for (String l : locs)
            locset.add(l);

        int i = 0;
        findCompatible:
        while (plans.size() < indices.length) {
            int index = ran.nextInt(100);
            File scriptFile = getFile(f, index);
            String[] plan = getPlan(scriptFile);
            Set<String> constSet = getConstSet(plan);
            for (int j = i - 1; j >= 0; j--) {
                Set<String> newSet = new HashSet<>();
                newSet.addAll(constSets.get(j));
                newSet.retainAll(constSet);
                newSet.removeAll(locset);

                if (!newSet.isEmpty()) {
                    continue findCompatible;
                }
            }
            indices[i] = index;
            List<String> planList = new LinkedList<>();
            for (String s : plan) {
                planList.add(s);
            }
            plans.add(planList);
            constSets.add(constSet);
            i++;
        }
    }

    private static String[] getPlan(File file) throws Exception {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        while (br.ready() && (!line.contains(" -verify "))) {
            line = br.readLine();
        }
        assert (line.length() > 0);
        String plan = line.substring(line.indexOf("\"") + 1, line.length() - 1);
        String[] actions = plan.substring(1, plan.length() - 1).split("\\)\\(");
        return actions;
    }

    private static Set<String> getConstSet(String[] actions) throws Exception {
        Set<String> consts = new HashSet<>();
        for (String action : actions) {
            String[] elems = action.split(" ");
            for (int i = 1; i < elems.length; i++) {
                consts.add(elems[i]);
            }
        }
        return consts;
    }
}
