package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntPairPriorityQueue;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntStack;

import java.util.*;

/**
 * Created by dh on 22.06.17.
 */
public class hLmCutnative extends SasHeuristic {
    private final SasHeuristics heuristic;
    private final int[] maxPrecInit;
    private final int[] hValInit;
    private final int[] precLessOps;

    SasPlusProblem p;
    private int[] unsatPrecs;
    private int numGoals;

    private int[] hVal;
    private int[] maxPrec;
    private BitSet[] maxPrecInv;
    private int maxPrecG;
    private int[] costs;
    private BitSet s0;

    public hLmCutnative(SasPlusProblem p, SasHeuristics heuristic) {
        this.heuristic = heuristic;
        this.p = p;
        this.maxPrecInit = new int[p.numOfOperators];
        for (int i = 0; i < maxPrecInit.length; i++)
            maxPrecInit[i] = -1;
        this.hValInit = new int[p.numOfStateFeatures];
        for (int i = 0; i < hValInit.length; i++)
            hValInit[i] = cUnreachable;
        this.maxPrecInv = new BitSet[p.numOfStateFeatures];
        for (int i = 0; i < p.numOfStateFeatures; i++)
            maxPrecInv[i] = new BitSet();

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
        int hLmCut = 0;

        int hMax = hMax(s0, g);
        if (hMax == 0)
            return hLmCut;
        else if (hMax == cUnreachable)
            return hMax;

        int[] ct = corrh2Tab(g);
        for (int i = 0; i < hVal.length; i++)
            assert (hVal[i] == ct[i]);

        while (hMax > 0) {
            BitSet goalZone = new BitSet(p.numOfStateFeatures);
            BitSet cut = new BitSet(p.numOfOperators);
            BitSet precsOfCutNodes = new BitSet(p.numOfStateFeatures);
            goalZone(goalZone, cut, precsOfCutNodes);
            if(cut.cardinality() ==0){
                goalZone(goalZone, cut, precsOfCutNodes);
            }
            assert cut.cardinality() > 0;

            // check forward-reachability
            forwardReachability(s0, cut, goalZone, precsOfCutNodes);
            assert cut.cardinality() > 0;

            // calculate costs
            int minCosts = Integer.MAX_VALUE;
            for (int cutted = cut.nextSetBit(0); cutted >= 0; cutted = cut.nextSetBit(cutted + 1)) {
                minCosts = Integer.min(minCosts, costs[cutted]);
            }
            assert minCosts > 0;
            hLmCut += minCosts;

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
                    fringe.push(singlePrec);
                } else {
                    cut.set(producer);
                    precsOfCutNodes.set(singlePrec);
                }
            }
        }
    }


    private void forwardReachability(BitSet s0, BitSet cut, BitSet goalZone, BitSet testReachability) {
        // put s0 facts into list of reachable facts
        BitSet reachableFacts = (BitSet) s0.clone();
        fringe.clear();
        for (int f = s0.nextSetBit(0); f >= 0; f = s0.nextSetBit(f + 1))
            fringe.push(f);

        // calculate reachability
        reachability:
        while (!fringe.isEmpty()) {
            UUIntStack addEffects = new UUIntStack();
            while (!fringe.isEmpty()) {
                int f = fringe.pop();
                testReachability.set(f, false);
                if (testReachability.isEmpty())
                    break reachability;


                for (int op = maxPrecInv[f].nextSetBit(0); op >= 0; op = maxPrecInv[f].nextSetBit(op + 1)) {
                    for (int addEff : p.addLists[op]) {
                        if (goalZone.get(addEff))
                            continue;
                        if (!reachableFacts.get(addEff)) {
                            reachableFacts.set(f, true);
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
                for (int op = maxPrecInv[f].nextSetBit(0); op >= 0; op = maxPrecInv[f].nextSetBit(op + 1))
                    cut.set(op, false);
            }
        }
    }

    public int hMax(BitSet s0, BitSet g) {
        this.s0 = s0;
        this.costs = p.costs.clone();
        this.unsatPrecs = p.numPrecs.clone();
        this.hVal = hValInit.clone();
        this.maxPrec = maxPrecInit.clone();
        this.numGoals = g.cardinality();
        for (int i = 0; i < p.numOfStateFeatures; i++)
            maxPrecInv[i].clear();

        int[] ct = corrh2Tab(g);

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
            assert (hVal[prop] == ct[prop]);
            // only for hMax
            //if (g.get(prop) && (--numGoals == 0)) {
            //    return getMaxVal(g);
            //}
            for (int op : p.precToTask[prop]) {
                if ((maxPrec[op] == -1) || (hVal[maxPrec[op]] < hVal[prop])) {
                    maxPrec[op] = prop;
                    maxPrecInv[prop].set(op);
                }
                if (--unsatPrecs[op] == 0) {
                    assert allPrecsTrue(op);
                    for (int f : p.addLists[op]) {
                        if ((hVal[maxPrec[op]] + costs[op]) < hVal[f]) {
                            hVal[f] = hVal[maxPrec[op]] + costs[op];
                            assert (hVal[f] >= ct[f]);
                            queue.add(hVal[f], f);
                        }
                    }
                }
            }
        }
        return getMaxValEnd(g);
    }

    private boolean allPrecsTrue(int op) {
        for (int prec : p.precLists[op]) {
            if (hVal[prec] == cUnreachable)
                return false;
        }
        return true;
    }

    public int costUpdate(BitSet operators, int decreaseBy, BitSet g) {
        Set<Integer> reachable = calcReach();
        for (int op = operators.nextSetBit(0); op >= 0; op = operators.nextSetBit(op + 1)) {
            costs[op] -= decreaseBy;
        }
        int[] ct = corrh2Tab(g);
        UUIntPairPriorityQueue queue = new UUIntPairPriorityQueue();
        for (int op = operators.nextSetBit(0); op >= 0; op = operators.nextSetBit(op + 1)) {
            //costs[op] -= decreaseBy;
            assert costs[op] >= 0;
            assert allPrecsTrue(op);
            for (int f : p.addLists[op]) {
                if ((hVal[maxPrec[op]] + costs[op]) < hVal[f]) { // that f might be cheaper now
                    hVal[f] = hVal[maxPrec[op]] + costs[op];
                    assert (hVal[f] >= ct[f]);
                    queue.add(hVal[f], f);
                    //System.out.println("IniPro " + f);
                }
            }
        }
        while (!queue.isEmpty()) {
            int[] pair = queue.minPair();
            int pVal = pair[0];
            int prop = pair[1];
            if (hVal[prop] < pVal) // we have prop's costs DECREASED -> this is fine
                continue;
            //System.out.println(prop + " @ " + pVal);
            assert (hVal[prop] == ct[prop]);
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
                    maxPrecInv[prop].set(op, false);
                    maxPrec[op] = opMaxPrec;
                    maxPrecInv[opMaxPrec].set(op, true);
                    for (int f : p.addLists[op]) {
                        if ((hVal[maxPrec[op]] + costs[op]) < hVal[f]) {
                            hVal[f] = hVal[maxPrec[op]] + costs[op];
                            assert reachable.contains(f);
                            //System.out.println("HVAL " + f + "=" + hVal[f] + " from " + op);
                            //if(hVal[f] != ct[f])
                            //    System.out.print(0);
                            assert hVal[f] >= 0;
                            queue.add(hVal[f], f);
                        }
                    }
                }
            }
        }
        assert reachablilityOK(hVal);
        int res = getMaxValEnd(g);
        assert equalToReCalc(res, g);
        return res;
    }

    private boolean equalToReCalc(int res, BitSet g) {
        int[] temp = p.costs;
        p.costs = this.costs.clone();
        hMax h2 = new hMax(p);
        p.costs = temp;
        int other = h2.calcHeu(this.s0, g);
        //return true;
        return res == other;
    }

    private int[] corrh2Tab(BitSet g) {
        int[] temp = p.costs;
        p.costs = this.costs.clone();
        hMax h2 = new hMax(p);
        h2.earlyAbord = false;
        p.costs = temp;
        int other = h2.calcHeu(this.s0, g);
        reachablilityOK(h2.hVal);
        return h2.hVal;
        //return res == other;
    }

    private boolean reachablilityOK(int[] someHVals) {
        Set<Integer> reachableFacts = calcReach();
        for (int i = 0; i < p.numOfStateFeatures; i++) {
            if (((someHVals[i] == cUnreachable) && (reachableFacts.contains(i)))
                    || ((someHVals[i] < cUnreachable) && (!reachableFacts.contains(i))))
                return false;
        }
        return true;
    }

    private Set<Integer> calcReach() {
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
}
