// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.progression.heuristics.sasp.RtgBasedHeuristics;

import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntStack;

import java.util.*;

/**
 * Created by dh on 16.06.17.
 */
public abstract class RelaxedTaskGraph extends SasHeuristic {

    static int nodeCount;

    // this is only used for debugging
    static List<String> nodeNames = new ArrayList<>();

    static int[] precNodeToOp; // mapping of nodes that belong to preconditions to the corresponding operator
    static int[] opIndexToEffNode; // mapping of operator to its effect node

    static BitSet isAndNode = new BitSet(); // is it an AND-node? (it is an OR-node otherwise)

    // for each node, the int defines the *index* of the nodes to be activated before the respective node
    static int[][] waitingForNodes;

    /* for each node, the int defines the *number* of nodes to be activated before the respective node
     * - for or-nodes, this will be always one
     * - for and-nodes, it is the actual number of predecessors
     */
    static int[] initialWaitingForCount; // the original values

    /* for each node, it gives a list of nodes that wait that the respective node is activated */
    static int[][] whoIsWaitingForMe;

    static int[] precTnodes; // precondition nodes of actions without preconditions

    /* holds h-max for every node */
    public int[] hVal;
    int[] costs; // costs of activating a node (in addition to its predecessors -> this are action costs)
    int[] currentWaitingForCount;

    //
    // stuff that is needed to calculate LM-Cut
    //
    boolean trackPCF = true;
    protected int[] pcf; // maps an operator to one of its preconditions
    UUIntStack[] pcfInvert;
    protected int goalPCF;
    protected BitSet opReachable;
    public boolean earlyAbord = true;
    protected boolean evalBestAchievers = false;

    public RelaxedTaskGraph(SasPlusProblem p) {
        this(p, false);
    }

    public RelaxedTaskGraph(SasPlusProblem p, boolean trackPCF) {
        this.trackPCF = trackPCF;

        // init variable nodes
        List<List<Integer>> tempWaitingForNodes = new ArrayList<>();
        List<Integer> tempCosts = new ArrayList<>();
        for (int i = 0; i < p.numOfStateFeatures; i++) {
            tempWaitingForNodes.add(new ArrayList<>());
            isAndNode.set(i, false);
            tempCosts.add(0);
            nodeNames.add(p.factName(i));
        }

        // generate operator subgraphs
        int nodeID = p.numOfStateFeatures - 1; // current index of the last node
        opIndexToEffNode = new int[p.numOfOperators];
        Map<Integer, Integer> tempPrecNodeToOp = new HashMap<>();
        List<Integer> tempPrecTnodes = new ArrayList<>(); // nodes without precondition

        for (int iOperator = 0; iOperator < p.numOfOperators; iOperator++) {
            List<Integer> allPrecNodes = new ArrayList<>(); // currently one, but this might change due to ORs
            List<Integer> newPrecNode = new ArrayList<>();
            tempWaitingForNodes.add(newPrecNode);
            nodeID++;
            isAndNode.set(nodeID, true);
            tempCosts.add(0);
            tempPrecNodeToOp.put(nodeID, iOperator);
            allPrecNodes.add(nodeID);
            nodeNames.add("prec(" + p.opNames[iOperator] + ")");

            for (int iPrec = 0; iPrec < p.precLists[iOperator].length; iPrec++)
                newPrecNode.add(p.precLists[iOperator][iPrec]);

            // actions without preconditions
            if (p.precLists[iOperator].length == 0)
                tempPrecTnodes.add(nodeID);

            // effect node
            List<Integer> newEffNode = new ArrayList<>();
            tempWaitingForNodes.add(newEffNode);
            nodeID++;
            isAndNode.set(nodeID, true);
            tempCosts.add(p.costs[iOperator]);
            nodeNames.add("add(" + p.opNames[iOperator] + ")");
            opIndexToEffNode[iOperator] = nodeID;
            newEffNode.addAll(allPrecNodes); // this is waiting for its preconditions

            for (int iAddEff = 0; iAddEff < p.addLists[iOperator].length; iAddEff++)
                tempWaitingForNodes.get(p.addLists[iOperator][iAddEff]).add(nodeID); // the other node is waiting for me

            // todo: conditional effect
        }

        // copy temporal data structures to class members
        this.costs = new int[tempCosts.size()];
        for (int i = 0; i < tempCosts.size(); i++)
            this.costs[i] = tempCosts.get(i);

        List<List<Integer>> tempWhoIsWaitingForMe = new ArrayList<>();
        for (int i = 0; i < tempWaitingForNodes.size(); i++)
            tempWhoIsWaitingForMe.add(new ArrayList<>());

        this.waitingForNodes = new int[tempWaitingForNodes.size()][];
        this.initialWaitingForCount = new int[tempWaitingForNodes.size()];
        for (int i = 0; i < tempWaitingForNodes.size(); i++) {
            List<Integer> list = tempWaitingForNodes.get(i);
            this.waitingForNodes[i] = new int[list.size()];
            for (int j = 0; j < list.size(); j++) {
                this.waitingForNodes[i][j] = list.get(j);
                tempWhoIsWaitingForMe.get(list.get(j)).add(i);
            }
            if (isAndNode.get(i)) {
                initialWaitingForCount[i] = list.size();
            } else {
                initialWaitingForCount[i] = 1;
            }
        }

        this.whoIsWaitingForMe = new int[tempWhoIsWaitingForMe.size()][];
        for (int i = 0; i < tempWhoIsWaitingForMe.size(); i++) {
            List<Integer> list = tempWhoIsWaitingForMe.get(i);
            this.whoIsWaitingForMe[i] = new int[list.size()];
            for (int j = 0; j < list.size(); j++)
                this.whoIsWaitingForMe[i][j] = list.get(j);
        }

        this.precTnodes = new int[tempPrecTnodes.size()];
        for (int i = 0; i < tempPrecTnodes.size(); i++) {
            this.precTnodes[i] = tempPrecTnodes.get(i);
        }

        this.precNodeToOp = new int[tempWaitingForNodes.size()];
        for (int i = 0; i < this.precNodeToOp.length; i++) {
            if (tempPrecNodeToOp.containsKey(i))
                this.precNodeToOp[i] = tempPrecNodeToOp.get(i);
            else
                this.precNodeToOp[i] = -1;
        }

        // precondition choice function
        if (this.trackPCF) {
            pcf = new int[p.numOfOperators];
            pcfInvert = new UUIntStack[p.numOfStateFeatures];
            for (int i = 0; i < p.numOfStateFeatures; i++)
                pcfInvert[i] = new UUIntStack();
            opReachable = new BitSet(p.numOfOperators);
        }

        this.nodeCount = tempWaitingForNodes.size();

        hVal = new int[tempWaitingForNodes.size()]; // initialized in calc method

        /*for(int i = 0; i < nodeNames.size();i++)
            System.out.println(nodeNames.get(i));
        System.out.println();*/
    }

    protected void evalAchiever(int nodeId, int bestAchiver) {

    }


    abstract int eAND();

    abstract int eOR();

    abstract int combineAND(int x, int y);

    abstract int combineOR(int x, int y);


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph planGraph {");
        for (int i = 0; i < nodeNames.size(); i++) {
            sb.append("\tnode [shape=ellipse, label=\"" +
                    nodeNames.get(i) + "\"] node" + i + ";");
        }
        for (int i = 0; i < waitingForNodes.length; i++) {
            for (int j : waitingForNodes[i])
                sb.append("node" + i + "-> node" + j);
        }
        sb.append("}");

        return sb.toString();
    }
}
