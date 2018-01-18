package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.util.EdgeLabelledGraph;
import scala.Tuple3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ClassicalMSGraph {


    public EdgeLabelledGraph<Integer, Integer, HashMap<Integer, NodeValue>, Integer, HashSet<Integer>, HashSet<Integer>, HashSet<Integer>, HashSet<Integer>, CascadingTables> graph;

    public HashMap<Integer, NodeValue> idMapping;

    public int startNodeID;
    public HashSet<Integer> usedFactIndexes;
    public HashSet<Integer> usedVariables;
    public HashSet<Integer> notYetUsedVariables;
    public HashSet<Integer> allVariables;
    public HashSet<Integer> goalVariables;
    public CascadingTables cascadingTables;
    public Integer[] arrayVertices;
    public Tuple3<Integer, Integer, Integer>[] labelledEdges;



    public ClassicalMSGraph(Integer[] nodeIDS, Tuple3<Integer, Integer, Integer>[] edges, HashMap<Integer, NodeValue> IDMapping, int startNodeID, HashSet<Integer> usedFactIndexes, HashSet<Integer> usedVariables, HashSet<Integer> notYetUsedVariables, HashSet<Integer> goalVariables,  HashSet<Integer> allVariables, CascadingTables cascadingTables){




         graph = new EdgeLabelledGraph<>(nodeIDS, edges, IDMapping, startNodeID, usedFactIndexes, usedVariables,notYetUsedVariables, allVariables, cascadingTables);

         this.startNodeID = startNodeID;
         this.usedFactIndexes = usedFactIndexes;
         this.usedVariables = usedVariables;
         this.notYetUsedVariables = notYetUsedVariables;
         this.allVariables = allVariables;
         this.cascadingTables = cascadingTables;
         this.idMapping = IDMapping;
         this.goalVariables = goalVariables;

         arrayVertices = (Integer[]) graph.arrayVertices();

         labelledEdges = graph.labelledEdges();


    }


}
