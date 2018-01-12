package de.uniulm.ki.panda3.planRecognition.partialObservability;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by dh on 01.02.17.
 * <p>
 * This program deletes observations from batch files used in plan recognition to call the PANDAaddPrefix
 * program. By deleting observations, it is simulated that observations have been missed.
 */
public class DeleteObservations {
    public static void main(String[] args) throws Exception {
        //String inDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/07-partial-observable/";
        String inDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/kitchen-corpus/07-part-obs/";
        int probability = 20;
        File dir = new File(inDir);
        for (File f : dir.listFiles()) {
            String inFile = f.getAbsolutePath();
            String outFile = inFile.replace(".sh", "-part-obs.sh");
            processFile(inFile, outFile, probability);
        }
    }

    public static void processFile(String inFile, String outFile, int probability) throws IOException {
        boolean containsVerifyInstance = false;
        int offset;
        if (containsVerifyInstance)
            offset = 1;
        else
            offset = 0;

        BufferedReader br = new BufferedReader(new FileReader(inFile));
        List<String> lines = new ArrayList<>();
        while (br.ready())
            lines.add(br.readLine());
        br.close();

        String lastLine = lines.get(lines.size() - 1);
        String[] split = lastLine.split("\"");
        String prefix = split[1];
        String[] actions = prefix.substring(1, prefix.length() - 1).split("\\)\\(");
        boolean[] observed = new boolean[actions.length];
        Random r = new Random(42);
        for (int i = 0; i < observed.length; i++) {
            int random = r.nextInt(100);
            observed[i] = (random >= probability);
        }

        List<String> modified = new ArrayList<>();
        for (int i = 0; i < lines.size() - offset; i++) {
            String line = lines.get(i);
            if (line.startsWith("echo")) {
                modified.add(line + "\n");
                continue;
            }

            String[] split2 = line.split("\"");
            if (split2.length == 1) {
                modified.add(line + "\n");
                continue;
            }

            String intro2 = split2[0];
            String prefix2 = split2[1];
            String[] actions2 = prefix2.substring(1, prefix2.length() - 1).split("\\)\\(");

            String mod = intro2 + "\"";

            for (int j = 0; j < actions2.length; j++) {
                if (observed[j]) {
                    mod += "(" + actions2[j] + ")";
                }
            }
            mod += "\"\n";
            modified.add(mod);
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
        for (int i = 0; i < modified.size(); i++) {
            bw.write(modified.get(i));
        }
        bw.close();
    }
}
