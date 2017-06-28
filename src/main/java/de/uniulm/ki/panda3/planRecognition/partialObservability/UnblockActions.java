package de.uniulm.ki.panda3.planRecognition.partialObservability;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 01.02.17.
 */
public class UnblockActions {
    enum context {declaration, precondition, effect}

    public static void main(String[] args) throws Exception {
        String inDir = "/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/07-partial-observable/02-unblock-actions/";
        File dir = new File(inDir);
        for (File f : dir.listFiles()) {
            BufferedReader br = new BufferedReader(new FileReader(f));
            List<String> lines = new ArrayList<>();
            boolean found = false;
            int lastLit = 1;
            while (br.ready()) {
                String line = br.readLine() + "\n";
                lines.add(line);
                if (line.trim().startsWith("(l1)"))
                    found = true;
                else if (found && (line.trim().startsWith("(l"))) {
                    lastLit++;
                } else if (found) {
                    found = false;
                }
            }
            br.close();


            List<String> newLines = new ArrayList<>();
            context c = context.declaration;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.indexOf("precondition") > -1)
                    c = context.precondition;
                else if (line.indexOf("effect") > -1)
                    c = context.effect;
                if ((c == context.precondition) && line.contains("(l" + lastLit + ")")) {
                    line = line.replaceAll("\\(l" + lastLit + "\\)", "");
                }
                if (line.trim().length() > 0)
                    newLines.add(line);
            }
            lines = newLines;

            newLines = new ArrayList<>();
            for (int i = 0; i < lines.size() - 1; i++) {
                String l1 = lines.get(i).trim();
                String l2 = lines.get(i + 1).trim();
                if (l1.equals("(and") && l2.equals(")")){
                    i++;
                    newLines.add("()\n");
                }
                else
                    newLines.add(lines.get(i));
            }
            newLines.add(")\n");
            lines = newLines;

            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            for (String line : lines)
                bw.write(line);
            bw.close();
        }

    }
}
