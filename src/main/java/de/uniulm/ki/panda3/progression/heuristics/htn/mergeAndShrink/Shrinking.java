package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.Utils;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.HtnElementaryNode;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.HtnNodeValue;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.HtnShrinkNode;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import scala.Tuple2;
import scala.Tuple3;

import java.util.*;

public class Shrinking {


    public static HtnMsGraph shrinkingStep(SasPlusProblem p, HtnMsGraph graph, HashSet<HashSet<Integer>> idsToShrinkByIncomingEdges) {


        ArrayList<ArrayList<Integer>> aggregatedIDs = new ArrayList<>();

        for (HashSet<Integer> sameIDs: idsToShrinkByIncomingEdges){

            ArrayList<Integer> sameIdsArrayList = new ArrayList<>();
            sameIdsArrayList.addAll(sameIDs);
            aggregatedIDs.add(sameIdsArrayList);
        }


        HashMap<Integer, NodeValue> newIDMapping = new HashMap<>();

        ArrayList<Integer> indexesToReplace = new ArrayList<>();

        for (ArrayList<Integer> indexes : aggregatedIDs) {

            indexesToReplace.addAll(indexes);

        }

        HashMap<Integer, Integer> tempReverseIDMapping = new HashMap<>();

        Tuple3<Integer, Integer, Integer>[] oldEdges = graph.labelledEdges;


        int index = 0;

        Integer[] nodes = (Integer[]) graph.arrayVertices;

        for (int id : nodes) {

            if (!indexesToReplace.contains(id)) {

                newIDMapping.put(index, graph.idMapping.get(id));
                tempReverseIDMapping.put(id, index);
                index++;

            }

        }

        for (ArrayList<Integer> toAggregate : aggregatedIDs) {

            NodeValue newNodeValue = graph.idMapping.get(toAggregate.get(0));


            tempReverseIDMapping.put(toAggregate.get(0), index);

            //(1) ab hier mit unterem Code ersetzen, falls doch Shrink Node nötig

            Boolean isGoalNode = newNodeValue.isGoalNode();

            for (int j = 1; j < toAggregate.size(); j++) {

                NodeValue newNodeValue2 = graph.idMapping.get(toAggregate.get(j));

                if ((newNodeValue instanceof HtnNodeValue) && (newNodeValue2 instanceof HtnNodeValue)) {
                    /*HtnNodeValue newNodeValue12 = (HtnNodeValue) newNodeValue;
                    HtnNodeValue newNodeValue22 = (HtnNodeValue) newNodeValue2;
                    newNodeValue = new HtnShrinkNode(newNodeValue12, newNodeValue22, p);*/
                    tempReverseIDMapping.put(toAggregate.get(j), index);
                    if (newNodeValue2.isGoalNode()) isGoalNode = true;

                } else {
                    System.out.println("Wrong type!!");
                    System.exit(1);
                }

            }

            newNodeValue = new HtnElementaryNode(p, isGoalNode);

            /* falls doch mit shrink node Teil darüber ab (1) damit ersetzen:
            for (int j = 1; j < toAggregate.size(); j++) {

                NodeValue newNodeValue2 = graph.idMapping.get(toAggregate.get(j));

                if ((newNodeValue instanceof HtnNodeValue) && (newNodeValue2 instanceof HtnNodeValue)) {
                    HtnNodeValue newNodeValue12 = (HtnNodeValue) newNodeValue;
                    HtnNodeValue newNodeValue22 = (HtnNodeValue) newNodeValue2;
                    newNodeValue = new HtnShrinkNode(newNodeValue12, newNodeValue22, p);
                    tempReverseIDMapping.put(toAggregate.get(j), index);

                } else {
                    System.out.println("Wrong type!!");
                    System.exit(1);
                }

            }

            newNodeValue = new HtnElementaryNode(p, isGoalNode);*/

            newIDMapping.put(index, newNodeValue);
            index++;

        }

        Tuple3<Integer, Integer, Integer>[] newEdges = shrinkEdges(oldEdges, tempReverseIDMapping);

        int newStartID = tempReverseIDMapping.get(graph.startNodeID);


        HtnMsGraph newGraph = new HtnMsGraph(Utils.convertNodeIDArrayListToArray(newIDMapping), newEdges, newIDMapping, newStartID);

        return newGraph;
    }

    public static Tuple3<Integer, Integer, Integer>[] shrinkEdges(Tuple3<Integer, Integer, Integer>[] oldEdges, HashMap<Integer, Integer> tempReverseIDMapping) {

        Set<Tuple3<Integer, Integer, Integer>> shrinkedEdges = new HashSet();

        for (Tuple3<Integer, Integer, Integer> edge : oldEdges) {

            Tuple3<Integer, Integer, Integer> shrinkedEdge = new Tuple3<>(tempReverseIDMapping.get(edge._1()), edge._2(), tempReverseIDMapping.get(edge._3()));
            if (!shrinkedEdges.contains(shrinkedEdge)) shrinkedEdges.add(shrinkedEdge);
        }


        ArrayList<Tuple3<Integer, Integer, Integer>> finalLists = new ArrayList<>();
        finalLists.addAll(shrinkedEdges);
        return Utils.convertEdgeArrayListToTuple3(finalLists);
    }

}

