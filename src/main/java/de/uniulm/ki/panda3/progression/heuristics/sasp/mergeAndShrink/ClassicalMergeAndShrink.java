package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntStack;
import de.uniulm.ki.util.DirectedGraph;
import de.uniulm.ki.util.Dot2PdfCompiler;
import de.uniulm.ki.util.EdgeLabelledGraph;
import de.uniulm.ki.util.SimpleDirectedGraph;
import scala.Tuple3;
import scala.collection.JavaConversions;


import java.util.*;
import java.util.stream.IntStream;


/**
 * Created by gregor on 25.08.17.
 */
public class ClassicalMergeAndShrink extends SasHeuristic {


    public ClassicalMergeAndShrink(SasPlusProblem p) {



/*        System.out.println(p.toString());
        String[] var0alt = p.values[0];
        for (String s : var0alt){
            System.out.println(s);
        }


        */

        String[] var0 = getFactStrsForVarIndex(p,0);

        for (String s : var0){
            System.out.println(s);
        }

        for (int i=0; i<var0.length;i++){
            var0[i] = "\"" + var0[i] + "\"";
        }

        int firstIndex = p.firstIndex[0];
        int lastIndex = p.lastIndex[0];

        ArrayList<Tuple3<String,String,String>> edges = getEdgesForOp(p,0,firstIndex,lastIndex);



        System.out.println(edges);


        Tuple3<String,String,String> t = new Tuple3<>(var0[0],"\"bla b\"",var0[1]);
        Tuple3<String,String,String> t2 = new Tuple3<>(var0[1],"bla2",var0[2]);
        Tuple3[] edgeArray = new Tuple3[edges.size()];
        for (int i=0; i<edges.size(); i++){
            edgeArray[i] = edges.get(i);
        }

        Tuple3[] edgeArray2 =new Tuple3[]{t, t2};

        System.out.println(edgeArray[0]);
        System.out.println(edgeArray2[0]);

        EdgeLabelledGraph<String,String> g = new EdgeLabelledGraph<String,String>(var0, edgeArray);
        //EdgeLabelledGraph<String,String> g = new EdgeLabelledGraph<String,String>(var0, new Tuple3[]{t, t2});

        Dot2PdfCompiler.writeDotToFile(g,"graph.pdf");

         System.exit(0);
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        return 0;
    }

    public String[] getFactStrsForVarIndex(SasPlusProblem p, int index){

        int firstIndex = p.firstIndex[index];
        int lastIndex = p.lastIndex[index];
        int count = lastIndex - firstIndex;

        String[] factStr = new String[count+1];
        for (int i = 0; i <= count; i++){
            factStr[i] = p.factStrs[firstIndex + i];
        }

        return factStr;
    }

    public ArrayList<Tuple3<String,String,String>> getEdgesForOp(SasPlusProblem p, int OpIndex, int firstIndex, int lastIndex){

        int[] precs = p.precLists[OpIndex];
        int[] adds = p.addLists[OpIndex];
        int[] dels = p.delLists[OpIndex];

        ArrayList<Tuple3<String,String,String>> edges = new ArrayList<Tuple3<String,String,String>>();

        for (int i=firstIndex; i<= lastIndex; i++){

            if (Utils.contains(dels, i)){

                for (int j=firstIndex; j<= lastIndex; j++){

                    if (Utils.contains(adds, j)){

                        String startEdge = "\"" + p.factStrs[i] + "\"";
                        String endEdge = "\"" + p.factStrs[j] + "\"";
                        String labelEdge = "\"" + p.opNames[OpIndex] + "\"";

                        Tuple3<String,String,String> edge = new Tuple3<>(startEdge,labelEdge,endEdge);
                        edges.add(edge);
                    }
                }
            }
        }




        return edges;

    }
}
