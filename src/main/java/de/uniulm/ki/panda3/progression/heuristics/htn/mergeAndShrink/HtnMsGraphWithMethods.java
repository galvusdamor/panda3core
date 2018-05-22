package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.Utils;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.util.EdgeLabelledGraph;
import scala.Tuple3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class HtnMsGraphWithMethods extends   HtnMsGraph {



    public HtnMsGraphWithMethods(Integer[] nodeIDS, Tuple3<Integer, Integer, Integer>[] edges, HashMap<Integer, NodeValue> IDMapping, int startNodeID){



        graph = new EdgeLabelledGraph<>(nodeIDS, edges, IDMapping, startNodeID);

        this.startNodeID = startNodeID;
        this.idMapping = IDMapping;

        arrayVertices = (Integer[]) graph.arrayVertices();

        labelledEdges = graph.labelledEdges();


    }




}

class TemporaryHtnMsGraphWithMethods extends TemporaryHtnMsGraph{


    public TemporaryHtnMsGraphWithMethods(LinkedList<Tuple3<Integer, Integer, Integer>> edges, HashMap<Integer, NodeValue> idMapping, int startNodeID){

        this.startNodeID = startNodeID;
        this.edges = edges;
        this.idMapping = idMapping;

    }

    public HtnMsGraph convertToHtnMsGraph(){

        Integer[] nodeIDs = new Integer[idMapping.keySet().size()];
        ArrayList<Integer> nodeIDList = new ArrayList<>();
        nodeIDList.addAll(idMapping.keySet());

        for (int i=0; i<nodeIDList.size(); i++){
            nodeIDs[i] = nodeIDList.get(i);
        }

        ArrayList<Tuple3<Integer, Integer, Integer>> edgeArrayList = new ArrayList<>();
        edgeArrayList.addAll(edges);

        Tuple3<Integer,Integer,Integer>[] edgeArray = Utils.convertEdgeArrayListToTuple3(edgeArrayList);


        HtnMsGraph htnMsGraph = new HtnMsGraphWithMethods(nodeIDs, edgeArray, idMapping, startNodeID);

        return htnMsGraph;
    }



}