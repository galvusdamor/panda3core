package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.util.EdgeLabelledGraph;
import scala.Tuple3;

import java.util.HashMap;

public abstract class HtnMsGraph{

    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer> graph;

    public HashMap<Integer, NodeValue> idMapping;

    public int startNodeID;
    public Integer[] arrayVertices;
    public Tuple3<Integer, Integer, Integer>[] labelledEdges;


}