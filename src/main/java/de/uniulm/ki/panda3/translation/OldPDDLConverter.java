package de.uniulm.ki.panda3.translation;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
public class OldPDDLConverter {

    public static void processDomain(String domainOut, String txtFile) throws Exception {
        FileWriter fw = new FileWriter(domainOut);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(txtFile.toLowerCase());
        bw.close();
    }

    public static void processProblem(String problemOut, String txtFile) throws Exception {
        FileWriter fw = new FileWriter(problemOut);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(txtFile.toLowerCase());
        bw.close();
    }
}
