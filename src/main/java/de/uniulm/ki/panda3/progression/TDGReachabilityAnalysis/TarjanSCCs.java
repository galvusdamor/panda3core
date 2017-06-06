package de.uniulm.ki.panda3.progression.TDGReachabilityAnalysis;

import java.util.BitSet;
import java.util.LinkedList;

/**
 * Created by dh on 06.06.17.
 */
public class TarjanSCCs {

    private BitSet graph[];

    // stack variables
    private int[] S;
    private int iS;
    private BitSet nodesInS;

    private int[] dfs;
    private int[] lowlink;
    private BitSet U;

    private int iScc;
    private int maxdfs;
    private int biggestScc = 0;

    private int[][] scc; // the set of SCCs
    private int[] nodeToScc; // maps a task to its SCC

    public TarjanSCCs(BitSet[] graph) {
        this.graph = graph;
        int nodeCount = graph.length;
        dfs = new int[nodeCount];
        lowlink = new int[nodeCount];

        U = new BitSet(nodeCount);
        U.set(0, nodeCount - 1, false);

        S = new int[nodeCount];
        iS = -1;
        nodesInS = new BitSet(nodeCount);
        nodesInS.set(0, nodeCount - 1, false);

        scc = new int[nodeCount][];
        iScc = -1;
        nodeToScc = new int[nodeCount];
    }

    public void tarjan(int v) {
        dfs[v] = maxdfs;
        lowlink[v] = maxdfs;
        maxdfs++;

        S[++iS] = v;
        nodesInS.set(v, true);

        U.set(v, true);

        BitSet childOfV = this.graph[v];
        int child = childOfV.nextSetBit(0);
        while (child > -1) { // for-loop over children
            if (!U.get(child)) { // this is unvisited
                tarjan(child);
                lowlink[v] = min(lowlink[v], lowlink[child]);
            } else if (nodesInS.get(child)) {
                lowlink[v] = min(lowlink[v], dfs[child]);
            }
            child = childOfV.nextSetBit(child + 1);
        }

        if (lowlink[v] == dfs[v]) {
            LinkedList<Integer> current = new LinkedList<>();
            do {
                child = S[iS--];
                nodesInS.set(child, false);
                current.add(child);
            } while (!(child == v));

            if (biggestScc < current.size())
                biggestScc = current.size();

            scc[++iScc] = new int[current.size()];
            int curSize = current.size();
            for (int i = 0; i < curSize; i++) {
                int node = current.removeFirst();
                scc[iScc][i] = node;
                nodeToScc[node] = iScc;
            }
        }
    }

    private int min(int i1, int i2) {
        if (i1 < i2)
            return i1;
        else
            return i2;
    }

    public void calcSccs(BitSet root) {
        for (int v = root.nextSetBit(0); (v > -1); v = root.nextSetBit(v + 1))
            tarjan(v);
        int[][] old = scc;
        scc = new int[iScc + 1][];
        for (int i = 0; i < scc.length; i++)
            scc[i] = old[i];
        assert sccsOK();
    }

    public int[][] getSCCs() {
        return this.scc;
    }

    public int[] getNodeToScc() {
        return this.nodeToScc;
    }

    public int numOfSCCs() {
        return iScc + 1;
    }

    public int biggestScc() {
        return biggestScc;
    }

    private boolean sccsOK() {
        for (int task = 0; task < nodeToScc.length; task++) {
            boolean ok = false;
            for (int node : scc[nodeToScc[task]]) {
                if (node == task) {
                    ok = true;
                    break;
                }
            }
            if (!ok)
                return false;
        }

        for (int i = 0; i < scc.length; i++) {
            for (int node : scc[i]) {
                if (!(nodeToScc[node] == i))
                    return false;
            }
        }
        return true;
    }
}
