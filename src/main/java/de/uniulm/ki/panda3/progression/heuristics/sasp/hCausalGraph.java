package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.progression.TDGReachabilityAnalysis.TarjanSCCs;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntPairPriorityQueue;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by dh on 16.07.17.
 */
public class hCausalGraph extends SasHeuristic {

    private final SasPlusProblem p;
    // A graph where every nodes may be connected with multiple edges and
    // every edge is labeled with a list of ints
    // [fromNode][toNode] -> list of int[]
    int[][][][] dtgs;

    // Bitsets that indicate which bits belong to the variables directly
    // preceding each variable in the causal graph
    BitSet[] masks;

    public hCausalGraph(SasPlusProblem p) {
        this.p = p;
        long t = System.currentTimeMillis();
        System.out.println("Initializing Causal Graph Heuristic");
        // calculate domain transition graphs for each variable
        System.out.println("- Building Domain Transition Graphs...");
        List<BitSet>[][] tDtgs = createDTGs(p);

        // calculate cyclic causal graph
        System.out.println("- Building Causal Graph...");
        int[][] arcWeights = new int[p.numOfVars][];
        BitSet[] tCg = createCG(p, tDtgs);

        // delete cycles
        System.out.println("- Pruning graphs");
        tCg = pruneGraphs(p, tCg, tDtgs);

        System.out.println("- Creating final representation");
        this.dtgs = copyToArray(tDtgs);
        int[][] cg = copyToArray(tCg);
        int[][] preds = calcInverseMapping(cg);
        this.masks = createBitMasks(preds);

        System.out.println("- Prepared heuristic in " + t + "ms");
    }

    int[][][] costs;
    boolean[][] done;

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        costs = new int[p.numOfVars][][];
        done = new boolean[p.numOfVars][];
        for (int i = 0; i < done.length; i++) {
            done[i] = new boolean[p.ranges[i]];
        }

        int hVal = 0;
        for (int f = g.nextSetBit(0); f >= 0; f = g.nextSetBit(f + 1)) {
            int v = p.indexToMutexGroup[f];
            int vI = s0.nextSetBit(p.firstIndex[v]);
            int vG = f - p.firstIndex[v];
            computeCosts(s0, v, vG);
            if (costs[v][vI][vG] == cUnreachable)
                return cUnreachable;
            else
                hVal += costs[v][vI][vG];
        }
        return hVal;
    }

    private void computeCosts(BitSet s, int v, int d0) {
        if (done[v][d0])
            return;

        initCosts(v, d0);
        BitSet[] localStates = new BitSet[p.ranges[v]];
        localStates[d0 - p.firstIndex[v]] = (BitSet) s.clone();
        localStates[d0 - p.firstIndex[v]].and(masks[v]);

        UUIntPairPriorityQueue queue = new UUIntPairPriorityQueue();
        queue.add(0, d0);
        boolean[] reached = new boolean[p.ranges[v]];
        while (!queue.isEmpty()) {
            int d1 = queue.minPair()[1];
            if (reached[d1 - p.firstIndex[v]])
                continue;
            reached[d1 - p.firstIndex[v]] = true;

            for (int d2 = p.firstIndex[v]; d2 <= p.lastIndex[v]; d2++) {
                for (int[] transition : dtgs[d1][d2]) {
                    if (reached[d2 - p.firstIndex[v]])
                        continue;

                    int transitionCosts = 1;
                    for (int e1 : transition) { // these are the conditions
                        int v1 = p.indexToMutexGroup[e1];
                        int e0 = s.nextSetBit(p.firstIndex[v1]);
                        computeCosts(s, v1, e0);
                        if (costs[v][e0][e1] < cUnreachable)
                            transitionCosts += costs[v][e0][e1];
                        else {
                            transitionCosts = cUnreachable;
                            break;
                        }
                    }
                    if ((costs[v][d0][d1] + transitionCosts) < costs[v][d0][d2]) {
                        costs[v][d0][d2] = costs[v][d0][d1] + transitionCosts;
                        localStates[d2 - p.firstIndex[v]] = (BitSet) localStates[d1 - p.firstIndex[v]].clone();
                        for (int e1 : transition) {
                            int vE = p.indexToMutexGroup[e1];
                            int oldVal = localStates[d2 - p.firstIndex[v]].nextSetBit(p.firstIndex[vE]);
                            localStates[d2 - p.firstIndex[v]].set(oldVal, false);
                            localStates[d2 - p.firstIndex[v]].set(e1, true);
                        }
                    }
                }
            }


        }
        done[v][d0] = true;
    }

    private void initCosts(int v, int d) {
        costs[v][d] = new int[p.ranges[v]];
        for (int k = 0; k < costs[v][d].length; k++) {
            if (d == k)
                costs[v][d][k] = 0;
            else
                costs[v][d][k] = cUnreachable;
        }
    }


    private List<BitSet>[][] createDTGs(SasPlusProblem p) {
        // for each element in the matrix of nodes * nodes, a list of precondition labels
        List<BitSet>[][] dtgs = new List[p.numOfStateFeatures][];
        for (int i = 0; i < p.numOfStateFeatures; i++) {
            dtgs[i] = new List[p.numOfStateFeatures];
            for (int j = 0; j < p.numOfStateFeatures; j++) {
                dtgs[i][j] = new ArrayList<>();
            }
        }

        for (int op = 0; op < p.numOfOperators; op++) {
            for (int i = 0; i < p.addLists[op].length; i++) {
                int dn = p.addLists[op][i];
                int v = p.indexToMutexGroup[dn];

                // prepare edge(s) and label
                BitSet label = new BitSet();
                int d = -1; // is there a precondition on that specific variable?
                for (int f : p.precLists[op]) {
                    if ((f >= p.firstIndex[v]) && (f <= p.lastIndex[v])) {
                        d = f;
                        break;
                    } else {
                        label.set(f, true);
                    }
                }
                if (d > -1) {
                    dtgs[d][dn].add(label);
                } else {
                    for (int j = p.firstIndex[v]; j <= p.lastIndex[v]; j++) {
                        if (j != dn)
                            dtgs[j][dn].add(label);
                    }
                }
            }
        }
        return dtgs;
    }

    private BitSet[] createCG(SasPlusProblem p, List<BitSet>[][] dtgs) {
        BitSet[] cg = new BitSet[p.numOfVars];
        for (int i = 0; i < p.numOfVars; i++)
            cg[i] = new BitSet();

        // transition conditions
        for (int i = 0; i < p.numOfStateFeatures; i++) {
            for (int j = 0; j < p.numOfStateFeatures; j++) {
                for (BitSet label : dtgs[i][j]) {
                    for (int pre = label.nextSetBit(0); pre >= 0; pre = label.nextSetBit(pre + 1)) {
                        int vn = p.indexToMutexGroup[i];
                        assert vn == p.indexToMutexGroup[j]; // should be the same variable
                        int v = p.indexToMutexGroup[pre];
                        assert v != vn;
                        cg[v].set(vn, true);
                    }
                }
            }
        }

        // co-occuring effects
        for (int op = 0; op < p.numOfOperators; op++) {
            for (int e1 : p.addLists[op]) {
                for (int e2 : p.addLists[op]) {
                    int v = p.indexToMutexGroup[e1];
                    int vn = p.indexToMutexGroup[e2];
                    if (v != vn) {
                        cg[v].set(vn, true);
                    }
                }
            }
        }
        return cg;
    }


    private BitSet[] pruneGraphs(SasPlusProblem p, BitSet[] cg, List<BitSet>[][] dtgs) {
        cg = cg.clone(); // do not change input variable
        TarjanSCCs tarjan = new TarjanSCCs(cg);
        tarjan.calcSccs();
        int[][] sccs = tarjan.getSCCs();
        if (tarjan.biggestScc() > 1)
            System.out.println("- CG is cyclic, pruning edges to get acyclic");
        for (int[] scc : sccs) {
            if (scc.length == 1)
                continue;
            // create ordering
            int[] ordering = new int[scc.length];
            for (int i = 0; i < ordering.length; i++)
                ordering[i] = -1;
            for (int i = 0; i < scc.length - 1; i++) {
                int minArg = getMinArg(scc, dtgs, ordering);
                ordering[minArg] = i;
            }

            // make cg acyclic
            for (int iv1 = 0; iv1 < scc.length; iv1++) {
                for (int iv2 = 0; iv2 < scc.length; iv2++) {
                    int v1 = scc[iv1];
                    int v2 = scc[iv2];
                    if (!(ordering[iv1] < ordering[iv2])) {
                        cg[v1].set(v2, false);
                    } else if (v1 != v2) { // delete from v1 all conditions on vars of v2
                        for (int i = 0; i < dtgs[v1].length; i++) {
                            for (BitSet label : dtgs[v1][i]) {
                                label.set(p.firstIndex[v2], p.lastIndex[v2], false);
                            }
                        }
                    }
                }
            }

        }
        return cg;
    }

    private int getMinArg(int[] scc, List<BitSet>[][] dtgs, int[] ordering) {
        int[] weights = new int[scc.length];
        overallLoop:
        for (int i = 0; i < dtgs.length; i++) {
            for (int j = 0; j < scc.length; j++) {
                if ((scc[j] == i) && (ordering[j] >= 0))
                    continue overallLoop;
            }
            sccLoop:
            for (int j = 0; j < scc.length; j++) {
                if (ordering[j] >= 0)
                    continue sccLoop;
                weights[j] += dtgs[i][j].size();
            }
        }
        int arg = -1;
        int w = Integer.MAX_VALUE;
        for (int i = 0; i < weights.length; i++) {
            if (ordering[i] >= 0)
                continue;
            if (weights[i] < w) {
                w = weights[i];
                arg = i;
            }
        }
        return arg;
    }

    private int[][] calcInverseMapping(int[][] cg) {
        List<Integer>[] preds = new List[cg.length];
        for (int i = 0; i < preds.length; i++) {
            preds[i] = new ArrayList<>();
        }
        for (int i = 0; i < cg.length; i++) {
            for (int j = 0; j < cg[i].length; j++) {
                preds[j].add(i);
            }
        }
        int[][] tpreds = new int[preds.length][];
        for (int i = 0; i < preds.length; i++) {
            tpreds[i] = new int[preds[i].size()];
            for (int j = 0; j < tpreds[i].length; j++) {
                tpreds[i][j] = preds[i].get(j);
            }
        }
        return tpreds;
    }

    private BitSet[] createBitMasks(int[][] preds) {
        BitSet[] masks = new BitSet[preds.length];
        for (int i = 0; i < preds.length; i++) {
            masks[i] = new BitSet();
            for (int v : preds[i]) {
                masks[i].set(p.firstIndex[v], p.lastIndex[v], true);
            }
        }
        return masks;
    }

    private int[][] copyToArray(BitSet[] tCg) {
        int[][] res = new int[tCg.length][];
        for (int i = 0; i < res.length; i++) {
            res[i] = new int[tCg[i].cardinality()];
            int j = 0;
            for (int k = tCg[i].nextSetBit(0); k >= 0; k = tCg[i].nextSetBit(k + 1))
                res[i][j++] = k;
        }
        return res;
    }

    private int[][][][] copyToArray(List<BitSet>[][] dtgs) {
        int[][][][] res;
        res = new int[dtgs.length][][][];
        for (int i = 0; i < res.length; i++) {
            res[i] = new int[dtgs[i].length][][];
            for (int j = 0; j < res[i].length; j++) {
                res[i][j] = new int[dtgs[i][j].size()][];
                for (int k = 0; k < res[i][j].length; k++) {
                    BitSet label = dtgs[i][j].get(k);
                    res[i][j][k] = new int[label.cardinality()];
                    int l = 0;
                    for (int m = label.nextSetBit(0); m >= 0; m = label.nextSetBit(m + 1)) {
                        res[i][j][k][l++] = m;
                    }
                }
            }
        }
        return res;
    }
}
