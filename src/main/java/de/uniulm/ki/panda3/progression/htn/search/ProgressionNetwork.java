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

    // this is state that has to be cloned
    public BitSet state;
    List<ProgressionPlanStep> unconstraintTasks;
    public LinkedList<Object> solution;

    public htnGroundedProgressionHeuristic heuristic;

    public int metric = 0;
    public boolean goalRelaxedReachable = true;

    private ProgressionNetwork() {
    }

    public ProgressionNetwork(BitSet state, List<ProgressionPlanStep> ps) {
        this.unconstraintTasks = new LinkedList<>();
        this.unconstraintTasks.addAll(ps);
        this.solution = new LinkedList<>();
        this.state = state;
    }

    public boolean empty() {
        return unconstraintTasks.isEmpty();
    }

    public List<ProgressionPlanStep> getFirst() {
        return unconstraintTasks;
    }

    public ProgressionNetwork decompose(ProgressionPlanStep ps, method m) {
        ProgressionNetwork res = this.clone();
        res.state = this.state;
        res.solution.add(m.m);
        res.unconstraintTasks.remove(ps);

        // get copy of method subtask network
        subtaskNetwork tn = m.instantiate();

        // method's last tasks constrain all tasks that have been constrained by ps before
        List<ProgressionPlanStep> lastNodes = tn.getLastNodes();
        for (ProgressionPlanStep ps2 : lastNodes) {
            ps2.successorList.addAll(ps.successorList);
        }

        // the first nodes off the task network are new first nodes of this network
        res.unconstraintTasks.addAll(tn.getFirstNodes());
        return res;
    }

    public ProgressionNetwork apply(ProgressionPlanStep ps) {
        ProgressionNetwork res = this.clone();
        res.state = (BitSet) this.state.clone();
        res.solution.add(ps.action);

        res.unconstraintTasks.remove(ps);

        // transfer state
        res.state.andNot(operators.del[ps.action]);
        res.state.or(operators.add[ps.action]);

        // every successor of ps is a first task if and only if it is
        // not a successor of any task in the firstTasks list.
        HashSet<ProgressionPlanStep> potentialFirst = new HashSet<>();
        potentialFirst.addAll(ps.successorList);

        LinkedList<ProgressionPlanStep> potentialPredecessors = new LinkedList<>();
        for (ProgressionPlanStep f : res.unconstraintTasks) { // this must be res.unconstraintTasks. otherwise, the ps itself is in there!
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
        res.unconstraintTasks.addAll(potentialFirst);
        return res;
    }

    public boolean isGoal() {
        if (!this.unconstraintTasks.isEmpty()) {
            return false;
        }
        BitSet temp = (BitSet) this.state.clone();
        temp.and(operators.goal);
        return temp.equals(operators.goal);
    }

    @Override
    protected ProgressionNetwork clone() {
        ProgressionNetwork res = new ProgressionNetwork();
        res.unconstraintTasks = new LinkedList<>(); // todo: array or linkedList?
        res.unconstraintTasks.addAll(this.unconstraintTasks);
        res.solution = new LinkedList<>();
        res.solution.addAll(this.solution);
        return res;
    }

    @Override
    public int compareTo(ProgressionNetwork other) {
        return (this.metric - other.metric);
    }
}
