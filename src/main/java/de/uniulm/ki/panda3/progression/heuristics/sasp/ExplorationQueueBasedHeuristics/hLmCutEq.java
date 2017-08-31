package de.uniulm.ki.panda3.progression.heuristics.sasp.ExplorationQueueBasedHeuristics;

import de.uniulm.ki.panda3.progression.heuristics.sasp.IncrementalCalc.IncInfLmCut;
import de.uniulm.ki.panda3.progression.heuristics.sasp.IncrementalCalc.IncrementInformation;
import de.uniulm.ki.panda3.progression.heuristics.sasp.RtgBasedHeuristics.hMaxRtg;
import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntPairPriorityQueue;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntStack;

import java.util.*;

/**
 * Created by dh on 22.06.17.
 */
public class hLmCutEq extends SasHeuristic {
    private final int[] maxPrecInit;
    private final int[] hValInit;
    private final int[] precLessOps;

    SasPlusProblem p;
    private int[] unsatPrecs;
    //private int numGoals; // only for hMax

    private int[] hVal;
    private int[] maxPrec;
    //private BitSet[] maxPrecInv;
    private int maxPrecG;
    private int[] costs;
    private IncInfLmCut myIncInf;

    // necessary for debug
    //private BitSet s0;

    @Override
    public String toString() {
        if (isIncremental)
            return "inc-hLM-Cut-EQ";
        else
            return "hLM-Cut-EQ";
    }

    public hLmCutEq(SasPlusProblem p, boolean incremental) {
        this.p = p;
        this.isIncremental = incremental;
        this.maxPrecInit = new int[p.numOfOperators];
        for (int i = 0; i < maxPrecInit.length; i++)
            maxPrecInit[i] = -1;
        this.hValInit = new int[p.numOfStateFeatures];
        for (int i = 0; i < hValInit.length; i++)
            hValInit[i] = cUnreachable;
        /*this.maxPrecInv = new BitSet[p.numOfStateFeatures];
        for (int i = 0; i < p.numOfStateFeatures; i++)
            maxPrecInv[i] = new BitSet();*/

        // get actions without preconditions
        List<Integer> tempPrecLess = new ArrayList<>();
        for (int i = 0; i < p.numOfOperators; i++) {
            if (p.addLists[i].length == 0)
                tempPrecLess.add(i);
        }
        precLessOps = new int[tempPrecLess.size()];
        for (int i = 0; i < tempPrecLess.size(); i++)
            precLessOps[i] = tempPrecLess.get(i);
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        return calcHeu(-1, null, s0, g);
    }

    @Override
    public int calcHeu(int lastAction, IncrementInformation inc, BitSet s0, BitSet g) {
        int hLmCut = 0;
        this.costs = p.costs.clone();

        if (isIncremental) {
            myIncInf = new IncInfLmCut();
            hLmCut = IncrementalLmCut(lastAction, (IncInfLmCut) inc);
        }

        g.andNot(s0);
        int hMax = hMax(s0, g);
        if (hMax == 0)
            return hLmCut;
        else if (hMax == cUnreachable)
            return hMax;

        while (hMax > 0) {
            //assert implementationEquality(s0, g);
            //assert reachablilityOK(s0, this.hVal); // this is less restrictive than the one above

            BitSet goalZone = new BitSet(p.numOfStateFeatures);
            BitSet cut = new BitSet(p.numOfOperators);
            BitSet precsOfCutNodes = new BitSet(p.numOfStateFeatures);
            goalZone(goalZone, cut, precsOfCutNodes);
            assert cut.cardinality() > 0;

            // check forward-reachability
            forwardReachabilityDFS(s0, cut, goalZone, precsOfCutNodes);
            //forwardReachabilityBFS(s0, cut, goalZone, precsOfCutNodes);
            assert cut.cardinality() > 0;

            // calculate costs
            int minCosts = Integer.MAX_VALUE;
            for (int cutted = cut.nextSetBit(0); cutted >= 0; cutted = cut.nextSetBit(cutted + 1)) {
                minCosts = Integer.min(minCosts, costs[cutted]);
            }
            assert minCosts > 0;
            hLmCut += minCosts;

            if (isIncremental) {
                myIncInf.cuts.add(toIntArray(cut));
                myIncInf.costs.push(minCosts);
            }

            // decrease action costs of cut
            if (debug) {
                debugOut("Cut: " + "\n");
                for (int cutted = cut.nextSetBit(0); cutted >= 0; cutted = cut.nextSetBit(cutted + 1)) {
                    debugOut(cutted + " " + p.opNames[cutted] + "\n");
                }
            }

            hMax = costUpdate(cut, minCosts, g);
        }

        return hLmCut;
    }

    @Override
    public IncrementInformation getIncInf() {
        return this.myIncInf;
    }

    private int IncrementalLmCut(int lastAction, IncInfLmCut parentIncInf) {
        int lmCutHeu = 0;
        //assert parentIncInf.costsGreaterZero(this.costs);
        //assert parentIncInf.cutsAreDisjunctive();
        parentIncInf.costs.resetIterator();
        for (int[] cut : parentIncInf.cuts) {
            int costs = parentIncInf.costs.next();
            assert costs > 0;
            if (!binContains(cut, lastAction, 0, cut.length - 1)) {
                lmCutHeu += costs;
                myIncInf.cuts.add(cut);
                myIncInf.costs.push(costs);
                for (int op : cut) {
                    this.costs[op] -= costs;
                    assert (this.costs[op] >= 0);
                    debugOut(op + " " + p.opNames[op] + "\n");
                }
            }
        }
        return lmCutHeu;
    }

    private boolean binContains(int[] ar, int elem, int firstI, int lastI) {
        if (lastI < firstI)
            return false;
        int mid = (firstI + lastI) / 2;
        if (ar[mid] == elem)
            return true;
        else if (ar[mid] < elem)
            return binContains(ar, elem, mid + 1, lastI);
        else
            return binContains(ar, elem, firstI, mid - 1);
    }

    private int[] toIntArray(BitSet cut) {
        int[] res = new int[cut.cardinality()];
        int i = 0;
        for (int op = cut.nextSetBit(0); op >= 0; op = cut.nextSetBit(op + 1))
            res[i++] = op;
        return res;
    }


    UUIntStack fringe = new UUIntStack();

    private void goalZone(BitSet goalZone, BitSet cut, BitSet precsOfCutNodes) {
        fringe.clear();
        fringe.push(this.maxPrecG);
        while (!fringe.isEmpty()) {
            int fact = fringe.pop();
            for (int producer : p.addToTask[fact]) {
                if (unsatPrecs[producer] > 0) // not reachable
                    continue;

                int singlePrec = maxPrec[producer];
                if (goalZone.get(singlePrec))
                    continue;

                if (this.costs[producer] == 0) {
                    goalZone.set(singlePrec);
                    precsOfCutNodes.set(singlePrec, false);
                    fringe.push(singlePrec);
                } else {
                    cut.set(producer);
                    precsOfCutNodes.set(singlePrec);
                }
            }
        }
    }


    private void forwardReachabilityDFS(BitSet s0, BitSet cut, BitSet goalZone, BitSet testReachability) {
        UUIntStack stack = new UUIntStack(100);

        BitSet remove = new BitSet();
        for (int f = testReachability.nextSetBit(0); f >= 0; f = testReachability.nextSetBit(f + 1)) {
            if (s0.get(f))
                continue;
            BitSet visited = new BitSet();
            boolean reachedS0 = false;
            stack.clear();
            stack.push(f);
            visited.set(f);
            reachabilityLoop:
            while (!stack.isEmpty()) {
                int pred = stack.pop();
                for (int op : p.addToTask[pred]) {
                    if (unsatPrecs[op] > 0)
                        continue;
                    if (goalZone.get(maxPrec[op]))
                        continue;
                    if ((p.numPrecs[op] == 0) || (s0.get(maxPrec[op]))) { // reached s0
                        reachedS0 = true;
                        break reachabilityLoop;
                    } else if (!visited.get(maxPrec[op])) {
                        visited.set(maxPrec[op], true);
                        stack.push(maxPrec[op]);
                    }
                }
            }
            if (!reachedS0)
                remove.set(f);
        }
        for (int op = cut.nextSetBit(0); op >= 0; op = cut.nextSetBit(op + 1)) {
            if (remove.get(maxPrec[op]))
                cut.set(op, false);
        }
    }

    /*
    private void forwardReachabilityBFS(BitSet s0, BitSet cut, BitSet goalZone, BitSet testReachability) {
        // put s0 facts into list of reachable facts
        BitSet reachableFacts = (BitSet) s0.clone();
        fringe.clear();
        for (int f = s0.nextSetBit(0); f >= 0; f = s0.nextSetBit(f + 1))
            fringe.push(f);
        for (int op : precLessOps) {
            for (int f : p.addLists[op])
                fringe.push(f);
        }

        // calculate reachability
        reachability:
        while (!fringe.isEmpty()) {
            UUIntStack addEffects = new UUIntStack();
            while (!fringe.isEmpty()) {
                int f = fringe.pop();
                testReachability.set(f, false);
                if (testReachability.isEmpty())
                    break reachability;

                assert getMaxPrecInv(f).equals(maxPrecInv[f]);
                for (int op = maxPrecInv[f].nextSetBit(0); op >= 0; op = maxPrecInv[f].nextSetBit(op + 1)) {
                    if (unsatPrecs[op] > 0)
                        continue;
                    for (int addEff : p.addLists[op]) {
                        if (goalZone.get(addEff))
                            continue;
                        if (!reachableFacts.get(addEff)) {
                            reachableFacts.set(addEff, true);
                            addEffects.push(addEff);
                        }
                    }
                }
            }
            fringe = addEffects;
        }

        // delete unreachable actions
        if (!testReachability.isEmpty()) { // some are not reachable
            for (int f = testReachability.nextSetBit(0); f >= 0; f = testReachability.nextSetBit(f + 1)) {
                for (int op = maxPrecInv[f].nextSetBit(0); op >= 0; op = maxPrecInv[f].nextSetBit(op + 1)) {
                    assert maxPrec[op] == f;
                    cut.set(op, false);
                }
            }
        }
    }*/

    private BitSet getMaxPrecInv(int f) {
        BitSet bs = new BitSet();
        for (int i = 0; i < p.numOfOperators; i++) {
            if (unsatPrecs[i] > 0)
                continue;
            if (maxPrec[i] == f)
                bs.set(i);
        }
        return bs;
    }

    public int hMax(BitSet s0, BitSet g) {
        //this.s0 = s0; // for debug
        if (g.cardinality() == 0)
            return 0;
        this.unsatPrecs = p.numPrecs.clone();
        this.hVal = hValInit.clone();
        this.maxPrec = maxPrecInit.clone();
        //this.numGoals = g.cardinality(); // only for hMax
        //for (int i = 0; i < p.numOfStateFeatures; i++)
        //    maxPrecInv[i].clear();

        UUIntPairPriorityQueue queue = new UUIntPairPriorityQueue();
        for (int f = s0.nextSetBit(0); f >= 0; f = s0.nextSetBit(f + 1)) {
            queue.add(0, f);
            hVal[f] = 0;
        }
        // actions without preconditions
        for (int a = 0; a < precLessOps.length; a++) {
            for (int f : p.addLists[a]) {
                if (hVal[f] > costs[a]) {
                    hVal[f] = costs[a];
                    queue.add(hVal[f], f);
                }
            }
        }

        while (!queue.isEmpty()) {
            int[] pair = queue.minPair();
            int pVal = pair[0];
            int prop = pair[1];
            if (hVal[prop] < pVal)
                continue;

            // only for hMax
            //if (g.get(prop) && (--numGoals == 0)) {
            //    return getMaxVal(g);
            //}
            for (int op : p.precToTask[prop]) {
                if ((maxPrec[op] == -1) || (hVal[maxPrec[op]] < hVal[prop])) {
                    maxPrec[op] = prop;
                }
                if (--unsatPrecs[op] == 0) {
                    //maxPrecInv[maxPrec[op]].set(op);
                    assert allPrecsTrue(op);
                    for (int f : p.addLists[op]) {
                        if ((hVal[maxPrec[op]] + costs[op]) < hVal[f]) {
                            hVal[f] = hVal[maxPrec[op]] + costs[op];
                            queue.add(hVal[f], f);
                        }
                    }
                }
            }
        }
        return getMaxValEnd(g);
    }

    public int costUpdate(BitSet operators, int decreaseBy, BitSet g) {
        UUIntPairPriorityQueue queue = new UUIntPairPriorityQueue();
        for (int op = operators.nextSetBit(0); op >= 0; op = operators.nextSetBit(op + 1)) {
            costs[op] -= decreaseBy;
            assert costs[op] >= 0;
            assert allPrecsTrue(op);
            for (int f : p.addLists[op]) {
                if ((hVal[maxPrec[op]] + costs[op]) < hVal[f]) { // that f might be cheaper now
                    hVal[f] = hVal[maxPrec[op]] + costs[op];
                    queue.add(hVal[f], f);
                }
            }
        }
        while (!queue.isEmpty()) {
            int[] pair = queue.minPair();
            int pVal = pair[0];
            int prop = pair[1];
            if (hVal[prop] < pVal) // we have prop's costs DECREASED -> this is fine
                continue;
            for (int op : p.precToTask[prop]) {
                if ((unsatPrecs[op] == 0) && (prop == maxPrec[op])) { // this may change the costs of the operator and all its successors
                    int opMaxPrec = -1;
                    int val = Integer.MIN_VALUE;
                    for (int f : p.precLists[op]) {
                        assert hVal[f] != cUnreachable;
                        if (hVal[f] > val) {
                            opMaxPrec = f;
                            val = hVal[f];
                        }
                    }
                    //maxPrecInv[prop].set(op, false);
                    maxPrec[op] = opMaxPrec;
                    //maxPrecInv[opMaxPrec].set(op, true);
                    for (int f : p.addLists[op]) {
                        if ((hVal[maxPrec[op]] + costs[op]) < hVal[f]) {
                            hVal[f] = hVal[maxPrec[op]] + costs[op];
                            assert hVal[f] >= 0;
                            queue.add(hVal[f], f);
                        }
                    }
                }
            }
        }
        return getMaxValEnd(g);
    }

    /*
     * When the graph is *not* build up until level-off-point -> use this on
     */
    private int getMaxVal(BitSet g) {
        int hVal = 0;
        for (int f = g.nextSetBit(0); f >= 0; f = g.nextSetBit(f + 1)) {
            assert this.hVal[f] != cUnreachable;
            if (hVal > this.hVal[f]) {
                hVal = this.hVal[f];
                maxPrecG = f;
            }
        }

        return hVal;
    }

    /*
     * When the graph is build up until level-off-point -> use this on
     */
    private int getMaxValEnd(BitSet g) {
        int hVal = 0;
        for (int f = g.nextSetBit(0); f >= 0; f = g.nextSetBit(f + 1)) {
            if (this.hVal[f] == cUnreachable) {
                maxPrecG = -1;
                return cUnreachable;
            }
            if (hVal < this.hVal[f]) {
                hVal = this.hVal[f];
                maxPrecG = f;
            }
        }

        return hVal;
    }

    /*
     * Tests used in assertions
     */
    private boolean allPrecsTrue(int op) {
        for (int prec : p.precLists[op]) {
            if (hVal[prec] == cUnreachable)
                return false;
        }
        return true;
    }

    private boolean implementationEquality(BitSet s0, BitSet g) {
        int[] temp = p.costs;
        p.costs = this.costs.clone();
        hMaxRtg otherImp = new hMaxRtg(p);
        otherImp.earlyAbord = false;
        p.costs = temp;
        otherImp.calcHeu(s0, g);

        int[] otherVals = otherImp.hVal;
        for (int i = 0; i < hVal.length; i++)
            if (hVal[i] != otherVals[i])
                return false;
        return true;
    }

    private boolean reachablilityOK(BitSet s0, int[] someHVals) {
        Set<Integer> reachableFacts = calcReach(s0);
        for (int i = 0; i < p.numOfStateFeatures; i++) {
            if (((someHVals[i] == cUnreachable) && (reachableFacts.contains(i)))
                    || ((someHVals[i] < cUnreachable) && (!reachableFacts.contains(i))))
                return false;
        }
        return true;
    }

    private Set<Integer> calcReach(BitSet s0) {
        Set<Integer> reachableFacts = new HashSet<>();
        for (int f = s0.nextSetBit(0); f >= 0; f = s0.nextSetBit(f + 1))
            reachableFacts.add(f);
        int oldSize = -1;
        while (oldSize < reachableFacts.size()) {
            oldSize = reachableFacts.size();
            oploop:
            for (int op = 0; op < p.numOfOperators; op++) {
                for (int prec : p.precLists[op]) {
                    if (!reachableFacts.contains(prec))
                        continue oploop;
                }
                for (int add : p.addLists[op])
                    reachableFacts.add(add);
            }
        }
        return reachableFacts;
    }
}
