package de.uniulm.ki.panda3.progression.sasp.heuristics;

import de.uniulm.ki.panda3.progression.proUtil.UUIntStack;
import de.uniulm.ki.panda3.progression.sasp.SasPlusProblem;

import java.util.BitSet;

/**
 * Created by dh on 02.05.17.
 */
public class hLmCut extends hMax {
    private final int[][] addToTask;
    private final int numOfOperators;
    private final int[][] addLists;
    private final String[] opNames;
    private int numOfStateFeatures;

    public hLmCut(SasPlusProblem p) {
        super(p, true);
        assert (p.correctModel());
        this.numOfStateFeatures = p.numOfStateFeatures;
        this.addToTask = p.addToTask;
        this.numOfOperators = p.numOfOperators;
        this.addLists = p.addLists;
        this.opNames = p.opNames;
    }

    @Override
    public int calcHeu(int[] s0, int[] g) {
        BitSet s0set = new BitSet(numOfStateFeatures);
        for (int f : s0)
            s0set.set(f);
        BitSet goalSet = new BitSet();
        for (int gf : g)
            goalSet.set(gf);
        return hLmCut(s0set, goalSet);
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        return hLmCut(s0, g);
    }

    private int hLmCut(BitSet s0, BitSet g) {
        int hMax = super.calcHeu(s0, g);

        if ((hMax == 0) || (hMax == Integer.MAX_VALUE))
            return hMax;
        int[] orgNodeCosts = this.costs.clone(); // the costs are changed in this method -> save costs

        int lmCutHeu = 0;
        while (hMax > 0) {
            assert (debugOut("hMax: " + hMax + "\n"));
            // compute goal-zone
            BitSet goalZone = new BitSet(this.numOfStateFeatures);
            BitSet cut = new BitSet(this.numOfOperators);
            BitSet testReachability = new BitSet(this.numOfStateFeatures);
            UUIntStack fringe = new UUIntStack();
            fringe.push(this.goalPCF);
            while (!fringe.isEmpty()) {
                int fact = fringe.pop();
                assert (debugOut("gz-fact: " + nodeNames.get(fact) + "\n"));
                for (int producer : addToTask[fact]) {
                    if (!opReachable.get(producer))
                        continue;
                    assert (debugOut("producer: " + this.opNames[producer] + "\n"));

                    int singlePrec = pcf[producer];
                    if (goalZone.get(singlePrec)) {
                        continue;
                    }
                    assert (debugOut("with prec: " + nodeNames.get(singlePrec) + "\n"));

                    if (this.costs[this.opIndexToEffNode[producer]] == 0) {
                        goalZone.set(singlePrec);
                        fringe.push(singlePrec);
                    } else {
                        cut.set(producer);
                        testReachability.set(singlePrec);
                    }
                }
            }

            int goalZ = goalZone.nextSetBit(0);
            assert (debugOut("Goal-Zone: "));
            while (goalZ >= 0) {
                assert (debugOut(nodeNames.get(goalZ) + " "));
                goalZ = goalZone.nextSetBit(goalZ + 1);
            }
            assert (debugOut("\n"));

            // check forward-reachability
            BitSet reachableFacts = (BitSet) s0.clone();
            UUIntStack newFacts = new UUIntStack();
            int reachableFact = s0.nextSetBit(0);
            while (reachableFact >= 0) {
                newFacts.push(reachableFact);
                reachableFact = s0.nextSetBit(reachableFact + 1);
            }
            reachabliltity:
            while (!newFacts.isEmpty()) {
                UUIntStack addEffects = new UUIntStack();
                while (!newFacts.isEmpty()) {
                    reachableFact = newFacts.pop();
                    reachableFacts.set(reachableFact, true);
                    testReachability.set(reachableFact, false);
                    if (testReachability.isEmpty())
                        break reachabliltity;

                    UUIntStack operators = pcfInvert[reachableFact];
                    operators.resetIterator();
                    while (operators.hasNext()) {
                        int action = operators.next();
                        for (int addEff : this.addLists[action]) {
                            if (!reachableFacts.get(addEff)) {
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

            // calculate costs
            int minCosts = Integer.MAX_VALUE;
            int cutted = cut.nextSetBit(0);
            while (cutted >= 0) {
                minCosts = Integer.min(minCosts, this.costs[this.opIndexToEffNode[cutted]]);
                cutted = cut.nextSetBit(cutted + 1);
            }
            assert minCosts > 0;
            lmCutHeu += minCosts;

            // decrease action costs of cut
            assert (debugOut("Cut: " + "\n"));
            cutted = cut.nextSetBit(0);
            while (cutted >= 0) {
                this.costs[this.opIndexToEffNode[cutted]] -= minCosts;
                assert (debugOut(cutted + " " + nodeNames.get(this.opIndexToEffNode[cutted]) + "\n"));
                cutted = cut.nextSetBit(cutted + 1);
            }
            hMax = super.calcHeu(s0, g);
        }
        this.costs = orgNodeCosts; // restore original cost values
        return lmCutHeu;
    }
}
