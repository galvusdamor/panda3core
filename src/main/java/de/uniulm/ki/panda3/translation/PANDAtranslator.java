package de.uniulm.ki.panda3.translation;

import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.parser.hddl.hddlLexer;
import de.uniulm.ki.panda3.symbolic.parser.hddl.hddlPanda3Visitor;
import de.uniulm.ki.panda3.symbolic.parser.hddl.hddlParser;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.writer.hpddl.HPDDLWriter;
import de.uniulm.ki.panda3.symbolic.writer.xml.XMLWriter;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import scala.Tuple2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by dhoeller on 27.01.16.
 */
public class PANDAtranslator {
    public static void main(String[] strs) throws Exception {
        FileReader inDomain = null;
        FileReader inProblem = null;
        // try {
        String ourPDDLFormat = "hddl";
        String ourXmlFormat = "xml";
        String ronsPDDLFormat = "ron";
        List<String> languages = new LinkedList<>();

        languages.add(ourXmlFormat);
        languages.add(ourPDDLFormat);
        languages.add(ronsPDDLFormat);

        if (strs.length < 2) { // need at least direction and one file name
            printHelp(languages);
            System.out.println("PANDA says: I did nothing.");
            return;
        }

        String fromLang = "";
        String toLang = "";

        String fromDomainFile = "";
        String fromProblemFile = "";

        String toDomainFile = "";
        String toProblemFile = "";


        if (strs[0].contains("-")) {
            String[] split = strs[0].split("-");
            if (languages.contains(split[0]) && languages.contains(split[1])) {
                fromLang = split[0];
                toLang = split[1];
                if (new File(strs[1]).exists()) {
                    fromDomainFile = strs[1];
                }
                if ((strs.length >= 3) && (new File(strs[2]).exists())) {
                    fromProblemFile = strs[2];
                }
                if (strs.length >= 5) {
                    toDomainFile = strs[3];
                    toProblemFile = strs[4];
                }
            }
        } else if ((strs.length >= 3) && strs[1].equals("-") && languages.contains(strs[0]) && languages.contains(strs[2])) {
            fromLang = strs[0];
            toLang = strs[2];
            if ((strs.length >= 4) && (new File(strs[3]).exists())) {
                fromDomainFile = strs[3];
            }
            if ((strs.length >= 5) && (new File(strs[4]).exists())) {
                fromProblemFile = strs[4];
            }
            if (strs.length >= 7) {
                toDomainFile = strs[5];
                toProblemFile = strs[6];
            }
        }


        if (fromLang.equals(toLang)) {
            System.out.println("PANDA says: I do not know format. I did nothing.");
            return;
        }
        if (toLang.length() == 0) {
            System.out.println("PANDA says: I do not know out-format. I did nothing.");
            return;
        }
        if (fromDomainFile.length() == 0) {
            System.out.println("PANDA says: I could not find domain-file. I did nothing.");
            return;
        }
        if (fromProblemFile.length() == 0) {
            System.out.println("PANDA says: I could not find problem-file. I did nothing.");
            return;
        }

        if (toDomainFile.length() == 0) {
            int i = 1;
            toDomainFile = fromDomainFile + "." + toLang;
            toProblemFile = fromProblemFile + "." + toLang;
            while ((new File(toProblemFile).exists()) || (new File(toDomainFile).exists())) {
                toDomainFile = fromDomainFile + "." + toLang + i;
                toProblemFile = fromProblemFile + "." + toLang + i++;
            }
            System.out.println("PANDA says: No output filenames specified, will use \n\"" + toDomainFile + "\" and \n\"" + toProblemFile + "\".");
        }


        inDomain = new FileReader(fromDomainFile);
        inProblem = new FileReader(fromProblemFile);

        Tuple2<Domain, Plan> planningInstance = null;
        boolean readProblem = false;
        if (fromLang.equals(ourPDDLFormat)) {
            hddlLexer lDomain = new hddlLexer(new ANTLRInputStream(inDomain));
            hddlLexer lProblem = new hddlLexer(new ANTLRInputStream(inProblem));

            hddlParser pDomain = new hddlParser(new CommonTokenStream(lDomain));
            hddlParser pProblem = new hddlParser(new CommonTokenStream(lProblem));

            planningInstance = new hddlPanda3Visitor().visitInstance(pDomain.domain(), pProblem.problem());
            readProblem = true;
        } else {
            System.out.println("PANDA says: Input format not yet implemented: \"" + fromLang + "\".");
        }

        if ((readProblem) && (toLang.equals(ronsPDDLFormat))) {
            HPDDLWriter writer = HPDDLWriter.apply("someDomain", "someProblem");

            BufferedWriter bwDomain = new BufferedWriter(new FileWriter(toDomainFile));
            BufferedWriter bwProblem = new BufferedWriter(new FileWriter(toProblemFile));

            bwDomain.write(writer.writeDomain(planningInstance._1()));
            bwProblem.write(writer.writeProblem(planningInstance._1(), planningInstance._2()));

            bwDomain.close();
            bwProblem.close();

            System.out.println("PANDA says: Done.");
        } else if ((readProblem) && (toLang.equals(ourXmlFormat))) {
            XMLWriter writer = new XMLWriter("someDomain", "someProblem");

            BufferedWriter bwDomain = new BufferedWriter(new FileWriter(toDomainFile));
            BufferedWriter bwProblem = new BufferedWriter(new FileWriter(toProblemFile));

            bwDomain.write(writer.writeDomain(planningInstance._1()));
            bwProblem.write(writer.writeProblem(planningInstance._1(), planningInstance._2()));

            bwDomain.close();
            bwProblem.close();

            System.out.println("PANDA says: Done.");
        } else {
            System.out.println("PANDA says: Output format not yet implemented: \"" + toLang + "\".");
        }
        /*} catch (Exception e) {
            System.out.println("PANDA says: Something went wrong \n");
            e.printStackTrace();
        }*/
    }

    private static void printHelp(List<String> languages) {
        System.out.print("The PANDAtranslator enables the translation between different HTN description languages.\n" +
                //"It further enables to enforce a given prefix into a planning problem, e.g. for plan recognition or repair.\n\nParameters:"+
                "PANDAtranlate <from>-<to> <domain-in-file> <problem-in-file> [domain-out-file] [problem-out-file]\n" +
                "where <from> and <to> are out of [");

        for (int i = 0; i < languages.size(); i++) {
            if (i > 0) {
                System.out.print("|");
            }
            System.out.print(languages.get(i));
        }
        System.out.print("]\n");
    }

}
