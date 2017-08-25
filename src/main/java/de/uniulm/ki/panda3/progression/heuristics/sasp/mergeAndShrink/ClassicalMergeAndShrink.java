package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntStack;
import de.uniulm.ki.util.DirectedGraph;
import de.uniulm.ki.util.Dot2PdfCompiler;
import de.uniulm.ki.util.EdgeLabelledGraph;
import de.uniulm.ki.util.SimpleDirectedGraph;
import scala.Tuple3;

import java.util.*;

/**
 * Created by gregor on 25.08.17.
 */
public class ClassicalMergeAndShrink extends SasHeuristic {


    public ClassicalMergeAndShrink(SasPlusProblem p) {
        Tuple3<Integer,String,Integer> t = new Tuple3<>(1,"bla",3);

        EdgeLabelledGraph<Integer,String> g = new EdgeLabelledGraph<Integer,String>(new Integer[]{1,2,3}, new Tuple3[]{t});


        Dot2PdfCompiler.writeDotToFile(g,"graph.pdf");


        System.out.println(p.toString());
        System.out.println("Gregor war hier");
        System.exit(0);
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        return 0;
    }
}
