package de.uniulm.ki.panda3.symbolic.ioInterface;

import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.parser.hddl.antlrHDDLLexer;
import de.uniulm.ki.panda3.symbolic.parser.hddl.hddlPanda3Visitor;
import de.uniulm.ki.panda3.symbolic.parser.hddl.antlrHDDLParser;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import de.uniulm.ki.panda3.symbolic.writer.hddl.HDDLWriter;
import de.uniulm.ki.panda3.symbolic.writer.hpddl.HPDDLWriter;
import de.uniulm.ki.panda3.symbolic.writer.xml.XMLWriter;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import scala.Tuple2;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Loads and writes planning instances from and to files in different file formats
 */
public class FileHandler {
    public static Tuple2<Domain, Plan> loadHDDLFromFile(String domainFileName, String problemFileName) throws IOException {
        FileReader inDomain = new FileReader(domainFileName);
        FileReader inProblem = new FileReader(problemFileName);

        Tuple2<Domain, Plan> planningInstance;
        antlrHDDLLexer lDomain = new antlrHDDLLexer(new ANTLRInputStream(inDomain));
        antlrHDDLLexer lProblem = new antlrHDDLLexer(new ANTLRInputStream(inProblem));

        antlrHDDLParser pDomain = new antlrHDDLParser(new CommonTokenStream(lDomain));
        antlrHDDLParser pProblem = new antlrHDDLParser(new CommonTokenStream(lProblem));

        planningInstance = new hddlPanda3Visitor().visitInstance(pDomain.domain(), pProblem.problem());
        return planningInstance;
    }

    public static void writeXMLToFiles(Tuple2<Domain, Plan> planningInstance, String domainFileName, String problemFileName) throws IOException {
        XMLWriter writer = new XMLWriter("someDomain", "someProblem");

        BufferedWriter bwDomain = new BufferedWriter(new FileWriter(domainFileName));
        BufferedWriter bwProblem = new BufferedWriter(new FileWriter(problemFileName));


        bwDomain.write(writer.writeDomain(planningInstance._1()));
        bwProblem.write(writer.writeProblem(planningInstance._1(), planningInstance._2()));

        bwDomain.close();
        bwProblem.close();
    }

    public static void writeRonToFiles(Tuple2<Domain, Plan> planningInstance, String domainFileName, String problemFileName) throws IOException {
        HPDDLWriter writer = HPDDLWriter.apply("someDomain", "someProblem");

        BufferedWriter bwDomain = new BufferedWriter(new FileWriter(domainFileName));
        BufferedWriter bwProblem = new BufferedWriter(new FileWriter(problemFileName));

        bwDomain.write(writer.writeDomain(planningInstance._1()));
        bwProblem.write(writer.writeProblem(planningInstance._1(), planningInstance._2()));

        bwDomain.close();
        bwProblem.close();
    }

    public static void writeHDDLToFiles(Tuple2<Domain, Plan> planningInstance, String domainFileName, String problemFileName) throws IOException {
        HDDLWriter writer = HDDLWriter.apply("someDomain", "someProblem");

        BufferedWriter bwDomain = new BufferedWriter(new FileWriter(domainFileName));
        BufferedWriter bwProblem = new BufferedWriter(new FileWriter(problemFileName));

        bwDomain.write(writer.writeDomain(planningInstance._1()));
        bwProblem.write(writer.writeProblem(planningInstance._1(), planningInstance._2()));

        bwDomain.close();
        bwProblem.close();
    }
}
