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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dh on 09.10.16.
 */
public class planRecPerformance {
    public static void main(String[] args) throws Exception {
        String solutionLogCSV = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/greedy-imp-random-1-10/single-tlt.csv";
        String shDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/02-scripts";
        String gtFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/corpus-overview.txt";
        String out = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/greedy-imp-random-1-10/planRecLookAhead.csv";
        whatToDo todo = whatToDo.lookAhead;
        //whatToDo todo = whatToDo.overlap;

        Map<Integer, String[]> groundTruthPlans = new HashMap<>();
        readGroundTruthPlans(shDir, groundTruthPlans);

        Map<Integer, String> groundTruthGoals = new HashMap<>();
        readGroundTruthGoals(gtFile, groundTruthGoals);

        Map<String, String[]> recPlans = new HashMap<>();
        Map<String, String> recGoals = new HashMap<>();
        readPlans(solutionLogCSV, recPlans, recGoals);

        BufferedWriter bw = new BufferedWriter(new FileWriter(out));
        //String s = "\"problemID\"" + " " + "\"prefixlength\"";
        //bw.write(s);
        if (todo == whatToDo.overlap) {
            bw.write("key tlt.gt.no-params tlt.correct length.groundtruth length.recplan length.prefix pref.percent max.plan.length overlap.all overlap.afterprefix overlap.all.pro overlap.afterprefix.pro\n");
        } else {
            bw.write("key prefix lookAhead pref.percent\n");
        }

        for (String key : recPlans.keySet()) {
            String problemIDStr = key.substring(0, 4);
            int problemID = Integer.parseInt(problemIDStr);

            String[] groundTruthPlan = groundTruthPlans.get(problemID);
            String[] recognizedPlan = recPlans.get(key);
            int enforced = 0;
            while ((recognizedPlan.length > enforced)
                    && (recognizedPlan[enforced].startsWith("p-"))) {
                enforced++;
            }

            if (todo == whatToDo.lookAhead)
                lookAheadMetric(bw, key, groundTruthPlan, recognizedPlan, enforced, groundTruthGoals);
            else if (todo == whatToDo.overlap) {
                overlap(groundTruthGoals, recGoals, bw, key, problemID, groundTruthPlan, recognizedPlan, enforced);
            }
        }
        bw.close();
    }

    private static void overlap(Map<Integer, String> groundTruthGoals, Map<String, String> recGoals, BufferedWriter bw, String key, int problemID, String[] groundTruthPlan, String[] recognizedPlan, int enforced) throws IOException {
        int max = groundTruthPlan.length;
        if (recognizedPlan.length > max)
            max = recognizedPlan.length;
        int overlap = enforced;
        outerloop:
        for (int i = enforced; i < groundTruthPlan.length; i++) {
            String gtAction = groundTruthPlan[i];
            innerloop:
            for (int j = enforced; j < recognizedPlan.length; j++) {
                String recAction = recognizedPlan[j];
                if (gtAction.equals(recAction)) {
                    overlap++;
                    break innerloop;
                }
            }
        }
        double v = 100.0 / max * overlap;
        double v1 = 100.0 / (max - enforced) * (overlap - enforced);

        String tltCorrect;
        String recG = recGoals.get(key);
        String gtG = groundTruthGoals.get(problemID);
        if (recG.equals(gtG)) {
            tltCorrect = "TRUE";
        } else
            tltCorrect = "FALSE";
        String tltNoPar = recG.substring(0, recG.indexOf(" "));
        bw.write(key + " "
                + tltNoPar + " "
                + tltCorrect + " "
                + groundTruthPlan.length + " "
                + recognizedPlan.length + " "
                + enforced + " "
                + (1.0 / groundTruthPlan.length * enforced) + " "
                + max + " "
                + overlap + " "
                + (overlap - enforced) + " "
                + v + " "
                + v1 + "\n");
    }

    private static void readGroundTruthGoals(String gtFile, Map<Integer, String> gtGoals) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(gtFile));
        int i = 1;
        while (br.ready()) {
            String line = br.readLine();
            String[] split = line.split(";");
            gtGoals.put(i++, split[0].replaceAll("_", "-"));
        }
        br.close();
    }

    enum whatToDo {lookAhead, overlap}

    private static void lookAheadMetric(BufferedWriter bw, String key, String[] groundTruthPlan, String[] recognizedPlan, int enforced, Map<Integer, String> groundTruthGoals) throws IOException {
        int korrekt = 0;
        for (int i = enforced; i < groundTruthPlan.length + 5; i++) {
            if ((recognizedPlan.length - 1 < i) && (groundTruthPlan.length - 1 < i)) {
                korrekt++;
            } else if ((recognizedPlan.length > i) && !(groundTruthPlan.length > i)) {
                break;
            } else if (!(recognizedPlan.length > i) && (groundTruthPlan.length > i)) {
                break;
            } else if (recognizedPlan[i].equals(groundTruthPlan[i])) {
                korrekt++;
            } else {
                break;
            }
        }

        bw.write(key + " " + enforced + " " + korrekt + " ");
        bw.write(1.0 / groundTruthPlan.length * enforced + "");
        bw.write("\n");
    }

    private static void readPlans(String solutionLogCSV, Map<String, String[]> solMap, Map<String, String> recGoals) throws IOException {
        Map<String, String[]> unsol = new HashMap<>();

        FileReader frSol = new FileReader(solutionLogCSV);
        BufferedReader brSol = new BufferedReader(frSol);

        String line = brSol.readLine();
        line = line.substring(1, line.length() - 1);
        String[] fields = line.split("\";\"");

        int iSol = -1;
        int iSeed = -1;
        int iProb = -1;
        int iSolStatus = -1;
        int iGoal = -1;

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].equals("99 progression:11:solution")) {
                iSol = i;
            } else if (fields[i].equals("expName")) {
                iProb = i;
            } else if (fields[i].equals("randomSeedSh")) {
                iSeed = i;
            } else if (fields[i].equals("99 progression:01:status")) {
                iSolStatus = i;
            } else if (fields[i].equals("99 progression:09:inferredTlt")) {
                iGoal = i;
            }
        }
        if ((iSol == -1) || (iProb == -1) || (iSeed == -1) || (iSolStatus == -1) || (iGoal == -1)) {
            System.out.println("Did not find needed header field -- This should never happen");
            return;
        }
        while (brSol.ready()) {
            line = brSol.readLine();
            line = line.substring(1, line.length() - 1);
            fields = line.split("\";\"");

            recGoals.put(fields[iProb] + "#" + fields[iSeed], clean(fields[iGoal]));

            String planStr = fields[iSol];
            String[] plan = planStr.split("&");
            for (int i = 0; i < plan.length; i++) {
                plan[i] = clean(plan[i]);
            }

            if (fields[iSolStatus].equals("solved"))
                solMap.put(fields[iProb] + "#" + fields[iSeed], plan);
            else
                unsol.put(fields[iProb] + "#" + fields[iSeed], plan);
        }
        brSol.close();
        return;
    }

    private static String clean(String s) {
        s = s.replaceAll("\\(\\)", "");
        s = s.replaceAll("\\[", " ");
        s = s.replaceAll("\\]", " ");
        s = s.replaceAll(",", " ");
        s = s.replaceAll("_", "-");
        return s.trim();
    }

    private static void readGroundTruthPlans(String shDir, Map<Integer, String[]> gtPlans) throws IOException {
        File frSh = new File(shDir);
        for (File f : frSh.listFiles()) {
            String name = f.getName().substring("makePrefix-".length(), f.getName().length() - 3);
            int num = Integer.parseInt(name);
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            while (!line.contains(" -verify ")) {
                line = br.readLine();
            }
            line = line.substring(line.indexOf(" -verify ") + " -verify ".length() + 2, line.length() - 2);
            String[] plan = line.split("\\)\\(");
            gtPlans.put(num, plan);
            br.close();
        }
    }
}
