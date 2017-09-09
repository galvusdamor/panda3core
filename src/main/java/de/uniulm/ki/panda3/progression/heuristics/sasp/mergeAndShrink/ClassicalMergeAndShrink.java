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



public class ClassicalMergeAndShrink extends SasHeuristic {


    public ClassicalMergeAndShrink(SasPlusProblem p) {



/*        System.out.println(p.toString());
        String[] var0alt = p.values[0];
        for (String s : var0alt){
            System.out.println(s);
        }


        */

        int firstIndex3 = p.firstIndex[0];
        int lastIndex3 = p.lastIndex[0];

        ArrayList<Integer> containedIndexes3 = new ArrayList<>();

        for (int i=firstIndex3; i<=lastIndex3; i++){
            containedIndexes3.add(i);
        }

        ArrayList<Tuple3<Integer,Integer,Integer>> edges3 = getEdgesForAllContainedIndexes(p,containedIndexes3);



        System.out.println(edges3);

        Tuple3[] edgeArray3 = new Tuple3[edges3.size()];
        for (int i=0; i<edges3.size(); i++){
            edgeArray3[i] = edges3.get(i);
        }


        Integer[] containedIndexesArray3 = containedIndexes3.toArray(new Integer[containedIndexes3.size()]);


        String[] nodes = convertNodesToStrings(p, containedIndexes3);
        Tuple3[] kanten = convertEdgesToStrings(p, edges3);



        EdgeLabelledGraph<String,String> g3 = new EdgeLabelledGraph<String, String>(nodes, kanten);
        //EdgeLabelledGraph<String,String> g = new EdgeLabelledGraph<String,String>(var0, new Tuple3[]{t, t2});

        Dot2PdfCompiler.writeDotToFile(g3,"graph.pdf");




        System.exit(0);




        String[] var0 = getFactStrsForVarIndex(p,0);

        for (String s : var0){
            System.out.println(s);
        }

        for (int i=0; i<var0.length;i++){
            var0[i] = "\"" + var0[i] + "\"";
        }

        int firstIndex = p.firstIndex[0];
        int lastIndex = p.lastIndex[0];

        ArrayList<Integer> containedIndexes = new ArrayList<>();

        for (int i=firstIndex; i<=lastIndex; i++){
            containedIndexes.add(i);
        }

        ArrayList<Tuple3<String,String,String>> edges = getEdgesForOpOld(p,0,containedIndexes);



        System.out.println(edges);


        /*Tuple3<String,String,String> t = new Tuple3<>(var0[0],"\"bla b\"",var0[1]);
        Tuple3<String,String,String> t2 = new Tuple3<>(var0[1],"bla2",var0[2]);
        Tuple3[] edgeArray2 =new Tuple3[]{t, t2};

        System.out.println(edgeArray[0]);
        System.out.println(edgeArray2[0]);*/

        Tuple3<Integer,String,Integer> t = new Tuple3<>(0,"\"bla b\"",1);
        Tuple3<Integer,String,Integer> t2 = new Tuple3<>(1,"bla2",0);
        Tuple3[] edgeArray2 =new Tuple3[]{t, t2};

        System.out.println(edgeArray2[0]);

        Tuple3[] edgeArray = new Tuple3[edges.size()];
        for (int i=0; i<edges.size(); i++){
            edgeArray[i] = edges.get(i);
        }


        Integer[] containedIndexesArray = containedIndexes.toArray(new Integer[containedIndexes.size()]);

        EdgeLabelledGraph<Integer,Integer> g = new EdgeLabelledGraph<Integer, Integer>(containedIndexesArray, edgeArray2);
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
        
        
        //// TODO: 10.09.2017 Schritt 2 


        return edges;

    }

    public ArrayList<Tuple3<String,String,String>> getEdgesForOpOld(SasPlusProblem p, int OpIndex, ArrayList<Integer> containedVarIndexes){

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

        ArrayList<Tuple3<String,String,String>> edges = new ArrayList<Tuple3<String,String,String>>();

        for (int startEdge : delListDismissed){

            for (int goalEdge : addListDismissed){

                
                
            }

        }

        /*for (int i=firstIndex; i<= lastIndex; i++){

            boolean containsPre = Utils.contains(precs, i);
            boolean containsDel = Utils.contains(dels, i);
            boolean containsAdd = Utils.contains(adds, i);

            Tuple3<Boolean,Boolean,Boolean> contains = new Tuple3<>(containsPre, containsDel, containsAdd);

            if (contains.equals(new Tuple3<>(true, false, true))){

            }

            if (contains.equals(new Tuple3<>(false, true, false))){

            }

            if (contains.equals(new Tuple3<>(false, false, false))){

            }

            if (contains.equals(new Tuple3<>(true, false, false))){

            }

            if (contains.equals(new Tuple3<>(false, false, true))){

            }

            if (contains.equals(new Tuple3<>(true, true, false))){

            }

            if (contains.equals(new Tuple3<>(false, true, true))){

            }

            if (contains.equals(new Tuple3<>(true, true, true))){

            }


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
        }*/




        return edges;

    }

    public ArrayList<Integer> dismissNotContainedIndexes(int[] allIndexes, ArrayList<Integer> containedIndexes){

        ArrayList<Integer> result = new ArrayList<>();

        for (int i : allIndexes){
            if (containedIndexes.contains(i)) result.add(i);
        }

        return result;
    }

    public Tuple3[] convertEdgesToStrings(SasPlusProblem p, ArrayList<Tuple3<Integer,Integer,Integer>> oldEdges){

        ArrayList<Tuple3<String,String,String>> newEdges = new ArrayList<>();

        for (Tuple3<Integer,Integer,Integer> oldEdge : oldEdges){

            String startEdge = "\"" + p.factStrs[oldEdge._1()] + "\"";
            String endEdge = "\"" + p.factStrs[oldEdge._3()] + "\"";
            String labelEdge = "\"" + p.opNames[oldEdge._2()] + "\"";

            Tuple3<String,String,String> newEdge = new Tuple3<>(startEdge,labelEdge,endEdge);
            newEdges.add(newEdge);


        }

        Tuple3[] newEdgeArray = new Tuple3[newEdges.size()];
        for (int i=0; i<newEdges.size(); i++){
            newEdgeArray[i] = newEdges.get(i);
        }

        return newEdgeArray;
    }


    public String[] convertNodesToStrings(SasPlusProblem p, ArrayList<Integer> containedIndexes){

        String[] newNodes = new String[containedIndexes.size()];

        for (int i=0; i<newNodes.length; i++){
            newNodes[i] = "\"" + p.factStrs[containedIndexes.get(i)] + "\"";
        }

        return newNodes;
    }

}
