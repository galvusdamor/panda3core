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
