package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.CascadingTables;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.Utils;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.HtnElementaryNode;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.HtnNodeValue;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import scala.Int;
import scala.Tuple3;

import java.util.ArrayList;
import java.util.HashMap;

public class Merging {



    public static HashMap<Integer,HtnMsGraph> mergeWithTaskIndex(SasPlusProblem p, int taskIndex, HashMap<Integer,HtnMsGraph> presentGraphs){


        Task t = ProgressionNetwork.indexToTask[taskIndex];

        System.out.println(t.isPrimitive());

        System.out.println(t.longInfo());

        HtnMsGraph graph;

        if (t.isPrimitive()){

            HtnElementaryNode startNode = new HtnElementaryNode(p, false);

            HtnElementaryNode goalNode = new HtnElementaryNode(p, true);

            HashMap<Integer, NodeValue> idMapping = new HashMap<>();

            Integer[] nodeIDs = new Integer[2];
            nodeIDs[0] = 0;
            nodeIDs[1] = 1;

            idMapping.put(0, startNode);
            idMapping.put(1,goalNode);

            ArrayList<Tuple3<Integer,Integer,Integer>> edges = new ArrayList<>();

            Tuple3<Integer,Integer,Integer> edge = new Tuple3<>(0, taskIndex, 1);

            edges.add(edge);

            Tuple3<Integer,Integer,Integer>[] edgeTuple = Utils.convertEdgeArrayListToTuple3(edges);





            CascadingTables cascadingTables = new CascadingTables();

            graph = new HtnMsGraph(nodeIDs, edgeTuple, idMapping, 0, cascadingTables);

            presentGraphs.put(taskIndex, graph);

        }else{

            if (presentGraphs.size() != 0){

            }else{
                System.out.println("Error! Problem is not Tail-Recursive, or wrong merging order!");
                System.exit(1);
            }

        }



        return presentGraphs;
    }



}
