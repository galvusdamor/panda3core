package de.uniulm.ki.panda3.parser.hddl;

import de.uniulm.ki.panda3.domain.Domain;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileReader;

/**
 * Created by dhoeller on 14.04.15.
 */
public class TestHddlVisitorParser {
    public static void main(String[] strs) {
        FileReader inDomain = null;
        FileReader inProblem = null;
        try {
            inDomain = new FileReader(System.getProperty("user.dir") + "/src/test/resources/testdomain.pddl");
            inProblem = new FileReader(System.getProperty("user.dir") + "/src/test/resources/testproblem.pddl");

            hddlLexer lDomain = new hddlLexer(new ANTLRInputStream(inDomain));
            hddlLexer lProblem = new hddlLexer(new ANTLRInputStream(inProblem));

            hddlParser pDomain = new hddlParser(new CommonTokenStream(lDomain));
            hddlParser pProblem = new hddlParser(new CommonTokenStream(lProblem));

            Domain d = new hddlPanda3Visitor().visitInstance(pDomain.domain(), pProblem.problem());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
