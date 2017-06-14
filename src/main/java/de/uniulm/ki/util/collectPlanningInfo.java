package de.uniulm.ki.util;

import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.PriorityQueueSearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by dh on 27.09.16.
 */
public class collectPlanningInfo {

    static private final String parsingKey = "#";

    public static void main(String[] args) throws Exception {
        //String inputFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/verification.log";
        //String outFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/verification.csv";
        //String inputFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/greedy-imp-random/greedy-imp-random.log";
        //String outFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/greedy-imp-random/greedy-imp-random.csv";
        //String inputFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/07-partial-observable/04-results-rcg-greedy/all.log";
        //String outFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/07-partial-observable/04-results-rcg-greedy/all.out";
        String inputFile = args[0]; //"foo";
        String outFile = args[1]; // "foo.csv";
        //String inputFile = "/home/gregor/Workspace/Panda3/lifted_planner/transport.log"; //"foo";
        //String outFile = "test.csv"; // "foo.csv";
        FileReader fr = new FileReader(inputFile);
        BufferedReader br = new BufferedReader(fr);

        HashMap<String, Boolean> isBoolsch = new HashMap<>();

        List<Map<String, String>> allExps = new LinkedList<>();
        HashMap<String, String> oneExp = new HashMap<>();
        int num = Integer.MIN_VALUE; // no insert at first #-line
        while (br.ready()) {
            String line = br.readLine();
            if (line.startsWith(parsingKey)) {
                String csv = line.substring(parsingKey.length(), line.length());
                String numStr = "";
                while (isNumber(csv.charAt(0))) {
                    numStr += csv.charAt(0);
                    csv = csv.substring(1);
                }
                int lastNum = num;
                num = Integer.parseInt(numStr);

                if (num < lastNum) {
                    allExps.add(oneExp);
                    oneExp = new HashMap<>();
                }

                csv = csv.trim();
                String[] elems = csv.substring(1, csv.length() -1).split("\";\"");
                for (String elem : elems) {
                    String[] pair = elem.split("\"=\"");
                    if (pair.length == 2) {
                        oneExp.put(pair[0], pair[1]);
                        if (!isBoolsch.containsKey(pair[0]))
                            isBoolsch.put(pair[0], false);
                        assert (!isBoolsch.get(pair[0]));
                    } else if (pair.length == 1) {
                        oneExp.put(pair[0], "");
                        if (!isBoolsch.containsKey(pair[0]))
                            isBoolsch.put(pair[0], true);
                        assert (isBoolsch.get(pair[0]));
                    } else {
                        assert (false);
                    }
                }
            } else if (line.startsWith("3 method-")) {
                String[] temp = line.split(" ");
                oneExp.put("postProcTLT", temp[temp.length - 1]);
            }
        }
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
                String val;
                if (isBoolsch.get(key)) {
                    if (exp.containsKey(key)) {
                        val = "TRUE";
                    } else {
                        val = "FALSE";
                    }
                } else {
                    if (exp.containsKey(key)) {
                        val = exp.get(key);
                    } else val = "";
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

    private static boolean isNumber(char c) {
        //return (c == '0') || (c == '1') || (c == '2') || (c == '3') || (c == '4') ||
        //        (c == '5') || (c == '6') || (c == '7') || (c == '8') || (c == '9');

        return c >= '0' && c <= '9';
    }
}
