package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andrea on 20.11.2017.
 */
abstract class MultiValuedDecisionTree{




    abstract int getNodeID(int[] state);
    abstract LeafNode getNodeOfState(HashMap<Integer,Integer> variableValueMapping);




}

class Node extends MultiValuedDecisionTree{

    ArrayList<MultiValuedDecisionTree> successors;
    int variableIndex;
    HashMap<Integer,Integer> successorMapping;

    public Node(ArrayList<MultiValuedDecisionTree> successors, int variableIndex, HashMap<Integer,Integer> successorMapping){

        this.successors=successors;
        this.variableIndex=variableIndex;
        this.successorMapping=successorMapping;

    }

    public int getNodeID(int[] state){

        int valueOfVariable = state[variableIndex];

        int successorIndex = successorMapping.get(valueOfVariable);

        MultiValuedDecisionTree successor = successors.get(successorIndex);

        return successor.getNodeID(state);
    }

    public LeafNode getNodeOfState(HashMap<Integer,Integer> variableValueMapping){

        int valueOfVariable = variableValueMapping.get(variableIndex);

        int successorIndex = successorMapping.get(valueOfVariable);

        MultiValuedDecisionTree successor = successors.get(successorIndex);

        return successor.getNodeOfState(variableValueMapping);

    }


}

class LeafNode extends MultiValuedDecisionTree{

    int NodeID;

    public LeafNode(int NodeID){

        this.NodeID = NodeID;

    }

    public int getNodeID(int[] state){

        return NodeID;
    }

    public LeafNode getNodeOfState(HashMap<Integer,Integer> variableValueMapping){

        return this;

    }

}









