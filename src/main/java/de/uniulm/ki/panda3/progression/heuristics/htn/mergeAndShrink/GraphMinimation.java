package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.Utils;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import scala.Tuple2;
import scala.Tuple3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public final class GraphMinimation {


    public static HtnMsGraph minimizeGraph(SasPlusProblem p, HtnMsGraph graph){


        //System.out.println("start minimation.");

        graph = shrinkSameIncomingOrOutgoingEdges(p, graph);


        return graph;

    }


    public static HtnMsGraph shrinkSameIncomingOrOutgoingEdges(SasPlusProblem p, HtnMsGraph graph){


        int index=0;
        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> incomingEdgesMap = getIDToIncomingOrOutgoingEdgesMap(graph, true);
        HashSet<HashSet<Integer>> idsToShrinkByIncomingEdges = getIDsToShrink(incomingEdgesMap, true);

        //String outputfile = "MinimizedGraph" + index + ".pdf";
        //Utils.printHtnGraph(p, graph, outputfile);

        graph = Shrinking.shrinkingStep(p, graph, idsToShrinkByIncomingEdges);

        index++;
        //outputfile = "MinimizedGraph" + index + ".pdf";
        //Utils.printHtnGraph(p, graph, outputfile);

        //System.out.println("To Shrink by incoming edges: " + idsToShrinkByIncomingEdges);


        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> outgoingEdgesMap = getIDToIncomingOrOutgoingEdgesMap(graph, false);
        HashSet<HashSet<Integer>> idsToShrinkByOutgoingEdges = getIDsToShrink(outgoingEdgesMap, false);

        graph = Shrinking.shrinkingStep(p, graph, idsToShrinkByOutgoingEdges);

        index++;
        //outputfile = "MinimizedGraph" + index + ".pdf";
        //Utils.printHtnGraph(p, graph, outputfile);

        //System.out.println("To Shrink by outgoing edges: " + idsToShrinkByOutgoingEdges);


        while (true){

            incomingEdgesMap = getIDToIncomingOrOutgoingEdgesMap(graph, true);
            idsToShrinkByIncomingEdges = getIDsToShrink(incomingEdgesMap, true);

            //if (idsToShrinkByIncomingEdges.size()==0) break;


            //System.out.println("To Shrink by incoming edges: " + idsToShrinkByIncomingEdges);

            graph = Shrinking.shrinkingStep(p, graph, idsToShrinkByIncomingEdges);

            index++;
            //outputfile = "MinimizedGraph" + index + ".pdf";
            //Utils.printHtnGraph(p, graph, outputfile);


            outgoingEdgesMap = getIDToIncomingOrOutgoingEdgesMap(graph, false);
            idsToShrinkByOutgoingEdges = getIDsToShrink(outgoingEdgesMap, false);

            if ((idsToShrinkByIncomingEdges.size()==0) &&(idsToShrinkByOutgoingEdges.size()==0)) break;
            //if (idsToShrinkByOutgoingEdges.size()==0) break;



            //System.out.println("To Shrink by outgoing edges: " + idsToShrinkByOutgoingEdges);

            graph = Shrinking.shrinkingStep(p, graph, idsToShrinkByOutgoingEdges);

            index++;
            //outputfile = "MinimizedGraph" + index + ".pdf";
            //Utils.printHtnGraph(p, graph, outputfile);


        }





        return graph;

    }



    public static HashSet<HashSet<Integer>> getIDsToShrink(HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> edgesMap, Boolean incomingEdges){


        HashMap<HashSet<Tuple2<Integer,Integer>>, HashSet<Integer>> incomingReducedNodesToNodeIDsMap = new HashMap<>();

        for (int id: edgesMap.keySet()){

            ArrayList<Tuple3<Integer, Integer, Integer>> edges1 = edgesMap.get(id);

            HashSet<Tuple2<Integer,Integer>> reducedEdges = new HashSet<>();

            for (Tuple3<Integer, Integer, Integer> edge : edges1){
                Tuple2<Integer,Integer> reducedEdge;

                if(incomingEdges==true) {
                    reducedEdge=new Tuple2<>(edge._1(), edge._2());
                }else{
                    reducedEdge=new Tuple2<>(edge._3(), edge._2());
                }
                reducedEdges.add(reducedEdge);
            }

            HashSet<Integer> matchingReducedNodes = incomingReducedNodesToNodeIDsMap.get(reducedEdges);

            if (matchingReducedNodes==null){

                HashSet<Integer> ids = new HashSet<>();
                ids.add(id);
                incomingReducedNodesToNodeIDsMap.put(reducedEdges, ids);

            }else{
                matchingReducedNodes.add(id);
            }

        }

        //System.out.println(incomingReducedNodesToNodeIDsMap);

        HashSet<HashSet<Integer>> idsToShrink = new HashSet<>();

        for (HashSet<Integer> sameIDs : incomingReducedNodesToNodeIDsMap.values()){

            if(sameIDs.size()>1){

                idsToShrink.add(sameIDs);
            }

        }

        return idsToShrink;

    }



    public static Tuple2<HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>>, HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>>> getIDToIncomingAndOutgoingEdgesMap(TemporaryHtnMsGraph graph){

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> incomingEdgesMap = new HashMap<>();
        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> outgoingEdgesMap = new HashMap<>();

        for (int i: graph.idMapping.keySet()){
            ArrayList<Tuple3<Integer, Integer, Integer>> incomingEdges = new ArrayList<>();
            incomingEdgesMap.put(i,incomingEdges);

            ArrayList<Tuple3<Integer, Integer, Integer>> outgoingEdges = new ArrayList<>();
            outgoingEdgesMap.put(i,outgoingEdges);
        }

        for (Tuple3<Integer, Integer, Integer> edge : graph.edges){

            incomingEdgesMap.get(edge._3()).add(edge);
            outgoingEdgesMap.get(edge._1()).add(edge);

        }

        Tuple2<HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>>, HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>>> result = new Tuple2<>(incomingEdgesMap, outgoingEdgesMap);

        return result;

    }

    public static HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> getIDToIncomingOrOutgoingEdgesMap(HtnMsGraph graph, Boolean isIncoming){

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> edgesMap = new HashMap<>();

        for (int i: graph.idMapping.keySet()){
            ArrayList<Tuple3<Integer, Integer, Integer>> incomingEdges = new ArrayList<>();
            edgesMap.put(i,incomingEdges);
        }

        for (Tuple3<Integer, Integer, Integer> edge : graph.labelledEdges){


            if (isIncoming){
                edgesMap.get(edge._3()).add(edge);
            }else {
                edgesMap.get(edge._1()).add(edge);
            }

        }



        return edgesMap;

    }



}
