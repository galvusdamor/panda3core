package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.Utils;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.*;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import scala.Tuple2;
import scala.Tuple3;

import java.util.*;

public class HtnMerging {



    public static HashMap<Integer,HtnMsGraph> getHtnMsGraphForTaskIndex(SasPlusProblem p, HashMap<Task, List<ProMethod>> methods, int taskIndex, HashMap<Integer,HtnMsGraph> presentGraphs,  int shrinkingBound, HtnShrinkingStrategy shrinkingStrategy, boolean withMethods){


        Task t = ProgressionNetwork.indexToTask[taskIndex];



        HtnMsGraph graph;

        if (t.isPrimitive()){


            graph = getHtnMsGraphForPrimitiveTask(p, taskIndex, withMethods);

            graph = GraphMinimation.minimizeGraph(p, graph);

            presentGraphs.put(taskIndex, graph);

        }else{

            //System.out.println(taskIndex + ": " + t.longInfo());

            HtnElementaryNode startNode = new HtnElementaryNode(p, false);

            HashMap<Integer, NodeValue> idMapping = new HashMap<>();

            idMapping.put(0, startNode);

            LinkedList<Tuple3<Integer,Integer,Integer>> edges = new LinkedList<>();

            TemporaryHtnMsGraph temporaryGraph;

            if(withMethods==true){
                temporaryGraph = new TemporaryHtnMsGraphWithMethods(edges, idMapping, 0);
            } else {
                temporaryGraph = new TemporaryHtnMsGraphWithoutMethods(edges, idMapping, 0);
            }


            List<ProMethod> proMethods = methods.get(t);

            temporaryGraph = handleProMethods(p, presentGraphs, proMethods, temporaryGraph, taskIndex, shrinkingBound, shrinkingStrategy);


            /*for (ProMethod proMethod : proMethods){

                temporaryGraph = handleProMethod(p, presentGraphs,temporaryGraph, proMethod, taskIndex, shrinkingBound, shrinkingStrategy);
            }*/




            HtnMsGraph newGraph = temporaryGraph.convertToHtnMsGraph();
            newGraph = GraphMinimation.minimizeGraph(p, newGraph);
            presentGraphs.put(taskIndex, newGraph);

        }



        return presentGraphs;
    }

    public static TemporaryHtnMsGraph handleProMethods(SasPlusProblem p, HashMap<Integer,HtnMsGraph> presentGraphs, List<ProMethod> proMethods, TemporaryHtnMsGraph temporaryGraph, int taskIndex, int shrinkingBound, HtnShrinkingStrategy shrinkingStrategy){




        HashMap<Integer, TaskDecomposition> proMethodToTaskDecompositionMap = new HashMap<>();
        HashMap<Integer, Integer> proMethodIndexToAdditionalSizeMap = new HashMap<>();

        for (int i=0; i<proMethods.size(); i++){

            ProMethod proMethod = proMethods.get(i);

            if (proMethod.subtasks.length==1){


                Task subtask = proMethod.subtasks[0];
                int subtaskIndex = ProgressionNetwork.taskToIndex.get(subtask);



                    HtnMsGraph subgraph = presentGraphs.get(subtaskIndex);
                    int additionalSize = subgraph.arrayVertices.length-1;
                    TaskDecomposition decomposition = new SingleDecomposition(additionalSize, subgraph);
                    proMethodToTaskDecompositionMap.put(i, decomposition);
                    proMethodIndexToAdditionalSizeMap.put(i, additionalSize);

                    /*if subtask is primitive, it was already handled before
                    if (subtask.isPrimitive()) {

                        int additionalSize = 1;
                        PrimitiveDecomposition decomposition = new PrimitiveDecomposition(additionalSize, subtaskIndex);
                        proMethodToTaskDecompositionMap.put(i, decomposition);
                        proMethodIndexToAdditionalSizeMap.put(i, additionalSize);
                    }else {
                        HtnMsGraph subgraph = presentGraphs.get(subtaskIndex);
                        int additionalSize = subgraph.arrayVertices.length-1;
                        TaskDecomposition decomposition = new SingleAbstractDecomposition(additionalSize, subgraph);
                        proMethodToTaskDecompositionMap.put(i, decomposition);
                        proMethodIndexToAdditionalSizeMap.put(i, additionalSize);
                    }*/


            }else if (proMethod.subtasks.length==2) {

                if (proMethod.orderings.size() > 0) {

                    //ordered Subtasks
                    int indexOfFirstSubtask = proMethod.orderings.get(0)[0];
                    int indexOfSecondSubtask = proMethod.orderings.get(0)[1];
                    Task subtask1 = proMethod.subtasks[indexOfFirstSubtask];
                    Task subtask2 = proMethod.subtasks[indexOfSecondSubtask];
                    int subtask1Index = ProgressionNetwork.taskToIndex.get(subtask1);
                    int subtask2Index = ProgressionNetwork.taskToIndex.get(subtask2);
                    HtnMsGraph graph1 = presentGraphs.get(subtask1Index);
                    if (subtask2Index==taskIndex){

                        int additionalSize = graph1.arrayVertices.length-2;
                        OrderedRecursiveDecomposition decomposition = new OrderedRecursiveDecomposition(additionalSize, graph1);
                        proMethodToTaskDecompositionMap.put(i,decomposition);
                        proMethodIndexToAdditionalSizeMap.put(i, additionalSize);

                    }else {
                        HtnMsGraph graph2 = presentGraphs.get(subtask2Index);
                        int additionalSize = calculateSizeOfOrderedSubGraphs(graph1, graph2)-1;

                        OrderedDecomposition decomposition = new OrderedDecomposition(additionalSize, graph1, graph2);
                        proMethodToTaskDecompositionMap.put(i,decomposition);
                        proMethodIndexToAdditionalSizeMap.put(i, additionalSize);
                    }

                }else {

                    //unorderedSubtasks

                    Task subtask1 = proMethod.subtasks[0];
                    Task subtask2 = proMethod.subtasks[1];
                    int subtask1Index = ProgressionNetwork.taskToIndex.get(subtask1);
                    int subtask2Index = ProgressionNetwork.taskToIndex.get(subtask2);
                    HtnMsGraph graph1 = presentGraphs.get(subtask1Index);
                    HtnMsGraph graph2 = presentGraphs.get(subtask2Index);

                    int additionalSize = calculateSizeOfOrderedSubGraphs(graph1, graph2)-1;
                    UnorderedDecomposition decomposition = new UnorderedDecomposition(additionalSize, graph1, graph2);
                    proMethodToTaskDecompositionMap.put(i,decomposition);
                    proMethodIndexToAdditionalSizeMap.put(i, additionalSize);

                }
            } else {
            throw new IllegalArgumentException("can only handle methods with 1 or two tasks");
            }




        }

        int sizeOfNewGraph=1;

        for (TaskDecomposition taskDecomposition : proMethodToTaskDecompositionMap.values()){
            sizeOfNewGraph+=taskDecomposition.additionalSizeAfterExecution;
        }

        if(sizeOfNewGraph<=shrinkingBound) {

            //no need to shrink
            for (int proMethodIndex:proMethodIndexToAdditionalSizeMap.keySet()){

                TaskDecomposition taskDecomposition = proMethodToTaskDecompositionMap.get(proMethodIndex);
                temporaryGraph = handleTaskDecomposition(p, taskDecomposition, temporaryGraph);


            }


            /*for (ProMethod proMethod : proMethods) {

                temporaryGraph = handleProMethod(p, presentGraphs, temporaryGraph, proMethod, taskIndex, shrinkingBound, shrinkingStrategy);
            }*/

        }else {
            //need to shrink
            //System.out.println("Need to shrink.");

            Boolean ableToShrink = true;

            while ((sizeOfNewGraph>shrinkingBound) && (ableToShrink == true)) {
                //find biggest DecompositionTask
                int maximumAdditionalSize = Collections.max(proMethodIndexToAdditionalSizeMap.values());

                for (int proMethodIndex : proMethodIndexToAdditionalSizeMap.keySet()) {

                    if (proMethodIndexToAdditionalSizeMap.get(proMethodIndex) == maximumAdditionalSize) {
                        //shrink

                        //System.out.println("Shrinking.");
                        TaskDecomposition oldTaskDecomposition = proMethodToTaskDecompositionMap.get(proMethodIndex);
                        try {
                            TaskDecomposition newTaskDecomposition = shrinkGraphsOfTaskDecomposition(p, oldTaskDecomposition, shrinkingBound, shrinkingStrategy);
                            proMethodToTaskDecompositionMap.put(proMethodIndex, newTaskDecomposition);
                            sizeOfNewGraph -= oldTaskDecomposition.additionalSizeAfterExecution;
                            sizeOfNewGraph += newTaskDecomposition.additionalSizeAfterExecution;
                            proMethodIndexToAdditionalSizeMap.put(proMethodIndex, newTaskDecomposition.additionalSizeAfterExecution);
                        }catch (IllegalArgumentException e){
                            ableToShrink=false;
                            break;
                        }


                    }


                }


                //System.out.println("New Size after Shrinking: " + sizeOfNewGraph);


            }


            //handle TaskDecompositions
            for (int proMethodIndex:proMethodIndexToAdditionalSizeMap.keySet()){

                TaskDecomposition taskDecomposition = proMethodToTaskDecompositionMap.get(proMethodIndex);
                temporaryGraph = handleTaskDecomposition(p, taskDecomposition, temporaryGraph);


            }



        }

        return temporaryGraph;

    }

    public static TemporaryHtnMsGraph handleTaskDecomposition(SasPlusProblem p, TaskDecomposition taskDecomposition, TemporaryHtnMsGraph temporaryGraph){


        if (taskDecomposition instanceof SingleDecomposition){

            HtnMsGraph subgraph = ((SingleDecomposition) taskDecomposition).graph1;

            temporaryGraph = appendGraphToTemporaryGraphAtIndex(temporaryGraph,subgraph, temporaryGraph.startNodeID)._2;

        }
        if (taskDecomposition instanceof OrderedDecomposition) {

            HtnMsGraph graph1 = ((OrderedDecomposition) taskDecomposition).graph1;
            HtnMsGraph graph2 = ((OrderedDecomposition) taskDecomposition).graph2;


            HashSet<Integer> nodesToAppend = new HashSet<>();

            Tuple2<HashSet<Integer>, TemporaryHtnMsGraph> resultsOfAppending = appendGraphToTemporaryGraphAtIndex(temporaryGraph, graph1, temporaryGraph.startNodeID);

            temporaryGraph = resultsOfAppending._2;
            nodesToAppend.addAll(resultsOfAppending._1);


            //Iterator<Integer> iter = nodesToAppend.iterator();
            for (int nodeIDToAppend : nodesToAppend) {
            /*while (iter.hasNext()){
                int nodeIDToAppend = iter.next();*/

                NodeValue nodeValue = temporaryGraph.idMapping.get(nodeIDToAppend);

                if (nodeValue instanceof HtnElementaryNode) {
                    NodeValue newNodeValue = new HtnElementaryNode(p, false);
                    temporaryGraph.idMapping.put(nodeIDToAppend, newNodeValue);
                }

                resultsOfAppending = appendGraphToTemporaryGraphAtIndex(temporaryGraph, graph2, nodeIDToAppend);

                temporaryGraph = resultsOfAppending._2;
                //nodesToAppend.addAll(resultsOfAppending._1);

            }


        }
        if (taskDecomposition instanceof OrderedRecursiveDecomposition){

            HtnMsGraph graph1 = ((OrderedRecursiveDecomposition) taskDecomposition).graph1;

            HashSet<Integer> nodesToAppend = new HashSet<>();

            Tuple2<HashSet<Integer>, TemporaryHtnMsGraph> resultsOfAppending = appendGraphToTemporaryGraphAtIndex(temporaryGraph, graph1, temporaryGraph.startNodeID);

            temporaryGraph = resultsOfAppending._2;
            nodesToAppend.addAll(resultsOfAppending._1);

            HashSet<Tuple3<Integer,Integer,Integer>> edgesToRemove = new HashSet<>();
            ArrayList<Tuple3<Integer, Integer, Integer>> tempEdges = new ArrayList<>(temporaryGraph.edges);


            for (int j=0; j<tempEdges.size(); j++){
                Tuple3<Integer, Integer, Integer> edge = tempEdges.get(j);
                if (nodesToAppend.contains(edge._3())){
                    Tuple3<Integer,Integer,Integer> newEdge = new Tuple3<>(edge._1(), edge._2(), temporaryGraph.startNodeID);

                    edgesToRemove.add(edge);
                    //temporaryGraph.edges.remove(edge);
                    temporaryGraph.edges.add(newEdge);
                }
            }

            temporaryGraph.edges.removeAll(edgesToRemove);

            for (int idToRemove:nodesToAppend){
                temporaryGraph.idMapping.remove(idToRemove);
            }

        }
        if (taskDecomposition instanceof UnorderedDecomposition){


            UnorderedDecomposition unorderedDecomposition = (UnorderedDecomposition) taskDecomposition;
            HtnMsGraph graphOfSubtask1 = unorderedDecomposition.graph1;
            HtnMsGraph graphOfSubtask2 = unorderedDecomposition.graph2;


            HtnMsGraph newGraph = mergeGraphs(graphOfSubtask1, graphOfSubtask2, p);

            temporaryGraph = appendGraphToTemporaryGraphAtIndex(temporaryGraph, newGraph, temporaryGraph.startNodeID)._2;


        }


        return temporaryGraph;



    }

    public static TaskDecomposition shrinkGraphsOfTaskDecomposition(SasPlusProblem p, TaskDecomposition taskDecomposition, int shrinkingBound, HtnShrinkingStrategy shrinkingStrategy){


        if (taskDecomposition instanceof PrimitiveDecomposition){

            throw new IllegalArgumentException("cannot shrink any more!");

        }
        if (taskDecomposition instanceof SingleDecomposition){

            //System.out.println("SingleDecomp");

            HtnMsGraph graph1 = ((SingleDecomposition) taskDecomposition).graph1;


            int sizeOfGraph1 = graph1.idMapping.keySet().size();

            //System.out.println("Shrinking Bound: " + shrinkingBound);

            int additionalSizeOfNewGraph = sizeOfGraph1-1;



                graph1 = shrinkingStrategy.shrink(p, graph1);


                sizeOfGraph1 = graph1.idMapping.keySet().size();

                additionalSizeOfNewGraph = sizeOfGraph1-1;



            TaskDecomposition newTaskDecomposition = new SingleDecomposition(additionalSizeOfNewGraph, graph1);
            return newTaskDecomposition;

        }
        if (taskDecomposition instanceof OrderedDecomposition){

            //System.out.println("OrderedDecomp");

            HtnMsGraph graph1 = ((OrderedDecomposition) taskDecomposition).graph1;
            HtnMsGraph graph2 = ((OrderedDecomposition) taskDecomposition).graph2;


            int sizeOfGraph1 = graph1.idMapping.keySet().size();
            int sizeOfGraph2 = graph2.idMapping.keySet().size();

            //System.out.println("Shrinking Bound: " + shrinkingBound);

            int additionalSizeOfNewGraph = sizeOfGraph1+sizeOfGraph2-2;



                if (sizeOfGraph2>sizeOfGraph1){
                    //System.out.println("Old size of graph 2:" + graph2.idMapping.keySet().size());
                    graph2 = shrinkingStrategy.shrink(p, graph2);
                    //System.out.println("New size of graph 2:" + graph2.idMapping.keySet().size());
                }else {
                    //System.out.println("Old size of graph 1:" + graph1.idMapping.keySet().size());
                    graph1 = shrinkingStrategy.shrink(p, graph1);
                    //System.out.println("New size of graph 1:" + graph1.idMapping.keySet().size());
                }

                sizeOfGraph1 = graph1.idMapping.keySet().size();
                sizeOfGraph2 = graph2.idMapping.keySet().size();

                additionalSizeOfNewGraph = sizeOfGraph1+sizeOfGraph2-2;



            TaskDecomposition newTaskDecomposition = new OrderedDecomposition(additionalSizeOfNewGraph, graph1, graph2);
            return newTaskDecomposition;

        }
        if (taskDecomposition instanceof OrderedRecursiveDecomposition){

            //System.out.println("OrderedRecursiveDecomp");
            HtnMsGraph graph1 = ((OrderedRecursiveDecomposition) taskDecomposition).graph1;


            int sizeOfGraph1 = graph1.idMapping.keySet().size();

            //System.out.println("Shrinking Bound: " + shrinkingBound);

            int additionalSizeOfNewGraph = sizeOfGraph1-1;



                graph1 = shrinkingStrategy.shrink(p, graph1);


                sizeOfGraph1 = graph1.idMapping.keySet().size();

                additionalSizeOfNewGraph = sizeOfGraph1-1;



            TaskDecomposition newTaskDecomposition = new OrderedRecursiveDecomposition(additionalSizeOfNewGraph, graph1);
            return newTaskDecomposition;

        }
        if (taskDecomposition instanceof UnorderedDecomposition){

            //System.out.println("UnorderedDecomp");
            HtnMsGraph graph1 = ((UnorderedDecomposition) taskDecomposition).graph1;
            HtnMsGraph graph2 = ((UnorderedDecomposition) taskDecomposition).graph2;


            int sizeOfGraph1 = graph1.idMapping.keySet().size();
            int sizeOfGraph2 = graph2.idMapping.keySet().size();

            //System.out.println("Shrinking Bound: " + shrinkingBound);

            int additionalSizeOfNewGraph = (sizeOfGraph1*sizeOfGraph2)-1;






                //System.out.println("Size of new Graph: " + sizeOfNewGraph);

                //System.out.println("Size of Graph 1: " + sizeOfGraph1);
                //System.out.println("Size of Graph 2: " + sizeOfGraph2);




                if (sizeOfGraph2>sizeOfGraph1){
                    graph2 = shrinkingStrategy.shrink(p, graph2);
                }else {
                    graph1 = shrinkingStrategy.shrink(p, graph1);
                }

                sizeOfGraph1 = graph1.idMapping.keySet().size();
                sizeOfGraph2 = graph2.idMapping.keySet().size();

                additionalSizeOfNewGraph = (sizeOfGraph1*sizeOfGraph2)-1;



            TaskDecomposition newTaskDecomposition = new UnorderedDecomposition(additionalSizeOfNewGraph, graph1, graph2);
            return newTaskDecomposition;

        }


        return null;
    }

    public static int calculateSizeOfOrderedSubGraphs(HtnMsGraph graph1, HtnMsGraph graph2){

        int size = graph1.arrayVertices.length + graph2.arrayVertices.length - 1;


        return size;
    }

    public static int calculateSizeOfUnOrderedSubGraphs(HtnMsGraph graph1, HtnMsGraph graph2){

        int size = graph1.arrayVertices.length * graph2.arrayVertices.length;


        return size;
    }

    public static HtnMsGraph getHtnMsGraphForPrimitiveTask(SasPlusProblem p, int taskIndex, boolean withMethods){

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





        HtnMsGraph graph;

        if(withMethods==true){
           graph = new HtnMsGraphWithMethods(nodeIDs, edgeTuple, idMapping, 0);
        } else {
            graph = new HtnMsGraphWithoutMethods(nodeIDs, edgeTuple, idMapping, 0);
        }

        return graph;

    }


    public static TemporaryHtnMsGraph handleProMethod(SasPlusProblem p, HashMap<Integer,HtnMsGraph> presentGraphs, TemporaryHtnMsGraph temporaryGraph, ProMethod proMethod, int actualTaskIndex,  int shrinkingBound, HtnShrinkingStrategy shrinkingStrategy){

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

                        HashSet<Tuple3<Integer,Integer,Integer>> edgesToRemove = new HashSet<>();


                        ArrayList<Tuple3<Integer, Integer, Integer>> tempEdges = new ArrayList<>(temporaryGraph.edges);


                        for (int j=0; j<tempEdges.size(); j++){
                            Tuple3<Integer, Integer, Integer> edge = tempEdges.get(j);
                            if (nodesToAppend.contains(edge._3())){
                                Tuple3<Integer,Integer,Integer> newEdge = new Tuple3<>(edge._1(), edge._2(), temporaryGraph.startNodeID);

                                edgesToRemove.add(edge);
                                //temporaryGraph.edges.remove(edge);
                                temporaryGraph.edges.add(newEdge);
                            }
                        }

                        temporaryGraph.edges.removeAll(edgesToRemove);

                        for (int idToRemove:nodesToAppend){
                            temporaryGraph.idMapping.remove(idToRemove);
                        }




/*                        for (int nodeIDToAppend : nodesToAppend) {
                            Tuple3<Integer, Integer, Integer> edge = new Tuple3<>(nodeIDToAppend, -1, temporaryGraph.startNodeID);
                            temporaryGraph.edges.add(edge);
                        }*/

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

                //System.out.println();

                Task subtask1 = proMethod.subtasks[0];
                Task subtask2 = proMethod.subtasks[1];

                int subtask1Index = taskToIndexMapping.get(subtask1);
                int subtask2Index = taskToIndexMapping.get(subtask2);

                HtnMsGraph graphOfSubtask1 = presentGraphs.get(subtask1Index);
                HtnMsGraph graphOfSubtask2 = presentGraphs.get(subtask2Index);


                //HtnMsGraph newGraph = mergeGraphs(graphOfSubtask1, graphOfSubtask2, p, shrinkingBound, shrinkingStrategy);
                HtnMsGraph newGraph = mergeGraphs(graphOfSubtask1, graphOfSubtask2, p);

                temporaryGraph = appendGraphToTemporaryGraphAtIndex(temporaryGraph, newGraph, temporaryGraph.startNodeID)._2;


            }


        } else {
            throw new IllegalArgumentException("can only handle methods with 1 or two tasks");
        }

        return temporaryGraph;

    }

    public static HtnMsGraph mergeGraphs(HtnMsGraph graph1, HtnMsGraph graph2, SasPlusProblem p){

        /*int sizeOfGraph1 = graph1.idMapping.keySet().size();
        int sizeOfGraph2 = graph2.idMapping.keySet().size();

        //System.out.println("Shrinking Bound: " + shrinkingBound);

        int sizeOfNewGraph = sizeOfGraph1*sizeOfGraph2;

        while (sizeOfNewGraph>shrinkingBound){




            //System.out.println("Size of new Graph: " + sizeOfNewGraph);

            //System.out.println("Size of Graph 1: " + sizeOfGraph1);
            //System.out.println("Size of Graph 2: " + sizeOfGraph2);




            if (sizeOfGraph2>sizeOfGraph1){
                graph2 = shrinkingStrategy.shrink(p, graph2);
            }else {
                graph1 = shrinkingStrategy.shrink(p, graph1);
            }

            sizeOfGraph1 = graph1.idMapping.keySet().size();
            sizeOfGraph2 = graph2.idMapping.keySet().size();

            sizeOfNewGraph = sizeOfGraph1*sizeOfGraph2;


        }*/


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


                //Falls doch mit Infos über Merge-Nodes:

                /*HtnNodeValue newNodeValue12 = (HtnNodeValue) newNodeValue1;
                HtnNodeValue newNodeValue22 = (HtnNodeValue) newNodeValue2;
                NodeValue newNodeValue = new HtnMergeNode(newNodeValue12, newNodeValue22, p);*/

                Boolean isGoalNode = newNodeValue1.isGoalNode() && newNodeValue2.isGoalNode();
                NodeValue newNodeValue = new HtnElementaryNode(p,isGoalNode);


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

        TemporaryHtnMsGraph newGraph;

        if(graph1 instanceof HtnMsGraphWithMethods){
            newGraph = new TemporaryHtnMsGraphWithMethods(newMultiEdges, idMapping, newStartID);
        } else {
            newGraph = new TemporaryHtnMsGraphWithoutMethods(newMultiEdges, idMapping, newStartID);
        }


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


    public static HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> getIDToOutgoingEdgesMap(HtnMsGraph graph){

        HashMap<Integer, ArrayList<Tuple3<Integer, Integer, Integer>>> outgoingEdgesMap = new HashMap<>();

        for (int i: graph.idMapping.keySet()){
            ArrayList<Tuple3<Integer, Integer, Integer>> edges = new ArrayList<>();
            outgoingEdgesMap.put(i,edges);
        }

        for (Tuple3<Integer, Integer, Integer> edge : graph.labelledEdges){

            outgoingEdgesMap.get(edge._1()).add(edge);

        }

        return outgoingEdgesMap;

    }



}


abstract class TaskDecomposition {

    int additionalSizeAfterExecution;

}

class PrimitiveDecomposition extends TaskDecomposition {

    public int subtaskIndex;

    public PrimitiveDecomposition(int sizeAfterExecution, int subtaskIndex){

        this.additionalSizeAfterExecution = sizeAfterExecution;
        this.subtaskIndex = subtaskIndex;
    }

}

class OrderedDecomposition extends TaskDecomposition {

    public HtnMsGraph graph1;
    public HtnMsGraph graph2;

    public OrderedDecomposition(int sizeAfterExecution, HtnMsGraph graph1, HtnMsGraph graph2){

        this.additionalSizeAfterExecution = sizeAfterExecution;
        this.graph1 = graph1;
        this.graph2 = graph2;
    }

}

class OrderedRecursiveDecomposition extends TaskDecomposition {

    public HtnMsGraph graph1;

    public OrderedRecursiveDecomposition(int sizeAfterExecution, HtnMsGraph graph1){

        this.additionalSizeAfterExecution = sizeAfterExecution;
        this.graph1 = graph1;
    }

}

class SingleDecomposition extends TaskDecomposition {

    public HtnMsGraph graph1;

    public SingleDecomposition(int sizeAfterExecution, HtnMsGraph graph1){

        this.additionalSizeAfterExecution = sizeAfterExecution;
        this.graph1 = graph1;
    }

}


class UnorderedDecomposition extends TaskDecomposition {

    public HtnMsGraph graph1;
    public HtnMsGraph graph2;

    public UnorderedDecomposition(int sizeAfterExecution, HtnMsGraph graph1, HtnMsGraph graph2){

        this.additionalSizeAfterExecution = sizeAfterExecution;
        this.graph1 = graph1;
        this.graph2 = graph2;
    }


}