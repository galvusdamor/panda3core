package de.uniulm.ki.panda3.planRecognition.kitchen;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dh on 02.11.17.
 */
public class createScripts {
    static String start = "java -jar PANDAaddPrefix.jar -domain domain.lisp ";
    static int planLengthSum = 0;

    public static void main(String[] in) throws Exception {
        //String file = "/media/dh/Volume/repositories/private-documents/evaluation-domains/kitchen/problems/p-0001-kitchen.lisp.sol";
        String dirName = "/media/dh/Volume/repositories/private-documents/evaluation-domains/kitchen/problems/";
        File dir = new File(dirName);
        int files = 0;
        for (File f : dir.listFiles()) {
            String fileName = f.toString();
            if (!(fileName.endsWith(".sol")))
                continue;
            String file = fileName;
            String target = file.replace(".lisp.sol", ".sh2");
            processFile(file, target);
            files++;
        }
        System.out.println("Mean plan length: " + ((double) planLengthSum / (double) files));

        String groundTruthFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/kitchen/groundtruth.txt";
        String groundTruthFileZiel = "/media/dh/Volume/repositories/private-documents/evaluation-domains/kitchen/groundtruth2.txt";
        BufferedReader br = new BufferedReader(new FileReader(groundTruthFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(groundTruthFileZiel));
        int i = 1;
        while (br.ready()) {
            String line = br.readLine();
            String plan = groundT.get(i);
            bw.write(line + "\t" + groundTLength.get(i) + "\t" + plan + "\n");
            i++;
        }
        br.close();
        bw.close();
    }

    static HashMap<Integer, String> groundT = new HashMap<>();
    static HashMap<Integer, Integer> groundTLength = new HashMap<>();

    private static void processFile(String inFile, String outFile) throws IOException {
        List<String> sol = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(inFile));
        String line = br.readLine();
        while (!line.equals("SOLUTION SEQUENCE")) {
            line = br.readLine();
        }
        while (br.ready()) {
            line = br.readLine();
            int i = line.indexOf(":");
            line = line.substring(i + 2).replaceAll("\\(\\)", "").replaceAll("\\]", "").replaceAll("\\[", " ").replaceAll("\\,", " ");
            line = "(" + line + ")";
            if (!line.startsWith("(SHOP_"))
                sol.add(line);
        }
        int numStrStart = inFile.indexOf("/p-") + 3;
        String numStr = inFile.substring(numStrStart, numStrStart + 4);
        int probNum = Integer.parseInt(numStr);
        String solStr = "";
        for (int i = 0; i < sol.size(); i++) {
            if (i > 0)
                solStr += "; ";
            solStr += sol.get(i);
        }
        groundT.put(probNum, solStr);
        groundTLength.put(probNum, sol.size());
        planLengthSum += sol.size();
        String targetFile = inFile.substring(inFile.indexOf("/p-"));
        String orgProblemFile = targetFile.substring(1, targetFile.indexOf(".sol"));
        targetFile = targetFile.substring(2, targetFile.indexOf("."));

        BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
        for (int i = 0; i <= sol.size(); i++) {
            String prefLength;
            if (i == 0)
                prefLength = "no-pref";
            else if (i == sol.size())
                prefLength = "full-pref";
            else prefLength = Integer.toString(i);
            String targetD = "d" + targetFile + "-" + prefLength + ".hddl";
            String targetP = "p" + targetFile + "-" + prefLength + ".hddl";
            String script = "echo \"" + orgProblemFile + " " + i + "\"\n"
                    + start + targetD
                    + " -problem " + orgProblemFile + " " + targetP
                    + " -prefix \"";
            for (int j = 0; j < i; j++)
                script += sol.get(j);
            script += "\"\n";
            bw.write(script);
        }
        bw.close();
    }
}
