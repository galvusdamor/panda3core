package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Andrea on 23.04.2018.
 */
public class MergingStrategy1 extends MergingStrategy{


    public ClassicalMSGraph merge(SasPlusProblem p, ClassicalMSGraph graph, int shrinkingBound, long seed, ShrinkingStrategy shrinkingStrategy){


        Set<Integer> notYetUsedVariables = getNotYetUsedVariables(p, graph);

        ArrayList<Integer> variablesToMerge = new ArrayList<>(notYetUsedVariables);

        ArrayList<Integer> allGoalVars = new ArrayList<>();

        HashSet<Integer> goalVariables = new HashSet<>();

        if (graph==null){
            goalVariables = SingleGraphMethods.getGoalVariables(p);
        }else{
            goalVariables = graph.goalVariables;
        }

        for (int var : variablesToMerge) if (goalVariables.contains(var)) allGoalVars.add(var);

        if (allGoalVars.size() > 0) {
            variablesToMerge = allGoalVars;
            //System.out.println("Goal var left");
        } else {
            //System.out.println("No Goal var left");
        }


        ClassicalMSGraph newGraph;

        if (!(graph==null)) {

            //System.out.println("Not yet used Variables: " + notYetUsedVariables);

            int randomVarIndex = Utils.randomIntGenerator(variablesToMerge.size(), seed);

            int randomVar = variablesToMerge.get(randomVarIndex);

            //System.out.println("Merge with Variable: " + randomVar);


            newGraph = mergeWithVar(p, graph, randomVar, shrinkingBound, shrinkingStrategy);


        }else{

            int randomVarIndex = Utils.randomIntGenerator(variablesToMerge.size(), seed);

            int randomVar = variablesToMerge.get(randomVarIndex);

            //System.out.println("Start with Variable: " + randomVar);

            newGraph = SingleGraphMethods.getSingleGraphForVarIndex(p, randomVar);

        }

        return newGraph;


    }





}
