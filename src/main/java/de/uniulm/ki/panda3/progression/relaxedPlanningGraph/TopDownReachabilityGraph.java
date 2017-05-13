package de.uniulm.ki.panda3.progression.relaxedPlanningGraph;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.*;

/**
 * Created by dh on 24.01.17.
 */
public class TopDownReachabilityGraph {

    private BitSet root = null;
    private BitSet reachableTasks[]; // maps a task to itself and all TASKS that are reachable via decomposition
    private BitSet reachableActions[]; // maps a task to (possibly) itself and all ACTIONS that are reachable via decomposition

    private int[][] scc; // the set of SCCs
    private int[] taskToScc; // maps a task to its SCC
    private BitSet[] scc2reachableTasks; // maps an SCC to all TASKS reachable from it

    static public int tToI(Task t) {
        return ProgressionNetwork.taskToIndex.get(t);
    }

    static public int[] maxDecompDepth; // this is the maximum decomposition depth that is left before tasks are primitive

    /*
    public TopDownReachabilityGraph(HashMap<Task, HashMap<GroundTask, List<method>>> methods, List<ProgressionPlanStep> initialTasks, int numTasks, int numActions, HashMap<GroundTask, Integer> mapping) {
        HashMap<GroundTask, List<method>> allmethods = new HashMap<>();
        for(HashMap<GroundTask, List<method>> val : methods.values())
            allmethods.putAll(val);

    }*/

    public TopDownReachabilityGraph(HashMap<Task, List<method>> methods, List<ProgressionPlanStep> initialTasks, int numTasks, int numActions) {
        System.out.println("Calculating top down reachability ...");
        long time = System.currentTimeMillis();

        // build a graph that, for each task, has edges to every subtask of every method decomposing it
        this.reachableTasks = new BitSet[numTasks];
        for (int i = 0; i < numTasks; i++) {
            this.reachableTasks[i] = new BitSet(numTasks);
            this.reachableTasks[i].set(0, numTasks - 1, false);
            this.reachableTasks[i].set(i, true); // the task itself is reachable
        }

        for (Task t : methods.keySet()) {
            BitSet parent = this.reachableTasks[tToI(t)];
            for (method m : methods.get(t))
                for (Task subTasks : m.subtasks)
                    parent.set(tToI(subTasks));
        }

        // set root to those tasks in the initial task network
        this.root = new BitSet(numTasks);
        this.root.set(0, numTasks - 1, false);
        for (ProgressionPlanStep ps : initialTasks) {
            root.set(tToI(ps.getTask()));
        }

        // calculate strongly connected components
        int maxdfs = 0;
        dfs = new int[numTasks];
        lowlink = new int[numTasks];

        U = new BitSet(numTasks);
        U.set(0, numTasks - 1, false);

        S = new int[numTasks];
        iS = -1;
        nodesInS = new BitSet(numTasks);
        nodesInS.set(0, numTasks - 1, false);

        scc = new int[numTasks][];
        iScc = -1;
        taskToScc = new int[numTasks];

        int v = root.nextSetBit(0);
        while (v > -1) {
            tarjan(v);
            v = root.nextSetBit(v + 1);
        }
        System.out.println(" - Found " + (iScc + 1) + " SCCs with up to " + biggestScc + " tasks.");

        // resize array with sccs
        int[][] old = scc;
        scc = new int[iScc + 1][];
        for (int i = 0; i < scc.length; i++) {
            scc[i] = old[i];
        }

        // set reachability for each scc to the union of the tasks reachable from its component-tasks
        scc2reachableTasks = new BitSet[scc.length];
        finished = new BitSet(scc.length);
        finished.set(0, scc.length - 1, false);

        for (int i = 0; i < scc.length; i++) {
            scc2reachableTasks[i] = new BitSet(numTasks);
            scc2reachableTasks[i].set(0, numTasks - 1, false);

            int[] current = scc[i];
            for (int j = 0; j < current.length; j++) {
                scc2reachableTasks[i].or(this.reachableTasks[scc[i][j]]);
            }
        }

        // calculate transitive reachability
        this.maxDecompDepth = new int[scc.length];
        v = root.nextSetBit(0);
        while (v > -1) {
            makeTransitive(taskToScc[v]);
            v = root.nextSetBit(v + 1);
        }


        // set the reachability of every task to the reachability of the SCC it belongs to and set also the reachable actions
        int[] temp = this.maxDecompDepth;
        this.maxDecompDepth = new int[numTasks];
        this.reachableActions = new BitSet[numTasks]; // these are the reachable actions
        for (int i = 0; i < reachableTasks.length; i++) {
            reachableTasks[i] = scc2reachableTasks[taskToScc[i]];
            maxDecompDepth[i] = temp[taskToScc[i]];
            this.reachableActions[i] = new BitSet(numActions);
            this.reachableActions[i].set(0, numActions - 1, false);
            v = reachableTasks[i].nextSetBit(0);
            while ((v > -1) && (v < numActions)) {
                this.reachableActions[i].set(v);
                v = reachableTasks[i].nextSetBit(v + 1);
            }
        }

        System.out.println(" - Reachability calculated in " + (System.currentTimeMillis() - time) + " ms.");
    }

    private BitSet finished;

    private void makeTransitive(int scc) {
        int v = scc2reachableTasks[scc].nextSetBit(0);
        BitSet accumulated = (BitSet) scc2reachableTasks[scc].clone();

        int curDepth = 0;
        while (v > -1) {
            int childScc = taskToScc[v];
            if (childScc != scc) {
                if (!finished.get(childScc)) {
                    makeTransitive(childScc);
                }
                accumulated.or(scc2reachableTasks[childScc]);
            }
            if (curDepth < maxDecompDepth[taskToScc[v]])
                curDepth = maxDecompDepth[taskToScc[v]];
            v = scc2reachableTasks[scc].nextSetBit(v + 1);
        }
        scc2reachableTasks[scc] = accumulated;
        maxDecompDepth[scc] = curDepth;
        finished.set(scc, true);
    }

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

    private void tarjan(int v) {
        dfs[v] = maxdfs;
        lowlink[v] = maxdfs;
        maxdfs++;

        S[++iS] = v;
        nodesInS.set(v, true);

        U.set(v, true);

        BitSet childOfV = this.reachableTasks[v];
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
                taskToScc[node] = iScc;
            }
        }
    }

    private int min(int i1, int i2) {
        if (i1 < i2)
            return i1;
        else
            return i2;
    }

    public BitSet getReachableActions(int task) {
        return this.reachableActions[task];
    }

    /*
    public void calcOrderingInvariants(int numTasks, HashMap<GroundTask, List<method>> methods, GroundTask[] indexToTask) {
        BitSet[] ordering = new BitSet[numTasks];
        for (int i = 0; i < numTasks; i++) {
            ordering[i] = new BitSet(numTasks);
            ordering[i].set(0, numTasks - 1, true);
            ordering[i].set(i, false);
        }
        for (Task t : methods.keySet()) {
            HashMap<GroundTask, List<method>> set = methods.get(t);
            for (GroundTask gt : set.keySet()) {
                for (method m : set.get(gt)) {
                    scala.collection.Iterator<PlanStep> iter = m.m.decompositionMethod().subPlan().planStepsWithoutInitGoal().iterator();
                    List<PlanStep> steps = new ArrayList<>();
                    while (iter.hasNext()) {
                        steps.add(iter.next());
                    }
                    for (int a = 0; a < steps.size(); a++) {
                        for (int b = 0; b < steps.size(); b++) {
                            GroundTask gtA = m.m.subPlanPlanStepsToGrounded().apply(steps.get(a));
                            GroundTask gtB = m.m.subPlanPlanStepsToGrounded().apply(steps.get(b));
                            int iA = tToI(gtA);
                            GroundTask testA = indexToTask[iA];
                            int iB = tToI(gtB);
                            GroundTask testB = indexToTask[iB];
                            if (!ordering[iA].get(iB))
                                continue;
                            boolean aBeforeB = m.m.decompositionMethod().subPlan().orderingConstraints().lt(steps.get(a), steps.get(b));
                            if (!aBeforeB) {
                                int childA = reachableTasks[iA].nextSetBit(0);
                                while (childA > -1) {
                                    int childB = reachableTasks[iB].nextSetBit(0);
                                    while (childB > -1) {
                                        if ((childA == 0) && (childB == 1))
                                            System.out.println();

                                        ordering[childA].set(childB, false);
                                        childB = reachableTasks[iB].nextSetBit(childB + 1);
                                    }
                                    childA = reachableTasks[iA].nextSetBit(childA + 1);
                                }
                            }
                        }
                    }
                }
            }
        }

        int someindex = 0;
        int otherTask = ordering[someindex].nextSetBit(0);
        while (otherTask > -1) {
            System.out.print(indexToTask[someindex].shortInfo());
            System.out.print(" < ");
            System.out.println(indexToTask[otherTask].shortInfo());
            otherTask = ordering[someindex].nextSetBit(otherTask + 1);
        }

        System.out.println("DONE!");
    }*/
}
