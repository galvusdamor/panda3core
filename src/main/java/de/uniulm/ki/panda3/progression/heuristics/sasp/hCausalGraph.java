package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.progression.TDGReachabilityAnalysis.TarjanSCCs;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.util.fastIntegerDataStructures.UUIntPairPriorityQueue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
    int[][] rev;
    int[][] cg;

    public hCausalGraph(SasPlusProblem p) {
        /*for (int i = 0; i < p.numOfVars; i++) {
            System.out.println(p.varNames[i] + " " + p.firstIndex[i] + " - " + p.lastIndex[i]);
        }*/

        //System.out.println(p.toString());
        this.p = p;
        long t = System.currentTimeMillis();
        System.out.println("Initializing Causal Graph Heuristic");
        // calculate domain transition graphs for each variable
        System.out.println("- Building Domain Transition Graphs...");
        List<BitSet>[][] tDtgs = createDTGs(p);

        // calculate cyclic causal graph
        System.out.println("- Building Causal Graph...");
        BitSet[] tCg = createCG(p, tDtgs);

        // delete cycles
        System.out.println("- Pruning graphs");
        pruneGraphs(p, tCg, tDtgs);
        pruneDtgs(tDtgs);
        System.out.println("- Creating final representation");
        this.dtgs = copyToArray(tDtgs);
        this.cg = copyToArray(tCg);
        rev = calcInverseMapping(cg);
        this.masks = createBitMasks(rev);

        //dotIt(p);

        System.out.println("- Prepared heuristic in " + (System.currentTimeMillis() - t) + "ms");
    }


    private void pruneDtgs(List<BitSet>[][] tDtgs) {
        for (int i = 0; i < tDtgs.length; i++) {
            for (int j = 0; j < tDtgs[i].length; j++) {
                List<BitSet> labelList = tDtgs[i][j];
                if (labelList.size() < 2)
                    continue;
                // delete identical label sets
                HashSet<BitSet> labelSet = new HashSet<>();
                for (BitSet l : labelList)
                    labelSet.add(l);
                labelList.clear();
                labelList.addAll(labelSet);
                if (labelList.size() == 1)
                    continue;
                // delete dominating label sets
                HashSet<Integer> delSet = new HashSet<>();
                for (int k = 0; k < labelList.size(); k++) {
                    for (int l = 0; l < labelList.size(); l++) {
                        if (k == l)
                            continue;
                        BitSet set1 = labelList.get(k);
                        BitSet set2 = labelList.get(l);
                        // contains test
                        BitSet set3 = (BitSet) set1.clone();
                        set3.and(set2);
                        if (set3.equals(set1)) { // set2 contains set1
                            delSet.add(l);
                        }
                    }
                }
                int[] delAr = new int[delSet.size()];
                int m = 0;
                for (int n : delSet)
                    delAr[m++] = n;
                Arrays.sort(delAr);
                for (m = delAr.length - 1; m >= 0; m--) {
                    labelList.remove(delAr[m]);
                }
            }
        }
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
            System.out.println(p.factStrs[f]);
            int v = p.indexToMutexGroup[f];
            int vI = s0.nextSetBit(p.firstIndex[v]) - p.firstIndex[v];
            int vG = f - p.firstIndex[v];
            computeCosts(s0, v, vI);
            if ((undefined(v, vI)) || (costs[v][vI][vG] == cUnreachable))
                return cUnreachable;
            else
                hVal += costs[v][vI][vG];
        }
        return hVal;
    }

    private boolean undefined(int v, int vI) {
        return (costs[v] == null) || (costs[v][vI] == null);
    }

    private void computeCosts(BitSet s, int v, int d0) {
        if (done[v][d0])
            return;
        //System.out.println(p.varNames[v] + " val: " + p.factStrs[p.firstIndex[v] + d0]);

        initCosts(v, d0);
        BitSet[] localStates = new BitSet[p.ranges[v]];
        localStates[d0] = (BitSet) s.clone();
        localStates[d0].and(masks[v]);

        UUIntPairPriorityQueue queue = new UUIntPairPriorityQueue();
        queue.add(0, d0 + p.firstIndex[v]);
        boolean[] reached = new boolean[p.ranges[v]];
        while (!queue.isEmpty()) {
            int gd1 = queue.minPair()[1];
            int d1 = gd1 - p.firstIndex[v];
            if (reached[d1])
                continue;
            reached[d1] = true;

            for (int gd2 = p.firstIndex[v]; gd2 <= p.lastIndex[v]; gd2++) { // global index
                int d2 = gd2 - p.firstIndex[v]; // local index
                if (reached[d2])
                    continue;

                for (int[] transition : dtgs[gd1][gd2]) {
                    int transitionCosts = 1;
                    for (int ge1 : transition) { // these are the conditions
                        int v1 = p.indexToMutexGroup[ge1];
                        int e1 = ge1 - p.firstIndex[v1];
                        assert contains(rev[v], v1);
                        int e0 = localStates[d1].nextSetBit(p.firstIndex[v1]) - p.firstIndex[v1];
                        assert (e0 >= 0);
                        assert (e0 <= p.lastIndex[v1]);
                        computeCosts(s, v1, e0);
                        if (costs[v1][e0][e1] < cUnreachable)
                            transitionCosts += costs[v1][e0][e1];
                        else {
                            transitionCosts = cUnreachable;
                            break;
                        }
                    }

                    if ((costs[v][d0][d1] + transitionCosts) < costs[v][d0][d2]) {
                        costs[v][d0][d2] = costs[v][d0][d1] + transitionCosts;
                        localStates[d2] = (BitSet) localStates[d1].clone();
                        for (int ge1 : transition) {
                            int v1 = p.indexToMutexGroup[ge1];
                            int oldE = localStates[d2].nextSetBit(p.firstIndex[v1]);
                            assert oldE >= 0;
                            assert oldE <= p.lastIndex[v1];
                            localStates[d2].set(oldE, false);
                            localStates[d2].set(ge1, true);
                        }
                        queue.add(costs[v][d0][d2], d2 + p.firstIndex[v]);
                    }
                }
            }
        }
        done[v][d0] = true;
    }

    private boolean contains(int[] pred, int v1) {
        for (int i : pred)
            if (i == v1)
                return true;
        return false;
    }

    private void initCosts(int v, int d) {
        if (costs[v] == null) // the first one needs to initialize
            costs[v] = new int[p.ranges[v]][];
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
                        assert d == -1;
                        d = f;
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

    private int[][] createCG2(SasPlusProblem p, List<BitSet>[][] dtgs, Set<Integer> counted) {
        int[][] cg = new int[p.numOfVars][];
        for (int i = 0; i < p.numOfVars; i++)
            cg[i] = new int[p.numOfVars];

        // transition conditions
        for (int i = 0; i < p.numOfStateFeatures; i++) {
            for (int j = 0; j < p.numOfStateFeatures; j++) {
                for (BitSet label : dtgs[i][j]) {
                    for (int pre = label.nextSetBit(0); pre >= 0; pre = label.nextSetBit(pre + 1)) {
                        int vn = p.indexToMutexGroup[i];
                        assert vn == p.indexToMutexGroup[j]; // should be the same variable
                        int v = p.indexToMutexGroup[pre];
                        assert v != vn;
                        assert v < p.numOfVars;
                        assert vn < p.numOfVars;
                        if (counted.contains(v) && counted.contains(vn))
                            cg[v][vn]++;
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
                        assert v < p.numOfVars;
                        assert vn < p.numOfVars;
                        if (counted.contains(v) && counted.contains(vn))
                            cg[v][vn]++;
                    }
                }
            }
        }
        return cg;
    }

    private BitSet[] translate(int[][] in) {
        BitSet[] res = new BitSet[in.length];
        for (int i = 0; i < in.length; i++) {
            res[i] = new BitSet();
            for (int j = 0; j < in[i].length; j++)
                if (in[i][j] > 0)
                    res[i].set(j);
        }
        return res;
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
                        assert v < p.numOfVars;
                        assert vn < p.numOfVars;
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
                        assert v < p.numOfVars;
                        assert vn < p.numOfVars;
                        cg[v].set(vn, true);
                    }
                }
            }
        }
        return cg;
    }


    private void pruneGraphs(SasPlusProblem p, BitSet[] cg, List<BitSet>[][] dtgs) {
        TarjanSCCs tarjan = new TarjanSCCs(cg);
        tarjan.calcSccs();
        int[][] sccs = tarjan.getSCCs();
        if (tarjan.biggestScc() > 1)
            System.out.println("- CG is cyclic, pruning edges to get acyclic");
        for (int[] scc : sccs) {
            if (scc.length == 1)
                continue;
            // create ordering
            HashMap<Integer, Integer> ordering = new HashMap<>();
            for (int i = 0; i < scc.length; i++) {
                int minArg = getMinArg(scc, dtgs, ordering);
                ordering.put(minArg, i);
                //System.out.println(minArg);
            }

            // make cg acyclic
            for (int iv1 = 0; iv1 < scc.length; iv1++) {
                for (int iv2 = 0; iv2 < scc.length; iv2++) {
                    int v1 = scc[iv1];
                    int v2 = scc[iv2];
                    if (ordering.get(v1) > ordering.get(v2)) { // the equal case is irrelevant
                        cg[v1].set(v2, false);
                    } else if (ordering.get(v1) < ordering.get(v2)) { // delete from v1 all conditions on vars of v2
                        // loop through all arcs belonging to v1 and delete labels on v2
                        for (int bit1 = p.firstIndex[v1]; bit1 <= p.lastIndex[v1]; bit1++) {
                            for (int bit2 = p.firstIndex[v1]; bit2 <= p.lastIndex[v1]; bit2++) {
                                for (BitSet label : dtgs[bit1][bit2]) {
                                    label.set(p.firstIndex[v2], p.lastIndex[v2] + 1, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        assert acyclic(cg);
    }

    private boolean acyclic(BitSet[] cg) {
        TarjanSCCs t2 = new TarjanSCCs(cg);
        t2.calcSccs();
        return (t2.biggestScc() == 1);
    }

    private int getMinArg(int[] scc, List<BitSet>[][] dtgs, HashMap<Integer, Integer> ordering) {
        HashSet<Integer> countedNodes = new HashSet<>();
        for (int i = 0; i < scc.length; i++) {
            int n = scc[i];
            if (!ordering.containsKey(n))
                countedNodes.add(n);
        }
        if (countedNodes.size() == 1) // last node -> no edges possible
            return countedNodes.iterator().next();

        int[][] tempCg = createCG2(p, dtgs, countedNodes);

        int[] weights = new int[scc.length];
        for (int j = 0; j < scc.length; j++) {
            for (int i = 0; i < scc.length; i++) {
                int nodeJ = scc[j];
                int nodeI = scc[i];
                weights[i] += tempCg[nodeJ][nodeI];
            }
        }
        int currentI = -1;
        int currentVal = Integer.MAX_VALUE;
        for (int i = 0; i < weights.length; i++) {
            if (!(ordering.containsKey(scc[i])) && (weights[i] < currentVal)) {
                currentI = i;
                currentVal = weights[i];
            }
        }
        return scc[currentI];
    }

    private int[][] calcInverseMapping(int[][] cg) {
        List<Integer>[] tRev = new List[cg.length];
        for (int i = 0; i < tRev.length; i++) {
            tRev[i] = new ArrayList<>();
        }
        for (int i = 0; i < cg.length; i++) {
            for (int j = 0; j < cg[i].length; j++) {
                tRev[cg[i][j]].add(i);
            }
        }
        int[][] rev = new int[tRev.length][];
        for (int i = 0; i < tRev.length; i++) {
            rev[i] = new int[tRev[i].size()];
            for (int j = 0; j < rev[i].length; j++) {
                rev[i][j] = tRev[i].get(j);
            }
        }
        return rev;
    }

    private BitSet[] createBitMasks(int[][] rev) {
        BitSet[] masks = new BitSet[rev.length];
        for (int i = 0; i < rev.length; i++) {
            masks[i] = new BitSet();
            for (int v : rev[i]) {
                masks[i].set(p.firstIndex[v], p.lastIndex[v] + 1, true);
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

    String dotCg(int[][] cg) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph someGraph {\n");

        List<String> nodes = new ArrayList<>();
        List<String> edges = new ArrayList<>();
        for (int i = 0; i < cg.length; i++) {
            nodes.add("\tnode [label=\"" + p.varNames[i] + "\"] node" + i + ";\n");
            for (int j = 0; j < cg[i].length; j++) {
                edges.add("\tnode" + i + " -> node" + cg[i][j] + "\n");
            }
        }
        for (String s : nodes)
            sb.append(s);
        for (String s : edges)
            sb.append(s);
        sb.append("}\n");
        return sb.toString();
    }

    String dotDtg(int[][][][] dtgs, int var) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph someGraph {\n");

        HashSet<String> nodes = new HashSet<>();
        HashSet<String> edges = new HashSet<>();
        for (int i = p.firstIndex[var]; i <= p.lastIndex[var]; i++) {
            int ival = i - p.firstIndex[var];
            String ivalName = p.factStrs[i];
            nodes.add("\tnode [label=\"" + ivalName + "\"] node" + ival + ";\n");
            for (int j = p.firstIndex[var]; j <= p.lastIndex[var]; j++) {
                for (int[] transition : dtgs[i][j]) {
                    int jval = j - p.firstIndex[var];
                    String label = "";
                    for (int l = 0; l < transition.length; l++) {
                        if (l > 0)
                            label += ", ";
                        label += p.factStrs[transition[l]];
                    }
                    String edge = "\tnode" + ival + " -> node" + jval;
                    if (label.length() > 0)
                        edge += " [label=\"" + label + "\"]";
                    edge += "\n";
                    edges.add(edge);
                }
            }
        }
        for (String s : nodes)
            sb.append(s);
        for (String s : edges)
            sb.append(s);
        sb.append("}\n");
        return sb.toString();
    }

    private void dotIt(SasPlusProblem p) {
        String dir = "/home/dh/Schreibtisch/dot/";
        try {
            FileWriter fw = new FileWriter(dir + "cg.dot");
            fw.write(dotCg(cg));
            fw.close();

            fw = new FileWriter(dir + "rev.dot");
            fw.write(dotCg(rev));
            fw.close();

            for (int var = 0; var < p.numOfVars; var++) {
                fw = new FileWriter(dir + "var" + var + ".dot");
                fw.write(dotDtg(dtgs, var));
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
