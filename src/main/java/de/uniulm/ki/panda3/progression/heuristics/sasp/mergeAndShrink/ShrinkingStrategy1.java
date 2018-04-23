package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andrea on 23.04.2018.
 */
public class ShrinkingStrategy1 extends ShrinkingStrategy{



    public ClassicalMSGraph shrink(SasPlusProblem p, ClassicalMSGraph graph){



        HashMap<Integer,Integer> distancesFromClosestGoalNode = getDistancesFromGoal(p, graph);

        HashMap<Integer,Integer> distancesFromStartNode = getDistancesFromStart(p, graph);


        HashMap<Integer,Integer> summedDistances = new HashMap<>();

        for (int id:graph.idMapping.keySet()){

            //System.out.println("ID: " + id);
            //System.out.println(distancesFromClosestGoalNode);
            //System.out.println(distancesFromStartNode);
            //System.out.println(distancesFromClosestGoalNode.containsKey(id));
            //System.out.println(distancesFromStartNode.containsKey(id));

            int distanceFromStart = distancesFromStartNode.get(id);
            int distanceFromGoal = distancesFromClosestGoalNode.get(id);
            summedDistances.put(id,distanceFromStart+distanceFromGoal);
        }

        //System.out.println(summedDistances);

        HashMap<Integer,Integer> originalSummedDistances = new HashMap<>();
        originalSummedDistances.putAll(summedDistances);


        ArrayList<Integer> currentMaxDistanceNodes;
        while (true) {
            if (summedDistances.isEmpty()){
                int first = -1;
                int second = -1;

                while (true) {
                    if (originalSummedDistances.isEmpty()){
                        currentMaxDistanceNodes = new ArrayList<>();
                        currentMaxDistanceNodes.add(first);
                        currentMaxDistanceNodes.add(second);
                        break;
                    }
                    ArrayList<Integer> farthestNodesFromStartAndGoal = getNodesFarthestFromStartAndGoal(graph, originalSummedDistances);
                    if (farthestNodesFromStartAndGoal.size() == 1){
                        int maxNode = farthestNodesFromStartAndGoal.get(0);
                        if (first == -1) first = maxNode;
                        else if (second == -1) second = maxNode;
                        originalSummedDistances.remove(maxNode);
                    } else {
                        currentMaxDistanceNodes = farthestNodesFromStartAndGoal;
                        break;
                    }
                }
                break;
            }

            ArrayList<Integer> farthestNodesFromStartAndGoal = getNodesFarthestFromStartAndGoal(graph, summedDistances);

            int maxDistanceToInit = 0;
            currentMaxDistanceNodes = new ArrayList<Integer>();
            for (int i : farthestNodesFromStartAndGoal) {
                int distToStart = distancesFromStartNode.get(i);
                if (distToStart > maxDistanceToInit) {
                    maxDistanceToInit = distToStart;
                    currentMaxDistanceNodes.clear();
                }

                if (distToStart == maxDistanceToInit)
                    currentMaxDistanceNodes.add(i);
            }

            if (currentMaxDistanceNodes.size() == 1){
                int maxNode = currentMaxDistanceNodes.get(0);
                summedDistances.remove(maxNode);
                distancesFromStartNode.remove(maxNode);
            } else break;
        }


        ArrayList<ArrayList<Integer>> nodesToShrink = new ArrayList<>();
        nodesToShrink.add(currentMaxDistanceNodes);

        ClassicalMSGraph shrinkedGraph = shrinkingStep(p,graph, nodesToShrink);


        return shrinkedGraph;

    }






}