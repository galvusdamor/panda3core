package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.CascadingTables;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.Utils;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.*;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import scala.Array;
import scala.Tuple2;
import scala.Tuple3;

import java.util.*;

public class Merging {



    public static HashMap<Integer,HtnMsGraph> getHtnMsGraphForTaskIndex(SasPlusProblem p, HashMap<Task, List<ProMethod>> methods, int taskIndex, HashMap<Integer,HtnMsGraph> presentGraphs){


        Task t = ProgressionNetwork.indexToTask[taskIndex];



        HtnMsGraph graph;

        if (t.isPrimitive()){

            graph = getHtnMsGraphForPrimitiveTask(p, taskIndex);

            presentGraphs.put(taskIndex, graph);

        }else{

            //System.out.println(taskIndex + ": " + t.longInfo());

            HtnElementaryNode startNode = new HtnElementaryNode(p, false);

            HashMap<Integer, NodeValue> idMapping = new HashMap<>();

            idMapping.put(0, startNode);

            LinkedList<Tuple3<Integer,Integer,Integer>> edges = new LinkedList<>();

            TemporaryHtnMsGraph temporaryGraph = new TemporaryHtnMsGraph(edges, idMapping, 0);


            List<ProMethod> proMethods = methods.get(t);


            for (ProMethod proMethod : proMethods){

                temporaryGraph = handleProMethod(p, presentGraphs,temporaryGraph, proMethod, taskIndex);
            }




            HtnMsGraph newGraph = temporaryGraph.convertToHtnMsGraph();
            presentGraphs.put(taskIndex, newGraph);

        }



        return presentGraphs;
    }

    public static HtnMsGraph getHtnMsGraphForPrimitiveTask(SasPlusProblem p, int taskIndex){

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

        HtnMsGraph graph = new HtnMsGraph(nodeIDs, edgeTuple, idMapping, 0, cascadingTables);

        return graph;

    }


    public static TemporaryHtnMsGraph handleProMethod(SasPlusProblem p, HashMap<Integer,HtnMsGraph> presentGraphs, TemporaryHtnMsGraph temporaryGraph, ProMethod proMethod, int actualTaskIndex){

        Map<Task, Integer> taskToIndexMapping = ProgressionNetwork.taskToIndex;

        if (proMethod.subtasks.length==1){
            int subTaskID = taskToIndexMapping.get(proMethod.subtasks[0]);
            HtnMsGraph graphOfSubtask = presentGraphs.get(subTaskID);
            if (graphOfSubtask==null){
                System.out.println("Error! Problem might not be Tail Recursive, or wrong order!");
                System.exit(1);
            }else {
                temporaryGraph = appendGraphToTemporaryGraphAtIndex(temporaryGraph,graphOfSubtask, temporaryGraph.startNodeID)._2;
            }

        }else if (proMethod.subtasks.length==2){

            //ordered subtasks

/*            int[] ordering = proMethod.orderings.get(0);

            System.out.println("ordering size: " + proMethod.orderings.size());

            if (proMethod.orderings.size()>1){

                for (int[] singleOrdering: proMethod.orderings){
                    System.out.println("New single Ordering:");
                    for (int i: singleOrdering) System.out.println(i);
                }
            }*/

            if(proMethod.orderings.size()>0) {


                int[] orderedSubTasks = new int[proMethod.orderings.size() + 1];

                orderedSubTasks[0] = proMethod.orderings.get(0)[0];
                orderedSubTasks[1] = proMethod.orderings.get(0)[1];

                for (int i = 1; i < proMethod.orderings.size(); i++) {
                    int j = i + 1;
                    orderedSubTasks[j] = proMethod.orderings.get(i)[1];
                }


                HashSet<Integer> nodesToAppend = new HashSet<>();
                nodesToAppend.add(temporaryGraph.startNodeID);


                for (int i = 0; i < orderedSubTasks.length; i++) {


                    int subtaskIndex = orderedSubTasks[i];

                    Task subTask = proMethod.subtasks[subtaskIndex];

                    int subTaskID = ProgressionNetwork.taskToIndex.get(subTask);


                    if (subTaskID == actualTaskIndex) {


                        for (int nodeIDToAppend : nodesToAppend) {
                            Tuple3<Integer, Integer, Integer> edge = new Tuple3<>(nodeIDToAppend, -1, temporaryGraph.startNodeID);
                            temporaryGraph.edges.add(edge);
                        }

                    } else {

                        HtnMsGraph graphOfSubtask = presentGraphs.get(subTaskID);


                        HashSet<Integer> newNodesToAppend = new HashSet<>();

                        for (int nodeIDToAppend : nodesToAppend) {

                            NodeValue nodeValue = temporaryGraph.idMapping.get(nodeIDToAppend);

                            if (nodeValue instanceof HtnElementaryNode) {
                                NodeValue newNodeValue = new HtnElementaryNode(p, false);
                                temporaryGraph.idMapping.put(nodeIDToAppend, newNodeValue);
                            }

                            Tuple2<HashSet<Integer>, TemporaryHtnMsGraph> resultsOfAppending = appendGraphToTemporaryGraphAtIndex(temporaryGraph, graphOfSubtask, nodeIDToAppend);

                            temporaryGraph = resultsOfAppending._2;
                            newNodesToAppend.addAll(resultsOfAppending._1);


                        }

                        nodesToAppend = newNodesToAppend;


                    }


                }
            }else{

                //not ordered subtasks

                System.out.println();

                Task subtask1 = proMethod.subtasks[0];
                Task subtask2 = proMethod.subtasks[1];

                int subtask1Index = taskToIndexMapping.get(subtask1);
                int subtask2Index = taskToIndexMapping.get(subtask2);

                HtnMsGraph graphOfSubtask1 = presentGraphs.get(subtask1Index);
                HtnMsGraph graphOfSubtask2 = presentGraphs.get(subtask2Index);


                HtnMsGraph newGraph = mergeGraphs(graphOfSubtask1, graphOfSubtask2, p);

                temporaryGraph = appendGraphToTemporaryGraphAtIndex(temporaryGraph, newGraph, temporaryGraph.startNodeID)._2;


            }


        } else {
            throw new IllegalArgumentException("can only handle methods with 1 or two tasks");
        }

        return temporaryGraph;

    }

    public static HtnMsGraph mergeGraphs(HtnMsGraph graph1, HtnMsGraph graph2, SasPlusProblem p){


        Integer[] graph1Nodes = (Integer[]) graph1.arrayVertices;

        Integer[] graph2Nodes = (Integer[]) graph2.arrayVertices;




        HashMap<Integer, Tuple2<Integer, Integer>> tempIdMapping = new HashMap<>();
        HashMap<Tuple2<Integer, Integer>, Integer> tempReverseIdMapping = new HashMap<>();

        List<Integer> graph1NodeIDs = Arrays.asList(graph1Nodes);

        List<Integer> graph2NodeIDs = Arrays.asList(graph2Nodes);

        Integer[][] combinations = graph1NodeIDs.stream().flatMap(ai -> graph2NodeIDs.stream().map(bi -> new Integer[]{ai, bi})).toArray(Integer[][]::new);

        Integer[] newNodeIDs = new Integer[combinations.length];

        HashMap<Integer, NodeValue> idMapping = new HashMap<>();

        for (int i = 0; i < combinations.length; i++) {

            int oldGraph1ID = combinations[i][0];
            int oldGraph2ID = combinations[i][1];

            Tuple2<Integer, Integer> combi = new Tuple2<>(oldGraph1ID, oldGraph2ID);

            tempIdMapping.put(i, combi);
            tempReverseIdMapping.put(combi, i);

            newNodeIDs[i] = i;

            NodeValue newNodeValue1 = graph1.idMapping.get(oldGraph1ID);
            NodeValue newNodeValue2 = graph2.idMapping.get(oldGraph2ID);

            if ((newNodeValue1 instanceof HtnNodeValue) && (newNodeValue2 instanceof HtnNodeValue))  {
                HtnNodeValue newNodeValue12 = (HtnNodeValue) newNodeValue1;
                HtnNodeValue newNodeValue22 = (HtnNodeValue) newNodeValue2;
                NodeValue newNodeValue = new HtnMergeNode(newNodeValue12, newNodeValue22, p);
                idMapping.put(i, newNodeValue);
            }else{
                System.out.println("Wrong type!!");
                System.exit(1);
            }


        }


        Tuple3<Integer, Integer, Integer>[] graph1Edges = graph1.labelledEdges;
        Tuple3<Integer, Integer, Integer>[] graph2Edges = graph2.labelledEdges;

        LinkedList<Tuple3<Integer, Integer, Integer>> newMultiEdges = new LinkedList<>();

        for (Tuple3<Integer, Integer, Integer> edgeOfGraph1 : graph1Edges) {

                int graph1StartNodeID = edgeOfGraph1._1();
                int graph1EndNodeID = edgeOfGraph1._3();

                for (int idOfGraph2Node : graph2NodeIDs) {

                    Tuple2<Integer, Integer> startNodeCombi = new Tuple2<>(graph1StartNodeID, idOfGraph2Node);
                    int newStartNodeID = tempReverseIdMapping.get(startNodeCombi);

                    Tuple2<Integer, Integer> endNodeCombi = new Tuple2<>(graph1EndNodeID, idOfGraph2Node);
                    int newEndNodeID = tempReverseIdMapping.get(endNodeCombi);

                    int taskIDofEdge = edgeOfGraph1._2();

                    Tuple3<Integer, Integer, Integer> newEdge = new Tuple3<>(newStartNodeID, taskIDofEdge, newEndNodeID);
                    newMultiEdges.add(newEdge);
                }

        }

        for (Tuple3<Integer, Integer, Integer> edgeOfGraph2 : graph2Edges) {

            int graph2StartNodeID = edgeOfGraph2._1();
            int graph2EndNodeID = edgeOfGraph2._3();

            for (int idOfGraph1Node : graph1NodeIDs) {

                Tuple2<Integer, Integer> startNodeCombi = new Tuple2<>(idOfGraph1Node, graph2StartNodeID);
                int newStartNodeID = tempReverseIdMapping.get(startNodeCombi);

                Tuple2<Integer, Integer> endNodeCombi = new Tuple2<>(idOfGraph1Node, graph2EndNodeID);
                int newEndNodeID = tempReverseIdMapping.get(endNodeCombi);

                int taskIDofEdge = edgeOfGraph2._2();

                Tuple3<Integer, Integer, Integer> newEdge = new Tuple3<>(newStartNodeID, taskIDofEdge, newEndNodeID);
                newMultiEdges.add(newEdge);
            }



        }


        Tuple2<Integer, Integer> oldStartIDs = new Tuple2<>(graph1.startNodeID, graph2.startNodeID);

        int newStartID = tempReverseIdMapping.get(oldStartIDs);

        TemporaryHtnMsGraph newGraph = new TemporaryHtnMsGraph(newMultiEdges, idMapping, newStartID);

        HtnMsGraph mergedGraph = newGraph.convertToHtnMsGraph();

        return mergedGraph;
    }




    public static HashSet<Integer> getGoalNodes(HtnMsGraph temporaryHtnMsGraph){

        HashSet<Integer> goalNodes = new HashSet<>();

        for (int id: temporaryHtnMsGraph.idMapping.keySet()){
            if (temporaryHtnMsGraph.idMapping.get(id).isGoalNode()){
                goalNodes.add(id);
            }
        }


        return goalNodes;

    }

    public static Tuple2<HashSet<Integer>,TemporaryHtnMsGraph> appendGraphToTemporaryGraphAtIndex(TemporaryHtnMsGraph temporaryGraph, HtnMsGraph graphToAppend, int index){

        if (!temporaryGraph.idMapping.keySet().contains(index)){
            System.out.println("cannot append it at this node ID, because the node ID does not exist.");
            System.exit(1);
        }

        HashMap<Integer, Integer> oldIdToNewIdMapping = new HashMap<>();

        if (graphToAppend== null){

            System.out.println("Graph is null!");
            System.out.println("index: " + index);
            System.exit(1);
        }

        HashSet<Integer> IdsToAppend = new HashSet<>();
        IdsToAppend.addAll(graphToAppend.idMapping.keySet());

        oldIdToNewIdMapping.put(graphToAppend.startNodeID, index);

        IdsToAppend.remove(graphToAppend.startNodeID);

        int i=temporaryGraph.idMapping.keySet().size();

        for (int oldID: IdsToAppend){
            oldIdToNewIdMapping.put(oldID,i);
            temporaryGraph.idMapping.put(i,graphToAppend.idMapping.get(oldID));
            i++;
        }


        for (Tuple3<Integer,Integer,Integer> edge : graphToAppend.labelledEdges){

            int newStartNodeID = oldIdToNewIdMapping.get(edge._1());
            int newGoalNodeID = oldIdToNewIdMapping.get(edge._3());

            Tuple3<Integer,Integer,Integer> newEdge = new Tuple3<>(newStartNodeID, edge._2(), newGoalNodeID);

            temporaryGraph.edges.add(newEdge);
        }

        HashSet<Integer> oldGoalNodes = getGoalNodes(graphToAppend);
        HashSet<Integer> newGoalNodes = new HashSet<>();

        for (int oldGoalNode: oldGoalNodes){

            newGoalNodes.add(oldIdToNewIdMapping.get(oldGoalNode));

        }

        Tuple2<HashSet<Integer>,TemporaryHtnMsGraph> results = new Tuple2<>(newGoalNodes, temporaryGraph);

        return results;

    }



}
