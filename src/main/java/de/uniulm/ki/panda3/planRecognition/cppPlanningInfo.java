package de.uniulm.ki.panda3.planRecognition;

import java.io.*;
import java.util.*;

/**
 * Created by dh on 06.11.17.
 */
public class cppPlanningInfo {
    enum domain {monroeFO1, monroeFO2, monroeFO3, monroeFO4, monroeFO5, monroePO1, monroePO2, monroePO3, monroePO4, kitchenFO, kitchenPO}

    public static final String cSolved = "solved";
    public static final String cTimeout = "timeout";
    public static final String cGtPercentage = "pref.percent";
    static boolean calcParamLessTasks = true;

    public static void main(String str[]) throws Exception {
        String folder = null;
        String logFile = null;
        String gtFile = null;
        String outFile = null;
        String replaceBy = null;
        String shDir = null;

        domain d = domain.monroePO3;
        switch (d) {
            case kitchenFO: {
                folder = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/kitchen-full-obs/greedy/";
                logFile = folder + "runExpKitchen.txt";
                gtFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/kitchen/groundtruth2.txt";
                outFile = folder + "runExpKitchen.csv";
                shDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/plans-kit/";
                calcParamLessTasks = false;
                break;
            }
            case kitchenPO: {
                folder = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/kitchen-part-obs/greedy/";
                logFile = folder + "runKitchenPartObs.txt";
                gtFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/kitchen/groundtruth2.txt";
                outFile = folder + "runKitchenPartObs.csv";
                shDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/plans-kit-po/";
                //folder = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/kitchen-full-obs/gas/";
                //logFile = folder + "k100GAS.txt";
                //gtFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/kitchen/groundtruth2.txt";
                //outFile = folder + "k100GAS.csv";
                calcParamLessTasks = false;
                break;
            }
            case monroeFO1: {
                folder = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-full-obs/";
                logFile = folder + "run/runMonroe.txt";
                gtFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/corpus-overview.txt";
                outFile = folder + "run1Monroe.csv";
                replaceBy = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-full-obs/rerun1/rerun1.txt";
                shDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/plans-monroe/";
                break;
            }
            case monroeFO2: {
                folder = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-full-obs/";
                logFile = folder + "run/runMonroe.txt";
                gtFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/corpus-overview.txt";
                outFile = folder + "run2Monroe.csv";
                replaceBy = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-full-obs/rerun2/rerun2.txt";
                shDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/plans-monroe/";
                break;
            }
            case monroeFO3: {
                folder = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-full-obs/";
                logFile = folder + "run/runMonroe.txt";
                gtFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/corpus-overview.txt";
                outFile = folder + "run3Monroe.csv";
                replaceBy = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-full-obs/rerun3/rerun3.txt";
                shDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/plans-monroe/";
                break;
            }
            case monroeFO4: {
                folder = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-full-obs/";
                logFile = folder + "run/runMonroe.txt";
                gtFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/corpus-overview.txt";
                outFile = folder + "run4Monroe.csv";
                replaceBy = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-full-obs/rerun4/rerun4.txt";
                shDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/plans-monroe/";
                break;
            }
            case monroePO1: {
                folder = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-part-obs/";
                logFile = folder + "run/runMonroePO.txt";
                gtFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/corpus-overview.txt";
                outFile = folder + "run1MonroePO.csv";
                shDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/plans-monroe/";
                break;
            }
            case monroePO2: {
                folder = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-part-obs/";
                logFile = folder + "run/runMonroePO.txt";
                gtFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/corpus-overview.txt";
                outFile = folder + "run2MonroePO.csv";
                replaceBy = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-part-obs/rerun1frodo/rerun1Frodo.txt";
                shDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/plans-monroe/";
                break;
            }
            case monroePO3: {
                folder = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-part-obs/";
                logFile = folder + "run/runMonroePO.txt";
                gtFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/corpus-overview.txt";
                outFile = folder + "run3MonroePO.csv";
                replaceBy = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-part-obs/rerun2/rerun2.txt";
                shDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/plans-monroe/";
                break;
            }
            case monroePO4: {
                folder = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-part-obs/";
                logFile = folder + "run/runMonroePO.txt";
                gtFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/corpus-overview.txt";
                outFile = folder + "run4MonroePO.csv";
                replaceBy = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/monroe-part-obs/rerun3/rerun3.txt";
                shDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/ICAPS18-exps/log-data/ground-truth/plans-monroe/";
                break;
            }
        }

        HashMap<String, HashMap<String, Integer>> recognizedGoals = new HashMap<>();
        HashMap<String, List<String>> recognizedPlans = new HashMap<>();

        List<HashMap<String, String>> rows = readLogFile(logFile, recognizedGoals, recognizedPlans);
        if (replaceBy != null) {
            replaceSearchData(replaceBy, rows, recognizedGoals, recognizedPlans);
        }

        HashMap<Integer, HashMap<String, Integer>> gtGoals = new HashMap<>();
        HashMap<Integer, List<String>> gtPlans = new HashMap<>();
        HashMap<Integer, Integer> gtPlanLength = new HashMap<>();

        readGroundTruth(gtFile, gtGoals, gtPlans, gtPlanLength);
        for (HashMap<String, String> row : rows) {
            if (!row.get("solutionStatus").equals(cSolved))
                continue;
            String key = row.get("problemFile");
            String goal = recognizedGoals.get(key).keySet().iterator().next();
            if (recognizedGoals.get(key).keySet().size() > 1)
                continue;
            goal = goal.substring(0, goal.indexOf(" "));
            row.put("infered.toplevel.task.only", goal);

        }

        addGTLengthToRows(rows, gtPlanLength);
        addGoalKeyVals(rows, recognizedGoals, gtGoals);
        Map<Integer, String[]> gtPlans2 = new HashMap<>();
        readGroundTruthPlans(shDir, gtPlans2);

        addPlanKeyVals(rows, recognizedPlans, gtPlans2);

        writeCSV(outFile, rows);
    }

    private static void addPlanKeyVals(List<HashMap<String, String>> rows, HashMap<String, List<String>> recognizedPlan, Map<Integer, String[]> gtPlans) {
        for (HashMap<String, String> row : rows) {
            if (!row.get("solutionStatus").equals("solved"))
                continue;
            int id = Integer.parseInt(row.get("probID"));
            String problemFile = row.get("problemFile");
            if (problemFile.contains("p-0079-kitchen-26.hdd"))
                System.out.println();
            List<String> rec = recognizedPlan.get(problemFile);
            String[] gt = gtPlans.get(id);
            //int enforced = Integer.parseInt(row.get("prefLength"));
            int enforced = 0;
            for(String ac : rec){
                if(ac.startsWith("p_"))
                    enforced++;
            }
            int lookAhead = 0;
            boolean lookOK = true;
            int overlap = 0;
            for (int i = 0; i < rec.size(); i++) {
                String action = rec.get(i).replaceAll("\\[", " ").replaceAll("\\]", " ").replaceAll("\\,", " ").trim();
                for (int j = 0; j < gt.length; j++) {
                    String s = gt[j].replaceAll("\\-", "\\_");
                    if (action.equals(s)) {
                        overlap++;
                        break;
                    }
                }
                if (gt.length > i && lookOK) {
                    String s = gt[i].replaceAll("\\-", "\\_");
                    if (action.equals(s))
                        lookAhead++;
                    else lookOK = false;
                }
            }
            row.put("numAcAfterPrefGT", Integer.toString(gt.length - enforced));
            row.put("numAcAfterPrefREC", Integer.toString(rec.size() - enforced));
            row.put("overlapAfterPrefAbs", Integer.toString(overlap));
            row.put("looAheadAbs", Integer.toString(lookAhead));
        }
    }

    private static void readGroundTruthPlans(String shDir, Map<Integer, String[]> gtPlans) throws IOException {
        File frSh = new File(shDir);
        for (File f : frSh.listFiles()) {
            String name;
            if (f.getName().contains("makePrefix"))
                name = f.getName().substring("makePrefix-".length(), f.getName().length() - 3);
            else
                name = f.getName().substring("p-".length(), 6);
            int num = Integer.parseInt(name);
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            //while (!line.contains(" -verify ")) {
            while (br.ready()) {
                line = br.readLine();
            }
            //String line2 = line.substring(line.indexOf(" -verify ") + " -verify ".length() + 2, line.length() - 2);
            line = line.substring(line.indexOf("\"") + 2, line.length() - 2);
            String[] plan = line.split("\\)\\(");
            gtPlans.put(num, plan);
            br.close();
        }
    }

    private static void replaceSearchData(String replaceBy, List<HashMap<String, String>> rows, HashMap<String, HashMap<String, Integer>> recognizedGoals, HashMap<String, List<String>> recognizedPlan) throws IOException {
        recognizedGoals.clear();
        recognizedPlan.clear();
        List<HashMap<String, String>> replacement = readLogFile(replaceBy, recognizedGoals, recognizedPlan);
        HashMap<String, HashMap<String, String>> repMap = new HashMap<>();
        for (HashMap<String, String> m : replacement)
            repMap.put(m.get("uuid"), m);
        String[] dels = {"searchTime", "solLength", "searchNodes", "searchPeakMemory",
                "searchTotalTime", "solutionStatus"};
        for (HashMap<String, String> row : rows) {
            for (String del : dels)
                row.remove(del);
            String uuid = row.get("uuid");
            HashMap<String, String> newData = repMap.get(uuid);
            for (String k : newData.keySet()) {
                row.put(k, newData.get(k));
            }
            if (recognizedGoals.containsKey(uuid)) {
                recognizedGoals.put(row.get("problemFile"), recognizedGoals.get(uuid));
                recognizedGoals.remove(uuid);
            }
            if (recognizedPlan.containsKey(uuid)) {
                String key = row.get("problemFile");
                List<String> val = recognizedPlan.get(uuid);
                recognizedPlan.put(key, val);
                recognizedPlan.remove(uuid);
            }
        }
    }

    private static void addGoalKeyVals(List<HashMap<String, String>> rows, HashMap<String, HashMap<String, Integer>> recognizedGoals, HashMap<Integer, HashMap<String, Integer>> gtGoals) {
        for (HashMap<String, String> row : rows) {
            if (!row.get("solutionStatus").equals(cSolved))
                continue;
            int id = Integer.parseInt(row.get("probID"));
            HashMap<String, Integer> allGoals = new HashMap<>();

            HashMap<String, Integer> currentRecGoals = recognizedGoals.get(row.get("problemFile"));
            HashMap<String, Integer> currentGtGoals = gtGoals.get(id);
            addGoals(currentRecGoals, allGoals);
            addGoals(currentGtGoals, allGoals);
            int numGtTasks = 0;
            for (String k : currentGtGoals.keySet()) {
                numGtTasks += currentGtGoals.get(k);
            }
            int numRecTasks = 0;
            for (String k : currentRecGoals.keySet()) {
                numRecTasks += currentRecGoals.get(k);
            }
            int truePositives = 0;
            int falsePositives = 0;
            int falseNegative = 0;
            for (String k : allGoals.keySet()) {
                int rec = 0;
                if (currentRecGoals.containsKey(k)) {
                    rec = currentRecGoals.get(k);
                }
                int gt = 0;
                if (currentGtGoals.containsKey(k)) {
                    gt = currentGtGoals.get(k);
                }
                truePositives += Integer.min(gt, rec);
                falsePositives += Integer.max(rec - gt, 0);
                falseNegative += Integer.max(gt - rec, 0);
            }
            row.put("truePositivesPercent", Double.toString(100.0 / numGtTasks * truePositives));
            row.put("falsePositivesPercent", Double.toString(100.0 / numRecTasks * falsePositives));
            row.put("falseNegativePercent", Double.toString(100.0 / numGtTasks * falseNegative));

            row.put("truePositivesAbsolute", Double.toString(truePositives));
            row.put("falsePositivesAbsolute", Double.toString(falsePositives));
            row.put("falseNegativeAbsolute", Double.toString(falseNegative));
            System.out.print("");
        }

        // calc param-less tlts
        if (calcParamLessTasks)
            for (HashMap<String, String> row : rows) {
                if (!row.get("solutionStatus").equals(cSolved))
                    continue;
                int id = Integer.parseInt(row.get("probID"));
                HashMap<String, Integer> currentRecGoals = recognizedGoals.get(row.get("problemFile"));
                HashMap<String, Integer> currentGtGoals = gtGoals.get(id);
                assert currentGtGoals.keySet().size() == 1;
                assert currentRecGoals.keySet().size() == 1;

                String rec = currentRecGoals.keySet().iterator().next();
                rec = rec.substring(0, rec.indexOf(" "));
                String gt = currentGtGoals.keySet().iterator().next();
                gt = gt.substring(0, gt.indexOf(" "));

                row.put("inf.tlt.task.only", Boolean.toString(rec.equals(gt)).toUpperCase());
                row.put("groundtruth.task.only", gt);
            }
    }

    private static void addGoals(HashMap<String, Integer> goals, HashMap<String, Integer> mergedGoals) {
        for (String k : goals.keySet()) {
            if (!mergedGoals.containsKey(k)) {
                mergedGoals.put(k, 0);
            }
            mergedGoals.put(k, Integer.max(goals.get(k), mergedGoals.get(k)));
        }
    }

    private static void addGTLengthToRows(List<HashMap<String, String>> rows, HashMap<Integer, Integer> planLength) {
        for (HashMap<String, String> row : rows) {
            String l = row.get("prefLength");
            int id = Integer.parseInt(row.get("probID"));
            int gtL = planLength.get(id);
            row.put("groundTruthLength", Integer.toString(gtL));
            if (l.equals("FULL")) {
                row.put("prefLength", Integer.toString(gtL));
            }
            l = row.get("prefLength");
            int prefL = Integer.parseInt(l);
            double percentage = 100.0 / (double) gtL * prefL / 100.0;
            row.put(cGtPercentage, Double.toString(percentage));
        }
    }

    private static void readGroundTruth(String gtFile, HashMap<Integer, HashMap<String, Integer>> goals, HashMap<Integer, List<String>> plans, HashMap<Integer, Integer> planLength) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(gtFile));
        int lineNum = 0;
        while (br.ready()) {
            String line = br.readLine();
            lineNum++;
            String[] split = line.split("\\t");
            String[] split2 = split[0].split("\\;");
            HashMap<String, Integer> g = new HashMap<>();
            for (String s : split2) {
                s = s.trim();
                if (!g.containsKey(s)) {
                    g.put(s, 0);
                }
                g.put(s, g.get(s) + 1);
            }
            goals.put(lineNum, g);
            planLength.put(lineNum, Integer.parseInt(split[1]));
            if (split.length > 2) {
                split2 = split[2].split("\\;");
                List<String> plan = new ArrayList<>();
                for (String step : split2) {
                    step = step.trim();
                    if (step.length() > 0)
                        plan.add(step);
                }
                plans.put(lineNum, plan);
            }
        }
    }

    private static void writeCSV(String outFile, List<HashMap<String, String>> runs) throws IOException {
        HashSet<String> allKeys = new HashSet<>();
        for (HashMap<String, String> run : runs) {
            allKeys.addAll(run.keySet());
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
        char del = '"';
        char sep = ';';

        boolean first = true;
        for (String key : allKeys) {
            if (first) {
                first = false;
            } else {
                bw.write(sep);
            }
            bw.write(del);
            bw.write(key);
            bw.write(del);
        }
        bw.write("\n");
        for (HashMap<String, String> run : runs) {
            first = true;
            for (String key : allKeys) {
                String val;
                if (run.containsKey(key))
                    val = run.get(key);
                else
                    val = "";
                if (first) {
                    first = false;
                } else {
                    bw.write(sep);
                }
                bw.write(del);
                bw.write(val);
                bw.write(del);
            }
            bw.write("\n");
        }
        bw.close();
    }

    enum timeCmd {timeGrounding, timeSearch, timeAll}

    private static List<HashMap<String, String>> readLogFile(String inFile, HashMap<String, HashMap<String, Integer>> reconizedTasks, HashMap<String, List<String>> recognizedPlan) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(inFile));
        List<HashMap<String, String>> runs = new ArrayList<>();
        HashMap<String, Set<String>> methodTltMapping = getTltMapping();

        HashMap<String, String> oneRun = null;
        timeCmd mode = timeCmd.timeAll;
        while (br.ready()) {
            String line = br.readLine();
            if (line.startsWith("Processing ")) {
                if (oneRun != null)
                    runs.add(oneRun);
                oneRun = new HashMap<>();
                line = line.substring("Processing ".length());
                if (line.startsWith("d-")) {
                    oneRun.put("domainFile", line);
                    line = br.readLine();
                }
                String uuid;
                if (line.endsWith(".htn"))
                    uuid = line.substring(0, line.length() - ".htn".length());
                else
                    uuid = line;
                oneRun.put("uuid", uuid);
                continue;
            } else if (line.trim().startsWith("Command being timed: ")) {
                line = line.trim();
                if (line.startsWith("Command being timed: \"java "))
                    mode = timeCmd.timeGrounding;
                else if (line.startsWith("Command being timed: \"./SearchEngine"))
                    mode = timeCmd.timeSearch;
                else if (line.startsWith("Command being timed: \"./panda-pro.sh"))
                    mode = timeCmd.timeAll;
                continue;
            } else if (line.startsWith("Problem: ")) {
                oneRun.put("problemFile", line.substring("Problem: ".length()));
                String probNumStr = line.substring("Problem: p-".length(), "Problem: p-".length() + 4);
                oneRun.put("probID", Integer.toString(Integer.parseInt(probNumStr)));
                if (line.contains("-no-pref"))
                    oneRun.put("prefLength", "0");
                else if (line.contains("-full-pref"))
                    oneRun.put("prefLength", "FULL");
                else {
                    String prefLength = line.substring(line.lastIndexOf("-") + 1, line.lastIndexOf("."));
                    oneRun.put("prefLength", Integer.toString(Integer.parseInt(prefLength)));
                }
                continue;
            } else if (line.startsWith("Search time ")) {
                oneRun.put("searchTime", getNumber(line.substring("Search time ".length())));
                continue;
            } else if (line.startsWith("Generated ") && line.endsWith(" search nodes.")) {
                oneRun.put("searchNodes", getNumber(line.substring("Generated ".length())));
                continue;
            } else if (line.startsWith("Found solution of length ")) {
                oneRun.put("solLength", getNumber(line.substring("Found solution of length ".length())));
                oneRun.put("solutionStatus", cSolved);
                continue;
            } else if (line.startsWith("Reached time limit - stopping search.")) {
                oneRun.put("solutionStatus", cTimeout);
            } else if (line.startsWith("\tElapsed (wall clock) time (h:mm:ss or m:ss): ")) {
                String pref;
                if (mode == timeCmd.timeGrounding)
                    pref = "grounding";
                else if (mode == timeCmd.timeSearch)
                    pref = "search";
                else
                    continue;
                oneRun.put(pref + "TotalTime", getTime(line.substring("\tElapsed (wall clock) time (h:mm:ss or m:ss): ".length())));
                continue;
            } else if (line.startsWith("\tMaximum resident set size (kbytes): ")) {
                String pref;
                if (mode == timeCmd.timeGrounding)
                    pref = "grounding";
                else if (mode == timeCmd.timeSearch)
                    pref = "search";
                else
                    continue;
                oneRun.put(pref + "PeakMemory", getNumber(line.substring("\tMaximum resident set size (kbytes): ".length())));
                continue;
            } else if (line.startsWith("\tSwaps: ")) {
                if (!getNumber(line.substring("\tSwaps: ".length())).equals("0"))
                    System.out.println("ERROR: Found SWAP");
                continue;
            } else if (line.trim().endsWith("@ mtlt[]")) {
                line = br.readLine();
                HashMap<String, Integer> recTasks = new HashMap<>();
                List<String> recPlan = new ArrayList<>();
                getRecResult(br, methodTltMapping, line, recTasks, recPlan);
                recognizedPlan.put(oneRun.get("problemFile"), recPlan);
                reconizedTasks.put(oneRun.get("problemFile"), recTasks);
            } else if (line.trim().endsWith("@ tlt[]")) { // TODO: this is hacky! it works because Monroe has one and kitchen multiple tlts
                assert line.startsWith("m_tlt_");
                line = line.substring(0, line.length() - "@ tlt[]".length() - 1);
                line = line.substring("m_tlt_".length());
                String task = line.substring(0, line.indexOf("["));
                String args = line.substring(line.indexOf("["));
                args = args.substring(1, args.length() - 1);
                String[] params = args.split("\\,");

                for (int i = 0; i < params.length; i++) {
                    String[] param = params[i].split("\\=");
                    assert param.length == 2;
                    task += " " + param[1];
                }
                HashMap<String, Integer> tlt = new HashMap<>();
                tlt.put(task, 1);
                String identifier = oneRun.get("problemFile");
                if (identifier == null)
                    identifier = oneRun.get("uuid");
                reconizedTasks.put(identifier, tlt);
                List<String> recPlan = new ArrayList<>();

                line = br.readLine();
                while (line.trim().length() > 0) {
                    if (!line.contains("@")) {
                        if (!line.startsWith("SHOP"))
                            recPlan.add(line);
                    }
                    line = br.readLine();
                }
                recognizedPlan.put(identifier, recPlan);
            }
        }
        if (oneRun != null)
            runs.add(oneRun);

        br.close();
        return runs;
    }

    private static void getRecResult(BufferedReader br, HashMap<String, Set<String>> methodTltMapping, String line, HashMap<String, Integer> reconizedTasks, List<String> recognizedPlan) throws IOException {
        HashMap<String, List<String>> interessting = new HashMap<>();
        while (line.trim().length() > 0) {
            if (!line.contains("@")) {
                if (!line.startsWith("SHOP"))
                    recognizedPlan.add(line);
            } else {
                String mName = line.substring(0, line.indexOf("["));
                String method = startsWithKey(mName, methodTltMapping);
                if (method.length() > 0) {
                    if (!interessting.containsKey(method))
                        interessting.put(method, new ArrayList<>());
                    interessting.get(method).add(line);
                }
            }
            line = br.readLine();
        }
        for (String method : interessting.keySet()) {
            HashMap<String, String> params = new HashMap<>();
            for (String val : interessting.get(method)) {
                int start = val.indexOf("[") + 1;
                int end = val.indexOf("]");
                String paramStr = val.substring(start, end);
                if (paramStr.length() == 0)
                    continue;
                String[] pSplit = paramStr.split(",");
                for (String s : pSplit) {
                    String[] split = s.split("\\=");
                    assert split.length == 2;
                    if (params.containsKey(split[0]))
                        assert params.get(split[0]).equals(split[1]);
                    params.put(split[0], split[1]);
                }
            }
            HashMap<String, Integer> newTasks = getRecTasks(methodTltMapping, method, params);
            for (String k : newTasks.keySet()) {
                if (!reconizedTasks.containsKey(k)) {
                    reconizedTasks.put(k, 0);
                }
                reconizedTasks.put(k, reconizedTasks.get(k) + 1);
            }
        }
    }

    private static HashMap<String, Integer> getRecTasks(HashMap<String, Set<String>> methodTltMapping, String mainMethod, HashMap<String, String> mainParams) {
        HashMap<String, Integer> reconizedTasks = new HashMap<>();
        for (String t : methodTltMapping.get(mainMethod)) {
            t = t.substring(1, t.length() - 1);
            String[] split = t.split(" ");
            String taskName = split[0];
            for (int i = 1; i < split.length; i++) {
                if (split[i].startsWith("?")) {
                    assert mainParams.containsKey(split[i]);
                    taskName += " " + mainParams.get(split[i]);
                } else {
                    String var = "varToConstConstant(";
                    int num = 0;
                    String foundK = null;
                    for (String k : mainParams.keySet()) {
                        if (k.startsWith(var)) {
                            num++;
                            foundK = k;
                        }
                    }
                    assert num == 1;
                    taskName += " " + mainParams.get(foundK);
                }
            }
            String fullTaskName = "(" + taskName + ")";
            if (!reconizedTasks.containsKey(fullTaskName))
                reconizedTasks.put(fullTaskName, 0);
            reconizedTasks.put(fullTaskName, reconizedTasks.get(fullTaskName) + 1);
        }
        return reconizedTasks;
    }

    private static String startsWithKey(String mName, HashMap<String, Set<String>> methodTltMapping) {
        for (String k : methodTltMapping.keySet())
            if ((mName.equals(k)) || (mName.startsWith(k + "_")))
                return k;
        return "";
    }

    private static String getTime(String str) {
        List<Integer> trenner = new ArrayList<>();
        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i) == ':')
                trenner.add(i);
        if (trenner.size() == 1) {
            String min = str.substring(0, trenner.get(0));
            String sec = str.substring(trenner.get(0) + 1);
            double secs = Integer.parseInt(min) * 60 + Double.parseDouble(sec);
            return Double.toString(secs);
        } else
            return null;
    }

    private static String getNumber(String str) {
        int i = 0;
        while ((i < str.length()) && (isNum(str.charAt(i)))) {
            i++;
        }
        return str.substring(0, i);
    }

    private static boolean isNum(char c) {
        return ((c == '0') || (c == '1') || (c == '2') || (c == '3') || (c == '4') || (c == '5')
                || (c == '6') || (c == '7') || (c == '8') || (c == '9') || (c == '.') || (c == ','));
    }

    public static HashMap<String, Set<String>> getTltMapping() {
        HashMap<String, Set<String>> methodTltMapping = new HashMap<>();
        String key = "";
        HashSet<String> tlts = new HashSet<>();

        key = "starter_1";
        tlts.add("(makeTomatoSoup ?pot1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "starter_2";
        tlts.add("(makeLettuce ?bowl1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "starter_3";
        tlts.add("(makeTomatoMozzarella ?bowl1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "starter_4";
        tlts.add("(makeBruchetta)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "starter_5";
        tlts.add("(makeCarrotSoup ?pot1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_1";
        tlts.add("(makeNoodles spaghetti ?pot1)");
        tlts.add("(makeBolognese ?pan1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_2";
        tlts.add("(makeNoodles spaghetti ?pot1)");
        tlts.add("(makeCarbonara ?pan1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_3";
        tlts.add("(makeNoodles spaghetti ?pot1)");
        tlts.add("(makeAllArrabbiata ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_4";
        tlts.add("(makeNoodles cannelloni ?pot1)");
        tlts.add("(makeBolognese ?pan1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_5";
        tlts.add("(makeNoodles cannelloni ?pot1)");
        tlts.add("(makeCarbonara ?pan1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_6";
        tlts.add("(makeNoodles cannelloni ?pot1)");
        tlts.add("(makeAllArrabbiata ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_7";
        tlts.add("(makeNoodles tortellini ?pot1)");
        tlts.add("(makeBolognese ?pan1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_8";
        tlts.add("(makeNoodles tortellini ?pot1)");
        tlts.add("(makeCarbonara ?pan1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_9";
        tlts.add("(makeNoodles tortellini ?pot1)");
        tlts.add("(makeAllArrabbiata ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_10";
        tlts.add("(makeNoodles ravioli ?pot1)");
        tlts.add("(makeBolognese ?pan1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_11";
        tlts.add("(makeNoodles ravioli ?pot1)");
        tlts.add("(makeCarbonara ?pan1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_12";
        tlts.add("(makeNoodles ravioli ?pot1)");
        tlts.add("(makeAllArrabbiata ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_13";
        tlts.add("(makeBoiledPotatoes ?pot1)");
        tlts.add("(makeTrout ?pan1)");
        tlts.add("(makeBeans ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_14";
        tlts.add("(makeBoiledPotatoes ?pot1)");
        tlts.add("(makeChicken ?rt1)");
        tlts.add("(makeBeans ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_15";
        tlts.add("(makeBoiledPotatoes ?pot1)");
        tlts.add("(makeSchnitzel ?pan1)");
        tlts.add("(makeBeans ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_16";
        tlts.add("(makeBoiledPotatoes ?pot1)");
        tlts.add("(makeTrout ?pan1)");
        tlts.add("(makePea ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_17";
        tlts.add("(makeBoiledPotatoes ?pot1)");
        tlts.add("(makeChicken ?rt1)");
        tlts.add("(makePea ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_18";
        tlts.add("(makeBoiledPotatoes ?pot1)");
        tlts.add("(makeSchnitzel ?pan1)");
        tlts.add("(makePea ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_19";
        tlts.add("(makeSkinnedPotatoes ?pot1)");
        tlts.add("(makeTrout ?pan1)");
        tlts.add("(makeBeans ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_20";
        tlts.add("(makeSkinnedPotatoes ?pot1)");
        tlts.add("(makeChicken ?rt1)");
        tlts.add("(makeBeans ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_21";
        tlts.add("(makeSkinnedPotatoes ?pot1)");
        tlts.add("(makeSchnitzel ?pan1)");
        tlts.add("(makeBeans ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_22";
        tlts.add("(makeSkinnedPotatoes ?pot1)");
        tlts.add("(makeTrout ?pan1)");
        tlts.add("(makePea ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_23";
        tlts.add("(makeSkinnedPotatoes ?pot1)");
        tlts.add("(makeChicken ?rt1)");
        tlts.add("(makePea ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_24";
        tlts.add("(makeSkinnedPotatoes ?pot1)");
        tlts.add("(makeSchnitzel ?pan1)");
        tlts.add("(makePea ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_25";
        tlts.add("(makeRice ?pot1)");
        tlts.add("(makeTrout ?pan1)");
        tlts.add("(makeBeans ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_26";
        tlts.add("(makeRice ?pot1)");
        tlts.add("(makeChicken ?rt1)");
        tlts.add("(makeBeans ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_27";
        tlts.add("(makeRice ?pot1)");
        tlts.add("(makeSchnitzel ?pan1)");
        tlts.add("(makeBeans ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_28";
        tlts.add("(makeRice ?pot1)");
        tlts.add("(makeTrout ?pan1)");
        tlts.add("(makePea ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_29";
        tlts.add("(makeRice ?pot1)");
        tlts.add("(makeChicken ?rt1)");
        tlts.add("(makePea ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "main_30";
        tlts.add("(makeRice ?pot1)");
        tlts.add("(makeSchnitzel ?pan1)");
        tlts.add("(makePea ?pot2)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "dessert_1";
        tlts.add("(makeVanillaPudding ?pot1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "dessert_2";
        tlts.add("(makeVanillaRaspberryIce ?bowl1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "dessert_3";
        tlts.add("(makeTiramisu ?bowl1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "dessert_4";
        tlts.add("(makeMascarpone ?bowl1)");
        methodTltMapping.put(key, tlts);
        tlts = new HashSet<>();

        key = "dessert_5";
        tlts.add("(makePancakes ?pan1)");
        methodTltMapping.put(key, tlts);

        return methodTltMapping;
    }
}
