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

package de.uniulm.ki.panda3.problemGenerators.pcpGenerator;

import de.uniulm.ki.panda3.problemGenerators.cfgIntersectionGenerator.CfGrammar;
import de.uniulm.ki.panda3.problemGenerators.cfgIntersectionGenerator.CfGrammarIntersectionToHTN;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.ioInterface.FileHandler;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import scala.Tuple2;

 // https://www8.cs.umu.se/kurser/5DV037/VT14/material/12-lecture.pdf
 // http://wimhesselink.nl/pub/whh471.pdf

/**
 * Created by dh on 22.02.17.
 */
public class PostCorrespondenceProblemToHTN {
    public static void main(String[] args) throws Exception {
        if (args.length != 3){
            System.out.println("Please provide an input definition and two files for the output (that will be overwritten!)." +
                    "\n program in.txt out-domain.txt out-problem.txt");
            return;
        }


        PostCorrespondenceProblem pkp = new PostCorrespondenceProblem(args[0]);
        pkp.createGrammar();
        CfGrammar g1 = pkp.grammar1();
        CfGrammar g2 = pkp.grammar2();

        Tuple2<Domain, Plan> prob = CfGrammarIntersectionToHTN.grammerInterProb(g1, g2);
        FileHandler.writeHDDLToFiles(prob, args[1], args[2]);
    }
}
