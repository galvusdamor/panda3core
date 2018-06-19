// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
