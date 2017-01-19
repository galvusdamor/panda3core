package de.uniulm.ki.panda3.progression.proUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by dh on 27.09.16.
 */
public class collectPlanningInfo {
    public static void main(String[] args) throws Exception {
        //String inputFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/verification.log";
//        String outFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/verification.csv";
        String inputFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/greedy-imp-random-1-10/data.log";
        String outFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/greedy-imp-random-1-10/single-tlt.csv";
        FileReader fr = new FileReader(inputFile);
        BufferedReader br = new BufferedReader(fr);

        List<Map<String, String>> allExps = new LinkedList<>();
        HashMap<String, String> oneExp = null;
        while (br.ready()) {
            String line = br.readLine();
            if (line.startsWith("Processing d-")) {
                if (oneExp != null)
                    allExps.add(oneExp);
                oneExp = new HashMap<>();
                String[] split = line.split(" ");
                String expName = split[1].substring("d-".length(), split[1].length() - 5);
                oneExp.put("randomSeedSh",  split[2]);
                oneExp.put("expName", expName);
            } else if (line.startsWith("###")) {
                String csv = line.substring("###\"".length(), line.length() - 1);
                String[] elems = csv.split("\";\"");
                for (String elem : elems) {
                    String[] pair = elem.split("\"=\"");
                    if (pair.length == 2) {
                        oneExp.put(pair[0], pair[1]);
                    } else if (pair.length == 1) {
                        oneExp.put(pair[0], "");
                    } else {
                        assert (false);
                    }
                }
                String foundPlans = oneExp.get("foundPlans");
                if (Double.parseDouble(foundPlans) > 0)
                    oneExp.put("X99.progression.01.status", "solved");
            } else if (line.startsWith("Domain is acyclic: ")) {
                oneExp.put("acyclic", line.substring("Domain is acyclic: ".length()));
            }
        }
        if (oneExp != null)
            allExps.add(oneExp);

        br.close();
        fr.close();

        HashSet<String> keySet = new HashSet<>();
        for (Map<String, String> exp : allExps) {
            keySet.addAll(exp.keySet());
        }

        FileWriter fw = new FileWriter(outFile);
        BufferedWriter bw = new BufferedWriter(fw);
        boolean first = true;
        for (String key : keySet) {
            if (first) {
                first = false;
            } else {
                bw.write(";");
            }
            bw.write("\"");
            bw.write(key);
            bw.write("\"");
        }
        bw.write("\n");

        for (Map<String, String> exp : allExps) {
            first = true;
            for (String key : keySet) {
                String val = "";
                if (exp.containsKey(key)) {
                    val = exp.get(key);
                } else if (key.equals("99 progression:01:status")) {
                    val = "no-search";
                }
                if (first) {
                    first = false;
                } else {
                    bw.write(";");
                }
                bw.write("\"");
                bw.write(val);
                bw.write("\"");
            }
            bw.write("\n");
        }
        bw.close();
        fw.close();
    }
}
