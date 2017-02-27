package de.uniulm.ki.util.problemGenerators;

import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.ioInterface.FileHandler;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import scala.Tuple2;

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
