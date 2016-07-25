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
public class progressionNetwork implements Comparable<progressionNetwork>, Cloneable {
    /* todo
     * - does this work for empty task networks? I don't think so. Could one compile these methods in something other?
     */

    // this is state that has to be cloned
    public BitSet state;
    List<proPlanStep> unconstraintTasks;
    public LinkedList<Object> solution;

    public htnGroundedProgressionHeuristic heuristic;

    public int metric = 0;

    private progressionNetwork() {
    }

    public progressionNetwork(BitSet state, List<proPlanStep> ps) {
        this.unconstraintTasks = new LinkedList<>();
        this.unconstraintTasks.addAll(ps);
        this.solution = new LinkedList<>();
        this.state = state;
    }

    public boolean empty() {
        return unconstraintTasks.isEmpty();
    }

    public List<proPlanStep> getFirst() {
        return unconstraintTasks;
    }

    public progressionNetwork decompose(proPlanStep ps, method m) {
        progressionNetwork res = this.clone();
        res.state = this.state;
        res.solution.add(m.m);
        res.unconstraintTasks.remove(ps);

        // get copy of method subtask network
        subtaskNetwork tn = m.instantiate();

        // method's last tasks constrain all tasks that have been constrained by ps before
        List<proPlanStep> lastNodes = tn.getLastNodes();
        for (proPlanStep ps2 : lastNodes) {
            ps2.successorList.addAll(ps.successorList);
        }

        // the first nodes off the task network are new first nodes of this network
        res.unconstraintTasks.addAll(tn.getFirstNodes());

        // todo: change metric here
        res.heuristic = this.heuristic.update(res, ps, m);
        res.metric = res.solution.size() + res.heuristic.getHeuristic();
        return res;
    }

    public progressionNetwork apply(proPlanStep ps) {
        progressionNetwork res = this.clone();
        res.state = (BitSet) this.state.clone();
        res.solution.add(ps.action);

        res.unconstraintTasks.remove(ps);

        // transfer state
        res.state.andNot(operators.del[ps.action]);
        res.state.or(operators.add[ps.action]);

        // every successor of ps is a first task if and only if it is
        // not a successor of any task in the firstTasks list.
        HashSet<proPlanStep> potentialFirst = new HashSet<>();
        potentialFirst.addAll(ps.successorList);

        LinkedList<proPlanStep> potentialPredecessors = new LinkedList<>();
        for (proPlanStep f : res.unconstraintTasks) { // this must be res.unconstraintTasks. otherwise, the ps itself is in there!
            potentialPredecessors.addAll(f.successorList);
        }

        while (true) {
            if (potentialFirst.isEmpty()) {
                break; // no first left
            }
            if (potentialPredecessors.isEmpty()) {
                break; // the next that are left are valid firstTasks
            }
            proPlanStep ps2 = potentialPredecessors.removeFirst();
            potentialPredecessors.addAll(ps2.successorList);
            potentialFirst.remove(ps2);
        }
        res.unconstraintTasks.addAll(potentialFirst);

        // todo: change metric here
        res.heuristic = this.heuristic.update(res, ps);
        res.metric = res.solution.size() + res.heuristic.getHeuristic();
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
    protected progressionNetwork clone() {
        progressionNetwork res = new progressionNetwork();
        res.unconstraintTasks = new LinkedList<>(); // todo: array or linkedList?
        res.unconstraintTasks.addAll(this.unconstraintTasks);
        res.solution = new LinkedList<>();
        res.solution.addAll(this.solution);
        return res;
    }

    @Override
    public int compareTo(progressionNetwork other) {
        return (this.metric - other.metric);
    }
}
