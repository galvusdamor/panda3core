package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntStack;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;

import java.util.*;

/**
 * Created by dh on 02.05.17.
 */
public class hLmCut extends hMax {
    private final int[][] addToTask;
    private final int numOfOperators;
    private final int[][] addLists;
    private final String[] opNames;
    private final String[] factStrs;
    private int numOfStateFeatures;

    public hLmCut(SasPlusProblem p, boolean incremental) {
        super(p, true);
        this.earlyAbord = false;
        this.trackPCF = true;
        assert (p.correctModel());
        this.numOfStateFeatures = p.numOfStateFeatures;
        this.addToTask = p.addToTask;
        this.numOfOperators = p.numOfOperators;
        this.addLists = p.addLists;
        this.opNames = p.opNames;
        this.factStrs = p.factStrs;
        this.isIncremental = incremental;
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        return hLmCut(s0, g);
    }

    private int hLmCut(BitSet s0, BitSet g) {
        int[] orgNodeCosts = this.costs.clone();
        IncInfLmCut incrementInfo = null;
        int lmCutHeu = 0;

        if (isIncremental) {
            incrementInfo = new IncInfLmCut();
            IncInfLmCut i = (IncInfLmCut) this.increment;
            assert i.costsGrZero(this.costs, this.opIndexToEffNode);
            assert i.disjunct();
            i.costs.resetIterator();
            for (BitSet cut : i.cuts) {
                int costs = i.costs.next();
                assert costs > 0;
                if (!cut.get(lastAction)) {
                    lmCutHeu += costs;
                    incrementInfo.cuts.add(cut);
                    incrementInfo.costs.push(costs);
                    int cutted = cut.nextSetBit(0);
                    while (cutted >= 0) {
                        this.costs[this.opIndexToEffNode[cutted]] -= costs;
                        assert (this.costs[this.opIndexToEffNode[cutted]] >= 0);
                        debugOut(cutted + " " + opNames[cutted] + "\n");
                        cutted = cut.nextSetBit(cutted + 1);
                    }
                }
            }
        }

        int hMax = super.calcHeu(s0, g);
        if ((hMax == 0) || (hMax == Integer.MAX_VALUE)) {
            this.costs = orgNodeCosts; // restore original cost values
            if (hMax == 0)
                return lmCutHeu;
            else
                return hMax;
        }

        while (hMax > 0) {
            debugOut("hMax: " + hMax + "\n");
            // compute goal-zone
            BitSet goalZone = new BitSet(this.numOfStateFeatures);
            BitSet cut = new BitSet(this.numOfOperators);
            BitSet precsOfCutNodes = new BitSet(this.numOfStateFeatures);
            goalZone(goalZone, cut, precsOfCutNodes);

            // check forward-reachability
            forwardReachability(s0, cut, goalZone, precsOfCutNodes);

            // calculate costs
            int minCosts = Integer.MAX_VALUE;
            int cutted = cut.nextSetBit(0);
            while (cutted >= 0) {
                minCosts = Integer.min(minCosts, this.costs[this.opIndexToEffNode[cutted]]);
                cutted = cut.nextSetBit(cutted + 1);
            }
            assert minCosts > 0;
            lmCutHeu += minCosts;

            if (isIncremental) {
                incrementInfo.cuts.add(cut);
                incrementInfo.costs.push(minCosts);
            }

            // decrease action costs of cut
            debugOut("Cut: " + "\n");
            cutted = cut.nextSetBit(0);
            while (cutted >= 0) {
                this.costs[this.opIndexToEffNode[cutted]] -= minCosts;
                assert (this.costs[this.opIndexToEffNode[cutted]] >= 0);
                debugOut(cutted + " " + opNames[cutted] + "\n");
                cutted = cut.nextSetBit(cutted + 1);
            }
            hMax = super.calcHeu(s0, g);
        }

        this.costs = orgNodeCosts; // restore original cost values
        this.increment = incrementInfo;
        return lmCutHeu;
    }

    private UUIntStack fringe = new UUIntStack();

    private void goalZone(BitSet goalZone, BitSet cut, BitSet precsOfCutNodes) {
        fringe.clear();
        fringe.push(this.goalPCF);
        while (!fringe.isEmpty()) {
            int fact = fringe.pop();
            debugOut("gz-fact: " + nodeNames.get(fact) + "\n");
            for (int producer : addToTask[fact]) {
                debugOut("producer: " + this.opNames[producer] + "\n");
                if (!opReachable.get(producer)) {
                    debugOut("unreachable\n");
                    continue;
                }

                int singlePrec = pcf[producer];
                if (goalZone.get(singlePrec)) {
                    continue;
                }
                debugOut("with prec: " + nodeNames.get(singlePrec) + "\n");

                if (this.costs[this.opIndexToEffNode[producer]] == 0) {
                    goalZone.set(singlePrec);
                    fringe.push(singlePrec);
                } else {
                    cut.set(producer);
                    precsOfCutNodes.set(singlePrec);
                }
            }
        }
        fringe.clear();

        int goalZ = goalZone.nextSetBit(0);
        debugOut("Goal-Zone: ");
        while (goalZ >= 0) {
            debugOut(nodeNames.get(goalZ) + " ");
            goalZ = goalZone.nextSetBit(goalZ + 1);
        }
        debugOut("\n");
    }

    private void forwardReachability(BitSet s0, BitSet cut, BitSet goalZone, BitSet testReachability) {
        // put s0 facts into list of reachable facts
        BitSet reachableFacts = (BitSet) s0.clone();
        UUIntStack newFacts = new UUIntStack();
        int reachableFact = s0.nextSetBit(0);
        while (reachableFact >= 0) {
            newFacts.push(reachableFact);
            reachableFact = s0.nextSetBit(reachableFact + 1);
        }

        // calculate reachability
        reachability:
        while (!newFacts.isEmpty()) {
            UUIntStack addEffects = new UUIntStack();
            while (!newFacts.isEmpty()) {
                reachableFact = newFacts.pop();
                testReachability.set(reachableFact, false);
                if (testReachability.isEmpty())
                    break reachability;

                UUIntStack operators = pcfInvert[reachableFact];
                operators.resetIterator();
                while (operators.hasNext()) {
                    int action = operators.next();
                    for (int addEff : this.addLists[action]) {
                        if (goalZone.get(addEff))
                            continue;
                        if (!reachableFacts.get(addEff)) {
                            reachableFacts.set(reachableFact, true);
                            addEffects.push(addEff);
                        }
                    }
                }
            }
            newFacts = addEffects;
        }

        // delete unreachable actions
        if (!testReachability.isEmpty()) { // some are not reachable
            int unreachableFact = testReachability.nextSetBit(0);
            while (unreachableFact >= 0) {
                UUIntStack unreachableOps = pcfInvert[unreachableFact];
                unreachableOps.resetIterator();
                while (unreachableOps.hasNext()) {
                    cut.set(unreachableOps.next(), false);
                }
                unreachableFact = testReachability.nextSetBit(unreachableFact + 1);
            }
        }
    }

    private String dotJustificationGraph(BitSet s0, BitSet goalZone) {
        int initKey = Integer.MAX_VALUE;
        Map<Integer, String> nodes = new HashMap<>();
        Map<Integer, Set<Integer>> edges = new HashMap<>();
        Map<Integer, Map<Integer, String>> edgeLabel = new HashMap<>();
        nodes.put(initKey, "i");
        UUIntStack features = new UUIntStack();
        int f = s0.nextSetBit(0);
        while (f >= 0) {
            features.push(f);
            String name = this.factStrs[f];
            nodes.put(f, name);
            if (!edges.containsKey(initKey))
                edges.put(initKey, new HashSet<>());
            edges.get(initKey).add(f);

            if (!edgeLabel.containsKey(initKey))
                edgeLabel.put(initKey, new HashMap<>());
            edgeLabel.get(initKey).put(f, "s0");
            f = s0.nextSetBit(f + 1);
        }

        while (!features.isEmpty()) {
            UUIntStack addEffects = new UUIntStack();
            while (!features.isEmpty()) {
                f = features.pop();

                UUIntStack operators = pcfInvert[f];
                operators.resetIterator();
                while (operators.hasNext()) {
                    int action = operators.next();
                    for (int addEff : this.addLists[action]) {
                        if (!nodes.containsKey(addEff)) {
                            addEffects.push(addEff);


                            String name = this.factStrs[f];
                            nodes.put(f, name);
                        }
                        if (!edges.containsKey(f))
                            edges.put(f, new HashSet<>());
                        edges.get(f).add(addEff);
                        if (!edgeLabel.containsKey(f))
                            edgeLabel.put(f, new HashMap<>());
                        edgeLabel.get(f).put(addEff, this.opNames[action]);

                    }
                }
            }
            features = addEffects;
        }

        nodes.put(Integer.MAX_VALUE - 1, "g");
        if (!edges.containsKey(this.goalPCF))
            edges.put(this.goalPCF, new HashSet<>());
        edges.get(this.goalPCF).add(Integer.MAX_VALUE - 1);

        Set<Integer> doNotPrint = new HashSet<>();
        //doNotPrint.add(initKey);
        for (int k : nodes.keySet()) {
            if (!edges.containsKey(k))
                doNotPrint.add(k);
        }
        doNotPrint.remove(Integer.MAX_VALUE - 1);

        boolean changed = true;
        while (changed) {
            int count = doNotPrint.size();
            for (int k : edges.keySet()) {
                Set<Integer> set = edges.get(k);
                if (doNotPrint.containsAll(set)) {
                    doNotPrint.add(k);
                }
                if (set.isEmpty()) {
                    doNotPrint.add(k);
                }
            }
            changed = (doNotPrint.size() != count);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n\n\tsubgraph cluster_0 {\n\t\tlabel = \"goal zone\";\n");
        for (int key : nodes.keySet()) {
            if (doNotPrint.contains(key))
                continue;
            if (goalZone.get(key)) {
                sb.append("\t\t\tnode [label=\"" + nodes.get(key) + "\"] node" + key + ";\n");
            }
        }
        sb.append("\t}\n");
        for (int key : nodes.keySet()) {
            if (doNotPrint.contains(key))
                continue;
            if (!goalZone.get(key)) {
                sb.append("\t\t\tnode [label=\"" + nodes.get(key) + "\"] node" + key + ";\n");
            }
        }
        for (int from : edges.keySet()) {
            Set<Integer> toSet = edges.get(from);
            for (int to : toSet) {
                if (doNotPrint.contains(from) || doNotPrint.contains(to))
                    continue;
                String label = edgeLabel.get(from).get(to);
                sb.append("\tnode" + from + " -> node" + to + "[label=\"" + label + "\"];\n");
            }
        }
        sb.append("}\n");
        return sb.toString();
    }
}
