package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.CascadingTables;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.HtnNodeValue;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.util.EdgeLabelledGraph;
import scala.Tuple3;

import java.util.HashMap;
import java.util.HashSet;

public class HtnMsGraph {


    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, CascadingTables> graph;

    public HashMap<Integer, NodeValue> idMapping;

    public int startNodeID;
    public CascadingTables cascadingTables;
    public Integer[] arrayVertices;
    public Tuple3<Integer, Integer, Integer>[] labelledEdges;


    public HtnMsGraph(Integer[] nodeIDS, Tuple3<Integer, Integer, Integer>[] edges, HashMap<Integer, NodeValue> IDMapping, int startNodeID
                      , CascadingTables cascadingTables
    ){



        this.cascadingTables = cascadingTables;


        graph = new EdgeLabelledGraph<>(nodeIDS, edges, IDMapping, startNodeID, cascadingTables);

        this.startNodeID = startNodeID;
        this.idMapping = IDMapping;

        arrayVertices = (Integer[]) graph.arrayVertices();

        labelledEdges = graph.labelledEdges();


    }




}
