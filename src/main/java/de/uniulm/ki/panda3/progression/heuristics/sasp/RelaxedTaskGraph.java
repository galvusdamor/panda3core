package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntPairPriorityQueue;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntStack;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;

import java.util.*;

/**
 * Created by dh on 28.04.17.
 */
public abstract class RelaxedTaskGraph extends SasHeuristic {

    private final int[] waitingForCountORG;

    int[] costs; // costs of activating a node (in addition to its predecessors -> this are action costs)

    // this is only used for debugging
    List<String> nodeNames = new ArrayList<>();

    int[] precNodeToOp; // mapping of nodes that belong to preconditions to the corresponding operator
    int[] opIndexToEffNode; // mapping of operator to its effect node

    BitSet isAndNode = new BitSet(); // is it an AND-node? (it is an OR-node otherwise)

    // for each node, the int defines the *index* of the nodes to be activated before the respective node
    int[][] waitingForNodes;

    /* for each node, the int defines the *number* of nodes to be activated before the respective node
     * - for or-nodes, this will be always one
     * - for and-nodes, it is the actual number of predecessors
     */
    int[] waitingForCount;

    /* for each node, it gives a list of nodes that wait that the respective node is activated */
    int[][] whoIsWaitingForMe;

    int[] precTnodes; // precondition nodes of actions without preconditions

    /* holds h-max for every node */
    int[] hVal;

    //
    // stuff that is needed to calculate LM-Cut
    //
    boolean trackPCF = true;
    protected int[] pcf; // maps an operator to one of its preconditions
    UUIntStack[] pcfInvert;
    protected int goalPCF;
    protected BitSet opReachable;
    protected boolean earlyAbord = true;
    protected boolean evalBestAchievers = false;

    abstract int eAND();

    abstract int eOR();

    abstract int combineAND(int x, int y);

    abstract int combineOR(int x, int y);

    public RelaxedTaskGraph(SasPlusProblem p) {
        this(p, false);
    }

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

    public RelaxedTaskGraph(SasPlusProblem p, boolean trackPCF) {
        super(p);
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
        this.waitingForCountORG = new int[tempWaitingForNodes.size()];
        for (int i = 0; i < tempWaitingForNodes.size(); i++) {
            List<Integer> list = tempWaitingForNodes.get(i);
            this.waitingForNodes[i] = new int[list.size()];
            for (int j = 0; j < list.size(); j++) {
                this.waitingForNodes[i][j] = list.get(j);
                tempWhoIsWaitingForMe.get(list.get(j)).add(i);
            }
            if (isAndNode.get(i)) {
                waitingForCountORG[i] = list.size();
            } else {
                waitingForCountORG[i] = 1;
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

        hVal = new int[tempWaitingForNodes.size()]; // initialized in calc method

        /*for(int i = 0; i < nodeNames.size();i++)
            System.out.println(nodeNames.get(i));
        System.out.println();*/
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        if (this.trackPCF) {
            pcf = new int[pcf.length];
            for (int i = 0; i < pcfInvert.length; i++)
                pcfInvert[i].clear();
            opReachable.clear();
        }

        waitingForCount = waitingForCountORG.clone();
        for (int i = 0; i < hVal.length; i++) {
            hVal[i] = Integer.MAX_VALUE;
        }
        UUIntPairPriorityQueue activatable = new UUIntPairPriorityQueue();

        // init queue by adding s0
        int nextF = s0.nextSetBit(0);
        while (nextF >= 0) {
            activatable.add(this.sortedIndex(nextF, 0));
            waitingForCount[nextF] = 0;
            nextF = s0.nextSetBit(nextF + 1);
        }

        // dummy prec-nodes of actions that do not have preconditions
        for (int prec : precTnodes) {
            activatable.add(this.sortedIndex(prec, 0 + costs[prec]));
            waitingForCount[prec] = 0;
        }

        return calcHeuLoop(activatable, (BitSet) g.clone(), this.earlyAbord);
    }

    private int calcHeuLoop(UUIntPairPriorityQueue activatable, BitSet goal, boolean earlyAbord) {
        int hValGoal = eAND();
        boolean goalReached = false;

        while (!activatable.isEmpty()) {
            int newNode = activatable.minPair()[1];
            if (goal.get(newNode)) {
                goal.set(newNode, false);
                int old = hValGoal;
                hValGoal = combineAND(hValGoal, hVal[newNode]);
                if (old != hValGoal)
                    this.goalPCF = newNode;
                if (goal.isEmpty()) {
                    goalReached = true;
                    if (earlyAbord)
                        break;
                }
            }
            for (int i = 0; i < whoIsWaitingForMe[newNode].length; i++) {
                int waitingNode = whoIsWaitingForMe[newNode][i];
                waitingForCount[waitingNode]--;
                if (waitingForCount[waitingNode] == 0) {
                    activatable.add(this.sortedIndex(waitingNode));
                }
            }
        }
        if (goalReached)
            return hValGoal;
        else
            return Integer.MAX_VALUE;
    }


    private int[] sortedIndex(int index, int hMaxVal) {
        hVal[index] = hMaxVal;
        int[] res = new int[2];
        res[0] = hVal[index];
        res[1] = index;
        return res;
    }

    private int[] sortedIndex(int index) {
        int[] res = new int[2];
        res[1] = index;

        int predCosts;
        if (isAndNode.get(index)) {
            int relPrec = -1;

            predCosts = eAND();
            for (int i = 0; i < waitingForNodes[index].length; i++) {
                int old = predCosts;
                predCosts = combineAND(predCosts, hVal[waitingForNodes[index][i]]);
                if (old != predCosts)
                    relPrec = waitingForNodes[index][i];
            }

            int op = precNodeToOp[index];
            if ((trackPCF) && (op > -1)) {
                opReachable.set(op);// mark operator as reached
                pcf[op] = relPrec; // mark which precondition has been the limiting factor
                pcfInvert[relPrec].push(op);
            }
        } else {
            predCosts = eOR();
            int bestAchiver = -1;
            for (int i = 0; i < waitingForNodes[index].length; i++) {
                int old = predCosts;
                predCosts = combineOR(predCosts, hVal[waitingForNodes[index][i]]);
                if (old != predCosts)
                    bestAchiver = waitingForNodes[index][i];
            }
            assert bestAchiver >= 0;
            if (evalBestAchievers) {
                this.evalAchiever(index, bestAchiver);
            }
        }

        hVal[index] = predCosts + costs[index];
        res[0] = hVal[index];
        return res;
    }

    protected void evalAchiever(int nodeId, int bestAchiver) {

    }
}
