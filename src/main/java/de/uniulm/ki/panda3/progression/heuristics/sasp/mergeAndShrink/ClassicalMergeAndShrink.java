package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntStack;
import de.uniulm.ki.util.DirectedGraph;
import de.uniulm.ki.util.Dot2PdfCompiler;
import de.uniulm.ki.util.EdgeLabelledGraph;
import de.uniulm.ki.util.SimpleDirectedGraph;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.JavaConversions;


import java.util.*;
import java.util.stream.IntStream;



public class ClassicalMergeAndShrink extends SasHeuristic {


    public ClassicalMergeAndShrink(SasPlusProblem p) {


        Tuple2<Integer[],Tuple3[]> graphData = getNodesAndEdgesForVarIndex(p,0);

        EdgeLabelledGraph<Integer,Integer> graph = new EdgeLabelledGraph<>(graphData._1(), graphData._2());

        EdgeLabelledGraph<String,String> stringGraph = convertGraphToStringGraph(p, graph);

        Dot2PdfCompiler.writeDotToFile(stringGraph,"graph.pdf");


        //System.out.println("test");


        System.exit(0);


    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        return 0;
    }


    public ArrayList<Tuple3<Integer,Integer,Integer>> getEdgesForAllContainedIndexes(SasPlusProblem p, ArrayList<Integer> containedVarIndexes){

        ArrayList<Tuple3<Integer,Integer,Integer>> edges = new ArrayList<>();

        for (int i=0; i<p.numOfOperators; i++){
            ArrayList<Tuple3<Integer,Integer,Integer>> edgesForOp = getEdgesForOp(p, i, containedVarIndexes);
            edges.addAll(edgesForOp);
        }

        return edges;
    }

    public ArrayList<Tuple3<Integer,Integer,Integer>> getEdgesForOp(SasPlusProblem p, int OpIndex, ArrayList<Integer> containedVarIndexes){

        int[] pres = p.precLists[OpIndex];
        int[] adds = p.addLists[OpIndex];
        int[] dels = p.delLists[OpIndex];

        //Schritt 1: OpIndexes in den pres, adds und dels aussortieren, die nicht in den containedVarIndexes sind

        //Schritt 2:
        //1) von allen dels zu allen adds
        //2) self-loops von allen bei denens weder in dels noch in adds ist
        //3) self-loops von allen, bei denens in beiden ist (ist bereits in Punkt 1 enthalten)
        


        //Schritt 1:

        ArrayList<Integer> preListDismissed = dismissNotContainedIndexes(pres, containedVarIndexes);
        ArrayList<Integer> addListDismissed = dismissNotContainedIndexes(adds, containedVarIndexes);
        ArrayList<Integer> delListDismissed = dismissNotContainedIndexes(dels, containedVarIndexes);

        ArrayList<Tuple3<Integer,Integer,Integer>> edges = new ArrayList<>();

        for (int startEdge : delListDismissed){

            for (int endEdge : addListDismissed){


                //String labelEdge = "\"" + p.opNames[OpIndex] + "\"";

                Tuple3<Integer,Integer,Integer> edge = new Tuple3<>(startEdge,OpIndex,endEdge);
                edges.add(edge);

            }

        }

        
        //Schritt 2:


        for (int index : containedVarIndexes){
            //Zusätzlich abklären?: nicht in pres enthalten
            if (!addListDismissed.contains(index) && !delListDismissed.contains(index)){
                Tuple3<Integer,Integer,Integer> edge = new Tuple3<>(index,OpIndex,index);
                edges.add(edge);
            }
        }


        return edges;

    }


    public ArrayList<Integer> dismissNotContainedIndexes(int[] allIndexes, ArrayList<Integer> containedIndexes){

        ArrayList<Integer> result = new ArrayList<>();

        for (int i : allIndexes){
            if (containedIndexes.contains(i)) result.add(i);
        }

        return result;
    }

    public Tuple3[] convertEdgesToStrings(SasPlusProblem p, Tuple3<Integer,Integer,Integer>[] oldEdges){


        ArrayList<Tuple3<String,String,String>> newEdges = new ArrayList<>();

        Map<Integer,ArrayList<Integer>> selfLoops = new HashMap<>();

        for (Tuple3<Integer,Integer,Integer> oldEdge : oldEdges){



            if (oldEdge._1() != oldEdge._3()) {


                //no self-loop

                String startEdge = getVarString(p, oldEdge._1());
  //                      "\"" + oldEdge._1() + ": "  + p.factStrs[oldEdge._1()] + "\"";
                String endEdge = getVarString(p, oldEdge._3());
  //              "\"" + oldEdge._3() + ": "  + p.factStrs[oldEdge._3()] + "\"";
                String labelEdge = getOpString(p,oldEdge._2());
                        //"\"" + oldEdge._2() + ": " + p.opNames[oldEdge._2()] +  "\"";

                Tuple3<String, String, String> newEdge = new Tuple3<>(startEdge, labelEdge, endEdge);
                newEdges.add(newEdge);

            }else{

                int varIndex = oldEdge._1();
                if (selfLoops.containsKey(varIndex)){

                    ArrayList<Integer> selfLoop = selfLoops.get(varIndex);
                    selfLoop.add(oldEdge._2());


                }else{
                    ArrayList<Integer> selfLoop = new ArrayList<>();
                    selfLoop.add(oldEdge._2());
                    selfLoops.put(varIndex,selfLoop);
                }

            }
        }

        for (int varIndex: selfLoops.keySet()){

            String varString = getVarString(p, varIndex);
            String labelEdge = "\"" + selfLoops.get(varIndex) + "\"";

            Tuple3<String, String, String> newEdge = new Tuple3<>(varString,labelEdge,varString);
            newEdges.add(newEdge);

        }

        Tuple3[] newEdgeArray = new Tuple3[newEdges.size()];
        for (int i=0; i<newEdges.size(); i++){
            newEdgeArray[i] = newEdges.get(i);
        }



        return newEdgeArray;
    }




    public String[] convertNodesToStrings(SasPlusProblem p, Integer[] containedIndexes){

        String[] newNodes = new String[containedIndexes.length];

        for (int i=0; i<newNodes.length; i++){
            newNodes[i] = getVarString(p, containedIndexes[i]);
                    //"\"" + containedIndexes[i] + ": " + p.factStrs[containedIndexes[i]] + "\"";
        }

        return newNodes;
    }

    public EdgeLabelledGraph<String,String> convertGraphToStringGraph(SasPlusProblem p, EdgeLabelledGraph<Integer,Integer> graph){

        String[] newNodes = convertNodesToStrings(p, (Integer[]) graph.arrayVertices());
        Tuple3[] newEdges = convertEdgesToStrings(p, graph.labelledEdges());

        EdgeLabelledGraph<String,String> newGraph = new EdgeLabelledGraph<>(newNodes, newEdges);


        return newGraph;
    }

    public Tuple2<Integer[],Tuple3[]> getNodesAndEdgesForVarIndex(SasPlusProblem p, int varIndex){

        int firstIndex = p.firstIndex[varIndex];
        int lastIndex = p.lastIndex[varIndex];

        ArrayList<Integer> containedIndexes = new ArrayList<>();

        for (int i=firstIndex; i<=lastIndex; i++){
            containedIndexes.add(i);
        }

        ArrayList<Tuple3<Integer,Integer,Integer>> edges = getEdgesForAllContainedIndexes(p,containedIndexes);



        //System.out.println(edges3);

        Tuple3[] edgeArray = new Tuple3[edges.size()];
        for (int i=0; i<edges.size(); i++){
            edgeArray[i] = edges.get(i);
        }


        Integer[] containedIndexesArray = containedIndexes.toArray(new Integer[containedIndexes.size()]);


        return new Tuple2<Integer[],Tuple3[]>(containedIndexesArray, edgeArray);
    }

    public String getVarString(SasPlusProblem p, int varIndex){

        String s = "\"" + varIndex + ": "   + p.factStrs[varIndex] + "\"";

        return s;

    }

    public String getOpString(SasPlusProblem p, int OpIndex){

        String s = "\"" + OpIndex + ": " + p.opNames[OpIndex] +  "\"";

        return s;

    }

}
