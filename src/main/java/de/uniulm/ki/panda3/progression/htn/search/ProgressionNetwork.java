package de.uniulm.ki.panda3.progression.htn.search;

import de.uniulm.ki.panda3.progression.htn.operators.*;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.htnGroundedProgressionHeuristic;

import java.util.*;

/**
 * Created by dhoeller on 21.07.16.
 * This is a state in the progression search, i.e. it contains the current network of undone tasks as well as
 * the current state of the environment.
 * <p/>
 * The implementation reuses unchanged parts of the task network and adds only those nodes that are new. This can
 * be done because it is changed only at its beginning, and the nodes only hold pointers to its successors.
 */
public class ProgressionNetwork implements Comparable<ProgressionNetwork>, Cloneable {
    /* todo
     * - does this work for empty task networks? I don't think so. Could one compile these methods in something other?
     */

    public BitSet state;
    List<ProgressionPlanStep> unconstraintPrimitiveTasks;
    List<ProgressionPlanStep> unconstraintAbstractTasks;
    public LinkedList<Object> solution;
    public int numProgressionSteps = 0;
    public int numSHOPProgressionSteps = 0;
    public int numDecompositionSteps = 0;


    private boolean printProgressionTrace = false;
    public String progressionTrace;

    public htnGroundedProgressionHeuristic heuristic;

    public int metric = 0;
    public boolean goalRelaxedReachable = true;
    public int id = 0;

    private ProgressionNetwork() {
    }

    public ProgressionNetwork(BitSet state, List<ProgressionPlanStep> ps) {
        this.unconstraintPrimitiveTasks = new LinkedList<>();
        this.unconstraintAbstractTasks = new LinkedList<>();
        for (ProgressionPlanStep p : ps) {
            if (p.isPrimitive) {
                unconstraintPrimitiveTasks.add(p);
            } else {
                unconstraintAbstractTasks.add(p);
            }
        }
        this.solution = new LinkedList<>();
        if (printProgressionTrace) {
            System.out.println("WARNING: The system is recording a full decomposition trace - this is VERY slow and only recommended for debugging.");
            this.progressionTrace = "\nPROGRESSION-TRACE:\n\n";
            this.progressionTrace += this.toString();
        }
        this.state = state;
    }

    public boolean empty() {
        return (this.unconstraintPrimitiveTasks.isEmpty() && this.unconstraintAbstractTasks.isEmpty());
    }

    public List<ProgressionPlanStep> getFirstPrimitiveTasks() {
        return this.unconstraintPrimitiveTasks;
    }

    public List<ProgressionPlanStep> getFirstAbstractTasks() {
        return this.unconstraintAbstractTasks;
    }

    @Deprecated
    public List<ProgressionPlanStep> getFirst() {
        List<ProgressionPlanStep> all = new LinkedList<>();
        all.addAll(this.unconstraintAbstractTasks);
        all.addAll(this.unconstraintPrimitiveTasks);
        return all;
    }

    @Override
    public String toString() {
        int taskid = 0;
        HashMap<ProgressionPlanStep, Integer> tasks = new HashMap<>();
        List<int[]> orderings = new LinkedList<>();
        LinkedList<ProgressionPlanStep> pss = new LinkedList<>();

        for (ProgressionPlanStep ps : unconstraintAbstractTasks) {
            pss.add(ps);
            tasks.put(ps, taskid++);
        }
        for (ProgressionPlanStep ps : unconstraintPrimitiveTasks) {
            pss.add(ps);
            tasks.put(ps, taskid++);
        }
        while (!pss.isEmpty()) {
            ProgressionPlanStep ps = pss.removeFirst();
            int iPrec = tasks.get(ps);
            for (ProgressionPlanStep succ : ps.successorList) {
                if (!tasks.containsKey(succ)) {
                    tasks.put(succ, taskid++);
                    pss.add(succ);
                }
                int iSucc = tasks.get(succ);
                int[] ord = new int[2];
                ord[0] = iPrec;
                ord[1] = iSucc;
                orderings.add(ord);
            }
        }
        HashMap<Integer, ProgressionPlanStep> inverse = new HashMap<>();
        for (ProgressionPlanStep key : tasks.keySet()) {
            inverse.put(tasks.get(key), key);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("tasks={");
        int i = 0;
        while (inverse.containsKey(i)) {
            ProgressionPlanStep ps = inverse.get(i);
            if (i > 0) {
                sb.append(", ");
                sb.append("\n       ");
            }
            sb.append(i);
            sb.append(":");
            sb.append(ps.getTask().longInfo());
            i++;
        }
        boolean first = true;
        sb.append("},\norderings={");
        for (int[] ord : orderings) {
            if (first)
                first = false;
            else
                sb.append(", ");
            sb.append("(");
            sb.append(ord[0]);
            sb.append("<");
            sb.append(ord[1]);
            sb.append(")");
        }
        first = true;
        sb.append("}\nnext={");
        for (ProgressionPlanStep ps : unconstraintAbstractTasks) {
            if (first)
                first = false;
            else
                sb.append(", ");
            sb.append(tasks.get(ps));
        }
        for (ProgressionPlanStep ps : unconstraintPrimitiveTasks) {
            if (first)
                first = false;
            else
                sb.append(", ");
            sb.append(tasks.get(ps));
        }
        sb.append("}\n");

        return sb.toString();
    }

    public ProgressionNetwork decompose(ProgressionPlanStep ps, method m) {
        ProgressionNetwork res = this.clone();
        res.state = this.state;
        res.solution.add(m.m);
        res.numDecompositionSteps++;
        res.unconstraintAbstractTasks.remove(ps);

        // get copy of method subtask network
        subtaskNetwork tn = m.instantiate();

        assert (tn.size() > 0);
        assert (tn.getLastNodes().size() > 0);
        assert (tn.getFirstNodes().size() > 0);

        // method's last tasks constrain all tasks that have been constrained by ps before
        List<ProgressionPlanStep> lastNodes = tn.getLastNodes();
        for (ProgressionPlanStep ps2 : lastNodes) {
            ps2.successorList.addAll(ps.successorList);
        }

        // the first nodes off the task network are new first nodes of this network
        for (ProgressionPlanStep p : tn.getFirstNodes()) {
            if (p.isPrimitive)
                res.unconstraintPrimitiveTasks.add(p);
            else
                res.unconstraintAbstractTasks.add(p);
        }
        if (printProgressionTrace) {
            res.progressionTrace += "\n";
            res.progressionTrace += res.toString();
        }
        return res;
    }

    public ProgressionNetwork apply(ProgressionPlanStep ps, boolean deleteRelaxed) {
        ProgressionNetwork res = this.clone();
        res.state = (BitSet) this.state.clone();
        res.solution.add(ps.action);
        if (operators.ShopPrecActions.contains(ps.action))
            res.numSHOPProgressionSteps++;
        else
            res.numProgressionSteps++;

        res.unconstraintPrimitiveTasks.remove(ps);

        assert(isApplicable(res.state, ps.action));

        // transfer state
        if (!deleteRelaxed) {
            res.state.andNot(operators.del[ps.action]);
        }
        res.state.or(operators.add[ps.action]);

        // every successor of ps is a first task if and only if it is
        // not a successor of any task in the firstTasks list.
        HashSet<ProgressionPlanStep> potentialFirst = new HashSet<>();
        potentialFirst.addAll(ps.successorList);

        // todo: shouldn't this be a set?
        LinkedList<ProgressionPlanStep> potentialPredecessors = new LinkedList<>();
        for (ProgressionPlanStep f : res.unconstraintAbstractTasks) { // this must be res.unconstraintTasks. otherwise, the ps itself is in there!
            potentialPredecessors.addAll(f.successorList);
        }
        for (ProgressionPlanStep f : res.unconstraintPrimitiveTasks) {
            potentialPredecessors.addAll(f.successorList);
        }

        // todo: this could be done in preprocessing
        for (ProgressionPlanStep f : ps.successorList) {
            potentialPredecessors.addAll(f.successorList);
        }

        while (true) {
            if (potentialFirst.isEmpty()) {
                break; // no first left
            }
            if (potentialPredecessors.isEmpty()) {
                break; // the next that are left are valid firstTasks
            }
            ProgressionPlanStep ps2 = potentialPredecessors.removeFirst();
            potentialPredecessors.addAll(ps2.successorList);
            potentialFirst.remove(ps2);
        }
        for (ProgressionPlanStep p : potentialFirst) {
            if (p.isPrimitive)
                res.unconstraintPrimitiveTasks.add(p);
            else
                res.unconstraintAbstractTasks.add(p);
        }
        if (printProgressionTrace) {
            res.progressionTrace += "\n";
            res.progressionTrace += res.toString();
        }
        return res;
    }

    private boolean isApplicable(BitSet state, int action) {
        int pre = operators.prec[action].nextSetBit(0);
        while (pre > -1) {
            if (!state.get(pre))
                return false;
            pre = operators.prec[action].nextSetBit(pre + 1);
        }
        return true;
    }

    public boolean isGoal() {
        if (!this.empty()) {
            return false;
        }
        BitSet temp = (BitSet) this.state.clone();
        temp.and(operators.goal);
        return temp.equals(operators.goal);
    }

    @Override
    protected ProgressionNetwork clone() {
        ProgressionNetwork res = new ProgressionNetwork();
        res.unconstraintPrimitiveTasks = new LinkedList<>(); // todo: array or linkedList?
        res.unconstraintAbstractTasks = new LinkedList<>(); // todo: array or linkedList?
        res.unconstraintPrimitiveTasks.addAll(this.unconstraintPrimitiveTasks);
        res.unconstraintAbstractTasks.addAll(this.unconstraintAbstractTasks);
        res.solution = new LinkedList<>();
        res.solution.addAll(this.solution);
        if (printProgressionTrace)
            res.progressionTrace = this.progressionTrace;
        res.numDecompositionSteps = this.numDecompositionSteps;
        res.numProgressionSteps = this.numProgressionSteps;
        res.numSHOPProgressionSteps = this.numSHOPProgressionSteps;
        return res;
    }

    @Override
    public int compareTo(ProgressionNetwork other) {
        int c = (this.metric - other.metric);
        //if (c == 0)
        //    c = (this.id - other.id);
        return c;
    }
}
