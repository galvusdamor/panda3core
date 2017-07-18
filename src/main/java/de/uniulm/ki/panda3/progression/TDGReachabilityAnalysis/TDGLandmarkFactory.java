package de.uniulm.ki.panda3.progression.TDGReachabilityAnalysis;

import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.*;

/**
 * Created by dh on 24.01.17.
 */
public class TDGLandmarkFactory implements IActionReachability, IDisjunctiveLandmarks {

    private final int numActions;
    private final int numTasks;
    private int nodeCount;

    // and/or-graph representing the decomposition
    private BitSet root = null;
    private BitSet graph[];

    private int[][] scc; // the set of SCCs
    private int[] nodeToScc; // maps a task to its SCC
    BitSet sccTree[];

    // reachability and landmarks
    BitSet[] possible;  // node -> reachable nodes
    BitSet[] necessary; // node -> necessary nodes (aka landmarks)
    BitSet[] reachableActions; // maps a task to (possibly) itself and all ACTIONS that are reachable via decomposition
    BitSet[][] relaxedLandmarks;


    static public int tToI(Task t) {
        return ProgressionNetwork.taskToIndex.get(t);
    }

    static public Task iToT(int i) {
        return ProgressionNetwork.indexToTask[i];
    }

    public TDGLandmarkFactory(HashMap<Task, List<ProMethod>> methods, List<ProgressionPlanStep> initialTasks, int numTasks, int numActions) {
        System.out.println("Calculating HTN invariants ...");
        long time = System.currentTimeMillis();
        this.numActions = numActions;
        this.numTasks = numTasks;

        buildAndOrGraph(methods, numTasks);

        // set root to those tasks in the initial task network
        this.root = new BitSet(nodeCount);
        this.root.set(0, nodeCount - 1, false);
        for (ProgressionPlanStep ps : initialTasks)
            root.set(tToI(ps.getTask()));

        // calculate strongly connected components
        TarjanSCCs tarjan = new TarjanSCCs(this.graph);
        tarjan.calcSccs(root);
        this.scc = tarjan.getSCCs();
        this.nodeToScc = tarjan.getNodeToScc();

        System.out.println(" - Found " + tarjan.numOfSCCs() + " SCCs with up to " + tarjan.biggestScc() + " tasks.");

        // build tree of SCCs
        this.sccTree = buildTreeOfSCCs();

        necessary = new BitSet[nodeCount];
        possible = new BitSet[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            necessary[i] = new BitSet(nodeCount);
            possible[i] = new BitSet(nodeCount);
        }

        for (int v = root.nextSetBit(0); v > -1; v = root.nextSetBit(v + 1))
            calcPossAndNecSets(nodeToScc[v]);

        /*
        taskNames = new String[nodeCount];
        taskParams = new String[nodeCount][];
        relLMs = new HashMap[nodeCount];
        for (int i = 0; i < nodeCount; i++)
            relLMs[i] = new HashMap<>();

        for (int v = root.nextSetBit(0); v > -1; v = root.nextSetBit(v + 1))
            calcRelaxedLMs(nodeToScc[v]);
        */
        // set the reachability of every task to the reachability of the SCC it belongs to and set also the reachable actions
        this.reachableActions = new BitSet[numTasks];
        for (int i = 0; i < reachableActions.length; i++) {
            this.reachableActions[i] = new BitSet(numActions);
            this.reachableActions[i].set(0, numActions - 1, false);
            for (int v = possible[i].nextSetBit(0); (v > -1) && (v < numActions); v = possible[i].nextSetBit(v + 1))
                this.reachableActions[i].set(v);
        }
        //collectRelaxedLMs();
        //printDisLMTranslation();

        //printBS(necessary);
        //printDisLMs();
        System.out.println(" - Reachability calculated in " + (System.currentTimeMillis() - time) + " ms.");
        assert (implementationEquality(methods, initialTasks, numTasks, numActions));
    }

    private void collectRelaxedLMs() {
        relaxedLandmarks = new BitSet[this.numTasks][];
        for (int i = 0; i < this.numActions; i++) // the rest will be set below
            relaxedLandmarks[i] = new BitSet[0];
        HashMap<String, BitSet> lookupTable = new HashMap<>();
        for (int i = 0; i < taskNames.length; i++) {
            if (taskNames[i] == null)
                continue;
            BitSet tasks;
            if (lookupTable.containsKey(taskNames[i])) {
                tasks = lookupTable.get(taskNames[i]);
            } else {
                tasks = new BitSet();
                lookupTable.put(taskNames[i], tasks);
            }
            tasks.set(i);
        }

        for (int i = numActions; i < numTasks; i++) {
            List<BitSet> tLMs = new ArrayList<>();
            for (String type : relLMs[i].keySet()) {
                BitSet disj = new BitSet(numTasks);
                BitSet tasksOfType = lookupTable.get(type);
                for (String[] parameterSet : relLMs[i].get(type)) {
                    for (int t = tasksOfType.nextSetBit(0); t >= 0; t = tasksOfType.nextSetBit(t + 1)) {
                        boolean match = true;
                        assert taskParams[t].length == parameterSet.length;
                        for (int iP = 0; iP < taskParams[t].length; iP++) {
                            if ((parameterSet[iP].equals(varSymbol)) || (parameterSet[iP].equals(taskParams[t][iP])))
                                continue;
                            else {
                                match = false;
                                break;
                            }
                        }
                        if (match)
                            disj.set(t);
                    }
                }
                tLMs.add(disj);
            }
            relaxedLandmarks[i] = new BitSet[tLMs.size()];
            for (int j = 0; j < tLMs.size(); j++)
                relaxedLandmarks[i][j] = tLMs.get(j);
        }
    }

    private void buildAndOrGraph(HashMap<Task, List<ProMethod>> methods, int numTasks) {
        // build method index map
        int mI = numTasks;
        HashMap<ProMethod, Integer> mIndices = new HashMap<>();
        for (Task t : methods.keySet())
            for (ProMethod m : methods.get(t)) {
                mIndices.put(m, mI);
                mI++;
            }

        this.nodeCount = numTasks + mIndices.keySet().size();

        // build a graph that,
        // - for each task, has edges to every method decomposing it
        // - for each method, has edges to its subtasks
        graph = new BitSet[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            graph[i] = new BitSet(nodeCount);
            graph[i].set(0, nodeCount - 1, false);
        }

        for (Task t : methods.keySet()) {
            BitSet decompTaskNode = graph[tToI(t)];
            for (ProMethod m : methods.get(t)) {
                int iM = mIndices.get(m);
                decompTaskNode.set(iM); // edge from abstract task to the method that decomposes it
                BitSet methodNode = graph[iM];
                for (Task subTasks : m.subtasks) {
                    methodNode.set(tToI(subTasks));
                }
            }
        }
    }

    private BitSet[] buildTreeOfSCCs() {
        BitSet[] sccTree = new BitSet[scc.length];
        for (int i = 0; i < scc.length; i++) {
            sccTree[i] = new BitSet(scc.length);
            for (int node : scc[i])
                for (int succ = graph[node].nextSetBit(0);
                     succ >= 0;
                     succ = graph[node].nextSetBit(succ + 1)) {
                    sccTree[i].set(nodeToScc[succ]);
                }
            sccTree[i].set(i, false);
        }
        return sccTree;
    }


    /*
     * Calculate sets of possible and necessary nodes
     */

    private void calcPossAndNecSets(int iScc) {
        int v = sccTree[iScc].nextSetBit(0);
        while (v > -1) {
            calcPossAndNecSets(v);
            v = sccTree[iScc].nextSetBit(v + 1);
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int node : scc[iScc]) {
                BitSet oldN = (BitSet) necessary[node].clone();
                BitSet oldP = (BitSet) possible[node].clone();
                if (isPrimitive(node)) {
                    necessary[node].set(node);
                    possible[node].set(node);
                } else if (isAbstract(node)) {
                    processAbsTask(node);
                } else { // this is a method node
                    processMethod(node);
                }

                if (!oldN.equals(necessary[node]) || !oldP.equals(possible[node]))
                    changed = true;
            }
            if (scc[iScc].length == 1) // shortcut
                break;
        }
    }

    private void processAbsTask(int node) {
        boolean first = true;
        for (int mI = graph[node].nextSetBit(0);
             mI >= 0;
             mI = graph[node].nextSetBit(mI + 1)) {
            if (first) {
                first = false;
                necessary[node] = (BitSet) necessary[mI].clone();
            } else
                necessary[node].and(necessary[mI]);
            possible[node].or(possible[mI]);
        }
        necessary[node].set(node);
        possible[node].set(node);
    }

    private void processMethod(int node) {
        for (int tI = graph[node].nextSetBit(0);
             tI >= 0;
             tI = graph[node].nextSetBit(tI + 1)) {
            necessary[node].or(necessary[tI]);
            possible[node].or(possible[tI]);
        }
    }


    /*
     * Calculate sets of relaxed landmarks
     */

    String[] taskNames;
    String[][] taskParams;
    private final String varSymbol = "?";

    // NodeID -> [TaskName -> ParamSets]
    HashMap<String, List<String[]>>[] relLMs;

    private void calcRelaxedLMs(int iScc) {
        int v = sccTree[iScc].nextSetBit(0);
        while (v > -1) {
            calcRelaxedLMs(v);
            v = sccTree[iScc].nextSetBit(v + 1);
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int node : scc[iScc]) {
                boolean localChange = false;
                if (isPrimitive(node)) {
                    calcRLMsPrimTask(node);
                } else if (isAbstract(node)) {
                    localChange = calcRLMsAbsTask(node);
                } else { // this is a method node
                    localChange = calcRLMsMethod(node);
                }

                if (localChange)
                    changed = true;
            }
            if (scc[iScc].length == 1) // shortcut
                break;
        }
    }

    private void calcRLMsPrimTask(int node) {
        if (taskNames[node] == null)
            extractParams(node);
        List<String[]> paramSets = new ArrayList<>();
        paramSets.add(taskParams[node].clone());
        relLMs[node].put(taskNames[node], paramSets);
    }

    private boolean calcRLMsAbsTask(int node) {
        if (taskNames[node] == null)
            extractParams(node);
        HashMap<String, List<String[]>> lms = null;
        boolean first = true;
        for (int mI = graph[node].nextSetBit(0); mI >= 0; mI = graph[node].nextSetBit(mI + 1)) {
            if (first) { // copy first
                first = false;
                lms = copyLMs(this.relLMs[mI]);
            } else { // combine
                LinkedList<String> delete = new LinkedList();
                for (String taskName : lms.keySet()) {
                    if (!this.relLMs[mI].containsKey(taskName)) {
                        delete.add(taskName);
                        continue;
                    }
                    List<String[]> taskParamSets = lms.get(taskName);
                    List<String[]> methParamSets = this.relLMs[mI].get(taskName);
                    unify(taskParamSets, methParamSets);
                }
                for (String del : delete)
                    lms.remove(del);
            }
        }

        // add yourself
        List<String[]> paramSets = new ArrayList<>();
        paramSets.add(taskParams[node].clone());
        lms.put(taskNames[node], paramSets);

        HashMap<String, List<String[]>> old = this.relLMs[node];
        this.relLMs[node] = lms;
        return !equalLmSets(old, lms);
    }

    private HashMap<String, List<String[]>> copyLMs(HashMap<String, List<String[]>> disLM) {
        HashMap<String, List<String[]>> lms = new HashMap<>();
        for (String key : disLM.keySet()) {
            List val = new ArrayList();
            lms.put(key, val);
            for (String[] paramComb : disLM.get(key))
                val.add(paramComb);
        }
        return lms;
    }

    private void unify(List<String[]> taskParamSets, List<String[]> methParamSets) {
        //System.out.println("UNIFY " + taskParamSets.size()  );
        for (int iLM = 0; iLM < taskParamSets.size(); iLM++) {
            int currentCost = Integer.MAX_VALUE;
            String[] currentUnified = null;
            for (int j = 0; j < methParamSets.size(); j++) {
                String[] lm = taskParamSets.get(iLM);
                String[] newTask = methParamSets.get(j);
                String[] unified = new String[lm.length];
                int cost = unify(unified, lm, newTask);
                if (cost < currentCost) {
                    currentCost = cost;
                    currentUnified = unified;
                }
            }
            if (currentCost < Integer.MAX_VALUE) {
                taskParamSets.remove(iLM);
                taskParamSets.add(iLM, currentUnified);
            } else {
                taskParamSets.remove(iLM); // no match for landmark
                iLM--;
            }
        }
    }

    private int unify(String[] unified, String[] x, String[] y) {
        int cost = 0;
        for (int i = 0; i < x.length; i++) {
            if ((x[i].equals(varSymbol)) || (x[i].equals(y[i]))) {
                unified[i] = x[i];
            } else {
                unified[i] = varSymbol;
                cost++;
            }
        }
        for (String var : unified)
            if (!var.equals(varSymbol))
                return cost;
        return Integer.MAX_VALUE; // all are variables
    }

    private boolean calcRLMsMethod(int node) {
        HashMap<String, List<String[]>> old = relLMs[node];
        relLMs[node] = new HashMap<>();
        for (int tI = graph[node].nextSetBit(0); tI >= 0; tI = graph[node].nextSetBit(tI + 1)) {

            HashMap<String, List<String[]>> childLMs = relLMs[tI];
            for (String task : childLMs.keySet()) {
                List<String[]> params;
                if (!relLMs[node].containsKey(task)) {
                    params = new ArrayList<>();
                    relLMs[node].put(task, params);
                } else
                    params = relLMs[node].get(task);

                // todo: some kind of contains test?
                for (String[] params2 : childLMs.get(task))
                    params.add(params2.clone());
            }
        }
        return !equalLmSets(old, relLMs[node]);
    }

    private boolean equalLmSets(HashMap<String, List<String[]>> thisSet, HashMap<String, List<String[]>> thatSet) {
        if (thisSet.size() != thatSet.size())
            return false;
        for (String key : thisSet.keySet()) {
            if (!thatSet.containsKey(key))
                return false;
            List<String[]> thisParams = thisSet.get(key);
            List<String[]> thatParams = thatSet.get(key);
            if (thisParams.size() != thatParams.size())
                return false;
            for (int i = 0; i < thisParams.size(); i++) {
                if (thisParams.get(i).length != thisParams.get(i).length)
                    return false;
                for (int j = 0; j < thisParams.get(i).length; j++) {
                    if (!thisParams.get(i)[j].equals(thatParams.get(i)[j]))
                        return false;
                }
            }
        }
        return true;
    }

    private void extractParams(int node) {
        Task task = iToT(node);
        String n = task.name();
        if (!n.contains("[")) {
            taskNames[node] = n;
            taskParams[node] = new String[0];
        } else {
            String tName = n.substring(0, n.indexOf("["));
            taskNames[node] = tName;
            String paramStrs = n.substring(n.indexOf("["));
            paramStrs = paramStrs.substring(1, paramStrs.length() - 1);
            String[] params = paramStrs.split(",");
            taskParams[node] = params;
        }
    }

    private boolean isAbstract(int node) {
        return (node >= numActions) && (node < numTasks);
    }

    private boolean isPrimitive(int node) {
        return node < numActions;
    }


    /*
     * Implement interfaces
     */

    @Override
    public BitSet getReachableActions(int task) {
        return this.reachableActions[task];
    }

    @Override
    public BitSet[] getDisjLandmarks(int task) {
        return this.relaxedLandmarks[task];
    }


    /*
     * printer functions
     */

    private void printDisLMTranslation() {
        for (int i = numActions; i < numTasks; i++) {
            System.out.println(ProgressionNetwork.indexToTask[i].name() + " :");
            for (int j = 0; j < relaxedLandmarks[i].length; j++) {
                BitSet lms = relaxedLandmarks[i][j];
                boolean first = true;
                for (int lm = lms.nextSetBit(0); lm >= 0; lm = lms.nextSetBit(lm + 1)) {
                    if (first) {
                        System.out.print(" - ");
                        first = false;
                    } else
                        System.out.print("   ");
                    System.out.println(ProgressionNetwork.indexToTask[lm].name());
                }
            }
        }
    }

    private void printDisLMs() {
        for (int i = numActions; i < numTasks; i++) {
            if (relLMs[i].size() > 0) {
                System.out.println(ProgressionNetwork.indexToTask[i].name() + " :");
                for (String key : relLMs[i].keySet()) {
                    for (String[] params : relLMs[i].get(key)) {
                        System.out.print(" - " + key);
                        for (String param : params)
                            System.out.print(" " + param);
                        System.out.println();
                    }
                }
                System.out.println();
            }
        }
    }

    private void printBS(BitSet[] someBS) {
        for (int i = numActions; i < numTasks; i++) {
            if (someBS[i].length() > 0) {
                System.out.println(ProgressionNetwork.indexToTask[i].name() + " :");
                int j = someBS[i].nextSetBit(0);
                while (j >= 0) {
                    System.out.println(" - " + ProgressionNetwork.indexToTask[j].name());
                    j = someBS[i].nextSetBit(j + 1);
                }
                System.out.println();
            }
        }
    }


    /*
     * Functions checking assertions
     */

    private boolean implementationEquality(HashMap<Task, List<ProMethod>> methods, List<ProgressionPlanStep> initialTasks, int numTasks, int numActions) {
        TaskReachabilityGraph that = new TaskReachabilityGraph(methods, initialTasks, numTasks, numActions);
        for (int i = 0; i < numTasks; i++) {
            BitSet thisImplementation = this.getReachableActions(i);
            BitSet thatImplementation = that.getReachableActions(i);

            if (!thisImplementation.equals(thatImplementation))
                return false;
        }

        return true;
    }
}
