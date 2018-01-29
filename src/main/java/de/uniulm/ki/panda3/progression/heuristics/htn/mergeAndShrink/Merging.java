package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.CascadingTables;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.Utils;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.HtnElementaryNode;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.panda3.symbolic.domain.Task;
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
            }

            //not ordered subtasks

        } else {
            throw new IllegalArgumentException("can only handle methods with 1 or two tasks");
        }

        return temporaryGraph;

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
