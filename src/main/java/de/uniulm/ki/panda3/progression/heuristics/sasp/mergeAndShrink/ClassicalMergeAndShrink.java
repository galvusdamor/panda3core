package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.util.Dot2PdfCompiler;
import de.uniulm.ki.util.EdgeLabelledGraph;
import de.uniulm.ki.util.EdgeLabelledGraphSingle;
import scala.Tuple2;
import scala.Tuple3;


import java.util.*;


public class ClassicalMergeAndShrink extends SasHeuristic {


    public ClassicalMergeAndShrink(SasPlusProblem p) {


        EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> graph = getMultiGraphUntilVarID(p, 4);

        printMultiGraph(p, graph, "graph.pdf");


        System.exit(0);


        SingleGraphMethods.printSingleGraphForVarIndex(p, 0, "graph.pdf");

        System.exit(0);






        EdgeLabelledGraphSingle<Integer,Integer> singleGraph = SingleGraphMethods.getSingleGraph(p, 0);
        EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> multiGraph = SingleGraphMethods.convertSingleGraphToMultiGraph(singleGraph);

        EdgeLabelledGraphSingle<Integer,Integer> singleGraph2 = SingleGraphMethods.getSingleGraph(p, 1);

        EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> newMultiGraph = mergingStep(p, multiGraph, singleGraph2);

        printMultiGraph(p, newMultiGraph, "graph.pdf");



        System.exit(0);


        //printSingleGraphForVarIndex(p, 3, "graph.pdf");

        //printMultiGraphForVarIndexes(p, 0, 1, "graph.pdf");


        singleGraph = SingleGraphMethods.getSingleGraph(p, 1);
        multiGraph = SingleGraphMethods.convertSingleGraphToMultiGraph(singleGraph);

        printMultiGraph(p, multiGraph, "graph.pdf");


        System.exit(0);


        ArrayList<Integer> varIndexes = new ArrayList<>();
        varIndexes.add(0);
        varIndexes.add(1);

        Tuple3<Integer[],Tuple3[],HashMap<Integer, ArrayList<Integer>>> multiGraphData = getNodesAndEdgesForVarIndexes(p,varIndexes);
        Integer[] nodeIDS = multiGraphData._3().keySet().toArray(new Integer[multiGraphData._3().keySet().size()]);

        for (Tuple3 t : multiGraphData._2()) {

            //  System.out.println(t);
        }

        EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> multiGraph2 = new EdgeLabelledGraph(nodeIDS, multiGraphData._2(),multiGraphData._3());

        EdgeLabelledGraph<String,String,HashMap<Integer, ArrayList<Integer>>> stringMultiGraph = convertMultiGraphToStringGraph(p, multiGraph2);

        Dot2PdfCompiler.writeDotToFile(stringMultiGraph,"graph.pdf");

        System.out.println(multiGraphData._2().length);

        System.out.println("test");

        System.exit(0);


        System.exit(0);


        HashMap<Integer, ArrayList<Integer>> mapping = new HashMap<>();

        ArrayList<Integer> first = new ArrayList<>();

        first.add(0);
        first.add(1);
        first.add(2);

        ArrayList<Integer> second = new ArrayList<>();

        second.add(3);
        second.add(4);

        mapping.put(0, first);
        mapping.put(1, second);


        EdgeLabelledGraph<Integer,Integer,HashMap> multiGraph3 = new EdgeLabelledGraph<>(multiGraphData._1(), multiGraphData._2(), mapping);

        System.out.println(multiGraph3.idMapping());


        //System.out.println("test");


        System.exit(0);


    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        return 0;
    }


    public ArrayList<Tuple3<Integer,Integer,Integer>> getEdgesForAllContainedIndexes(SasPlusProblem p, HashMap<Integer, ArrayList<Integer>> idMapping, ArrayList<Integer> containedVarIndexes){

        ArrayList<Tuple3<Integer,Integer,Integer>> edges = new ArrayList<>();

        for (int i=0; i<p.numOfOperators; i++){
            ArrayList<Tuple3<Integer,Integer,Integer>> edgesForOp = SingleGraphMethods.getEdgesForOpSingle(p, i, containedVarIndexes);
            edges.addAll(edgesForOp);
        }

        //System.out.println(edges.size());




        ArrayList<Tuple3<Integer,Integer,Integer>> multiEdges = SingleGraphMethods.convertSingleEdgesToMultiEdges(idMapping, edges);

        //System.out.println(multiEdges.size());

        /*for (Tuple3<Integer,Integer,Integer> e : multiEdges) {
            System.out.println(e);
        }*/

        return multiEdges;
    }










    public ArrayList<Tuple3<Integer,Integer,Integer>> getEdgesForOp(SasPlusProblem p, int OpIndex, ArrayList<Integer> containedVarIndexes, HashMap<Integer, ArrayList<Integer>> idMapping){

        int[] pres = p.precLists[OpIndex];
        int[] adds = p.addLists[OpIndex];
        int[] dels = p.delLists[OpIndex];

        //Schritt 1: OpIndexes in den pres, adds und dels aussortieren, die nicht in den containedVarIndexes sind

        //Schritt 2:
        //1) von allen dels zu allen adds
        //2) self-loops von allen bei denens weder in dels noch in adds ist
        //3) self-loops von allen, bei denens in beiden ist (ist bereits in Punkt 1 enthalten)


        ArrayList<Tuple3<Integer,Integer,Integer>> edges = new ArrayList<>();


        //Schritt 1:

        ArrayList<Integer> preListDismissed = Utils.dismissNotContainedIndexes(pres, containedVarIndexes);
        ArrayList<Integer> addListDismissed = Utils.dismissNotContainedIndexes(adds, containedVarIndexes);
        ArrayList<Integer> delListDismissed = Utils.dismissNotContainedIndexes(dels, containedVarIndexes);

        for (int id : idMapping.keySet()){

            ArrayList<Integer> assignedVarIndexes = idMapping.get(id);

            ArrayList<Integer> assignedPreListDismissed = Utils.dismissNotContainedIndexes(pres, assignedVarIndexes);
            ArrayList<Integer> assignedAddListDismissed = Utils.dismissNotContainedIndexes(adds, assignedVarIndexes);
            ArrayList<Integer> assignedDelListDismissed = Utils.dismissNotContainedIndexes(dels, assignedVarIndexes);

        }


/*

        for (int startEdge : delListDismissed){

            for (int endEdge : addListDismissed){


                //String labelEdge = "\"" + p.opNames[OpIndex] + "\"";

                Tuple3<Integer,Integer,Integer> edge = new Tuple3<>(startEdge,OpIndex,endEdge);
                if (!Utils.containsEdge(edges, edge)) {
                    edges.add(edge);
                }

            }

        }*/




        //Schritt 2:


/*
        for (int index : containedVarIndexes){
            //Zusätzlich abklären?: nicht in pres enthalten
            if (!addListDismissed.contains(index) && !delListDismissed.contains(index)){
                Tuple3<Integer,Integer,Integer> edge = new Tuple3<>(index,OpIndex,index);
                if (!Utils.containsEdge(edges, edge)) edges.add(edge);
            }
        }
*/


        return edges;

    }






    public Tuple3[] convertEdgesToStrings(SasPlusProblem p, Tuple3<Integer,Integer,Integer>[] oldEdges, HashMap<Integer, ArrayList<Integer>> idMapping){


        ArrayList<Tuple3<String,String,String>> newEdges = new ArrayList<>();

        Map<Integer,ArrayList<Integer>> selfLoops = new HashMap<>();

        for (Tuple3<Integer,Integer,Integer> oldEdge : oldEdges){



            if (oldEdge._1() != oldEdge._3()) {


                //no self-loop

                String startEdge = Utils.getMultiIDString(p, oldEdge._1(), idMapping);
                //                      "\"" + oldEdge._1() + ": "  + p.factStrs[oldEdge._1()] + "\"";
                String endEdge = Utils.getMultiIDString(p, oldEdge._3(), idMapping);
                //              "\"" + oldEdge._3() + ": "  + p.factStrs[oldEdge._3()] + "\"";
                String labelEdge = SingleGraphMethods.getOpString(p,oldEdge._2());
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

        for (int nodeID: selfLoops.keySet()){

            String varString = Utils.getMultiIDString(p, nodeID, idMapping);
            String labelEdge = "\"" + selfLoops.get(nodeID) + "\"";

            Tuple3<String, String, String> newEdge = new Tuple3<>(varString,labelEdge,varString);
            newEdges.add(newEdge);

        }

        Tuple3[] newEdgeArray = new Tuple3[newEdges.size()];
        for (int i=0; i<newEdges.size(); i++){
            newEdgeArray[i] = newEdges.get(i);
        }



        return newEdgeArray;
    }





    public static String[] convertNodesToStrings(SasPlusProblem p, HashMap<Integer, ArrayList<Integer>> idMapping){


        String[] newNodes = new String[idMapping.size()];

        Integer[] mappingKeys = idMapping.keySet().toArray(new Integer[idMapping.keySet().size()]);


        for (int i=0; i<newNodes.length; i++){
            newNodes[i] = Utils.getMultiIDString(p, mappingKeys[i], idMapping);
            //"\"" + containedIndexes[i] + ": " + p.factStrs[containedIndexes[i]] + "\"";
        }

        return newNodes;
    }



    public EdgeLabelledGraph<String,String,HashMap<Integer, ArrayList<Integer>>> convertMultiGraphToStringGraph(SasPlusProblem p, EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> graph){


        HashMap<Integer, ArrayList<Integer>> idMapping = graph.idMapping();

        String[] newNodes = convertNodesToStrings(p, idMapping);
        Tuple3[] newEdges = convertEdgesToStrings(p, graph.labelledEdges(), idMapping);


        EdgeLabelledGraph<String,String,HashMap<Integer, ArrayList<Integer>>> newGraph = new EdgeLabelledGraph<>(newNodes, newEdges, idMapping);


        return newGraph;
    }




    public Tuple3<Integer[],Tuple3[],HashMap<Integer, ArrayList<Integer>>> getNodesAndEdgesForVarIndexes(SasPlusProblem p, ArrayList<Integer> varIndexes){

        ArrayList<ArrayList<Integer>> factIndexesByVar = new ArrayList<>();

        ArrayList<Integer> containedIndexes = new ArrayList<>();

        for (int i : varIndexes){

            int firstIndex = p.firstIndex[i];
            int lastIndex = p.lastIndex[i];

            ArrayList<Integer> factIndexesForOneVar = new ArrayList<>();

            for (int j=firstIndex; j<=lastIndex; j++){
                containedIndexes.add(j);
                factIndexesForOneVar.add(j);
            }

            factIndexesByVar.add(factIndexesForOneVar);

        }


        HashMap<Integer, ArrayList<Integer>> idMapping = new HashMap<>();

        int id = 0;

        List<Integer> a = factIndexesByVar.get(0);
        List<Integer> b = factIndexesByVar.get(1);

        Integer[][] combinations = a.stream().flatMap(ai -> b.stream().map(bi -> new Integer[] { ai, bi })).toArray(Integer[][]::new);


        for (int i=0; i<combinations.length; i++){

            ArrayList<Integer> combi = new ArrayList<Integer>();
            for (int j: combinations[i]){
                combi.add(j);
            }


            idMapping.put(i, combi);

        }


/*
        for (int i=0; i<factIndexesByVar.size(); i++){


            ArrayList<Integer> oneList =

            ArrayList<Integer> map = new ArrayList<>();
            map.add(containedIndexes.get(i));

            idMapping.put(i, map);

        }
*/





/*
        HashMap<Integer, ArrayList<Integer>> idMapping2 = new HashMap<>();

        for (int i=0; i<containedIndexes.size(); i++){

            ArrayList<Integer> map = new ArrayList<>();
            map.add(containedIndexes.get(i));

            idMapping2.put(i, map);

        }*/


        ArrayList<Tuple3<Integer,Integer,Integer>> edges = getEdgesForAllContainedIndexes(p,idMapping,containedIndexes);



        //System.out.println(edges3);

        Tuple3[] edgeArray = new Tuple3[edges.size()];
        for (int i=0; i<edges.size(); i++){
            edgeArray[i] = edges.get(i);
        }


        Integer[] containedIndexesArray = containedIndexes.toArray(new Integer[containedIndexes.size()]);


        return new Tuple3<>(containedIndexesArray, edgeArray, idMapping);
    }









    public void printMultiGraphForVarIndexes(SasPlusProblem p, int varIndex1, int varIndex2, String outputfile){
        ArrayList<Integer> varIndexes = new ArrayList<>();
        varIndexes.add(varIndex1);
        varIndexes.add(varIndex2);

        Tuple3<Integer[],Tuple3[],HashMap<Integer, ArrayList<Integer>>> multiGraphData = getNodesAndEdgesForVarIndexes(p,varIndexes);
        Integer[] nodeIDS = multiGraphData._3().keySet().toArray(new Integer[multiGraphData._3().keySet().size()]);

        /*for (Tuple3 t : multiGraphData._2()) {

              System.out.println(t);
        }*/

        EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> multiGraph = new EdgeLabelledGraph(nodeIDS, multiGraphData._2(),multiGraphData._3());

        EdgeLabelledGraph<String,String,HashMap<Integer, ArrayList<Integer>>> stringMultiGraph = convertMultiGraphToStringGraph(p, multiGraph);

        // stringMultiGraph.dotString = Utils.eliminateDoubleRows(stringMultiGraph.dotString());




        //System.out.println(stringMultiGraph.dotString());

        //Dot2PdfCompiler.writeDotToFile(Utils.eliminateDoubleRows(stringMultiGraph.dotString()), outputfile);

        Dot2PdfCompiler.writeDotToFile(stringMultiGraph, outputfile);

        //System.out.println(Utils.eliminateDoubleRows(stringMultiGraph.dotString()));

        //System.out.println(stringMultiGraph.dotString().length());

        //System.out.println(Utils.eliminateDoubleRows(stringMultiGraph.dotString()).length());

    }

    public void printMultiGraph(SasPlusProblem p, EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> multiGraph, String outputfile){


        EdgeLabelledGraph<String,String,HashMap<Integer, ArrayList<Integer>>> stringMultiGraph = convertMultiGraphToStringGraph(p, multiGraph);

        Dot2PdfCompiler.writeDotToFile(stringMultiGraph, outputfile);

    }








    public EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> mergingStep(SasPlusProblem p, EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> multiGraph, EdgeLabelledGraphSingle<Integer,Integer> singleGraph){

        Integer[] multiNodes = (Integer[]) multiGraph.arrayVertices();

        Tuple3<Integer,Integer,Integer>[] multiEdges = multiGraph.labelledEdges();


        Tuple3<Integer,Integer,Integer>[] singleEdges = singleGraph.labelledEdges();

        Integer[] singleNodes = (Integer[]) singleGraph.arrayVertices();


        ArrayList<Tuple3<Integer,Integer,Integer>> newMultiEdges = new ArrayList<>();

        HashMap<Integer,Tuple2<Integer,Integer>> tempIdMapping = new HashMap<>();

        HashMap<Tuple2<Integer,Integer>,Integer> tempReverseIdMapping = new HashMap<>();

        List<Integer> a = Arrays.asList(multiNodes);

        List<Integer> b = Arrays.asList(singleNodes);

        Integer[][] combinations = a.stream().flatMap(ai -> b.stream().map(bi -> new Integer[] { ai, bi })).toArray(Integer[][]::new);

        for (int i=0; i<combinations.length; i++){

            Tuple2<Integer,Integer> combi = new Tuple2<>(combinations[i][0],combinations[i][1]);

            tempIdMapping.put(i,combi);
            tempReverseIdMapping.put(combi, i);
        }

/*        System.out.println(tempIdMapping);

        System.out.println(tempReverseIdMapping);

        Tuple2<Integer,Integer> testCombi = new Tuple2<>(2,5);

        System.out.println(tempReverseIdMapping.get(testCombi));*/


        //System.out.println("Multis: " + getEdgesOfOpId(multiEdges, 4));

        //System.out.println("Singles: " + getEdgesOfOpId(singleEdges, 4));


        for (int opID=0; opID<p.numOfOperators; opID++){

            ArrayList<Tuple3<Integer,Integer,Integer>> tempMultiEdges = new ArrayList<>();

            for (Tuple3<Integer,Integer,Integer> multiEdge : multiEdges){

                if (multiEdge._2() == opID) {

                    for (Tuple3<Integer, Integer, Integer> singleEdge : singleEdges) {

                        if (singleEdge._2() == opID) {

                            Tuple2<Integer, Integer> startNodes = new Tuple2<>(multiEdge._1(), singleEdge._1());

                            Tuple2<Integer, Integer> endNodes = new Tuple2<>(multiEdge._3(), singleEdge._3());

                            int newStartID = tempReverseIdMapping.get(startNodes);

                            int newEndID = tempReverseIdMapping.get(endNodes);

                            Tuple3<Integer, Integer, Integer> newEdge = new Tuple3<>(newStartID, opID, newEndID);

                            tempMultiEdges.add(newEdge);

                        }

                    }
                }


            }

/*
            //add self-loops ???
            for (int nodeID : tempIdMapping.keySet()){


                if (isNotStartOrEndNodeOfAnyEdge(tempMultiEdges, nodeID)){

                    Tuple3<Integer, Integer, Integer> selfLoop = new Tuple3<>(nodeID, opID, nodeID);
                    tempMultiEdges.add(selfLoop);
                }

            }*/

            newMultiEdges.addAll(tempMultiEdges);


        }




        //System.out.println(newMultiEdges);


        HashMap<Integer,ArrayList<Integer>> newIdMapping = new HashMap<>();


        for (int i : tempIdMapping.keySet()){

            Tuple2<Integer, Integer> oldIDs = tempIdMapping.get(i);

            int oldMultiID = oldIDs._1();

            int oldSingleID = oldIDs._2();

            ArrayList<Integer> assignedFacts = new ArrayList<>(multiGraph.idMapping().get(oldMultiID));

            if (!assignedFacts.contains(oldSingleID)) assignedFacts.add(oldSingleID);

            newIdMapping.put(i, assignedFacts);

        }

        //System.out.println(tempReverseIdMapping.get(new Tuple2<>(8,10)));

        //System.out.println(tempIdMapping.get(53));

        Tuple3<Integer, Integer, Integer>[] newEdgeTuple = Utils.convertEdgeArrayListToTuple3(newMultiEdges);

        Integer[] nodeIDS = Utils.convertNodeIDArrayListToArray(newIdMapping);


        EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> newMultiGraph = new EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>>(nodeIDS, newEdgeTuple, newIdMapping);

        //System.out.println(newIdMapping);

        //System.out.println(newMultiEdges);

        return newMultiGraph;

    }

    public ArrayList<Tuple3<Integer,Integer,Integer>> getEdgesOfOpId(Tuple3<Integer,Integer,Integer>[] edges, int opID){

        ArrayList<Tuple3<Integer,Integer,Integer>> edgesOfOpID = new ArrayList<>();

        for (Tuple3<Integer,Integer,Integer> edge : edges){

            if (edge._2() == opID) edgesOfOpID.add(edge);
        }

        return edgesOfOpID;
    }


    public EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> getMultiGraphUntilVarID(SasPlusProblem p, int lastVarID){

        EdgeLabelledGraphSingle<Integer,Integer> singleGraph = SingleGraphMethods.getSingleGraph(p, 0);
        EdgeLabelledGraph<Integer,Integer,HashMap<Integer, ArrayList<Integer>>> multiGraph = SingleGraphMethods.convertSingleGraphToMultiGraph(singleGraph);

        for (int i=1; ((i<=lastVarID) && (i<p.numOfVars)); i++){

            singleGraph = SingleGraphMethods.getSingleGraph(p, i);

            multiGraph = mergingStep(p, multiGraph, singleGraph);

        }

        return multiGraph;

    }

    public boolean isNotStartOrEndNodeOfAnyEdge(ArrayList<Tuple3<Integer,Integer,Integer>> edges, int nodeID){

        boolean result = true;

        for (Tuple3<Integer,Integer,Integer> edge : edges){

            if ((edge._1() == nodeID) || (edge._1() == nodeID)) return false;
        }


        return result;

    }



}
