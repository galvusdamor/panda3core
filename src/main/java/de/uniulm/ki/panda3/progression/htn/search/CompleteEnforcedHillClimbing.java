package de.uniulm.ki.panda3.progression.htn.search;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dh on 15.09.16.
 */
public class CompleteEnforcedHillClimbing extends ProgressionSearchRoutine {


    int cehcSearchNodes = 1;

    public List<Object> search(progressionNetwork firstSearchNode) {
        int bestMetric = firstSearchNode.metric;
        LinkedList<progressionNetwork> fringe = new LinkedList<>();
        fringe.add(firstSearchNode);

        planningloop:
        while (true) {
            if (fringe.isEmpty()) // failure
                return null;

            progressionNetwork n = fringe.removeFirst();

            operatorloop:
            for (proPlanStep ps : n.getFirst()) {
                if (ps.isPrimitive) {
                    int pre = operators.prec[ps.action].nextSetBit(0);
                    while (pre > -1) {
                        if (!n.state.get(pre))
                            continue operatorloop;
                        pre = operators.prec[ps.action].nextSetBit(pre + 1);
                    }

                    progressionNetwork node = n.apply(ps);
                    if (node.heuristic.goalRelaxedReachable()) {
                        if (node.isGoal())
                            return node.solution;

                        if (node.metric < bestMetric) {
                            bestMetric = node.metric;
                            System.out.println("-> " + node.metric);
                            List<Object> solution = (new CompleteEnforcedHillClimbing()).search(node);
                            if (solution != null)
                                return solution;

                            System.out.println("<- " + bestMetric);
                        }
                        fringe.addLast(node);
                    }
                    cehcSearchNodes++;
                    if ((cehcSearchNodes % 10000) == 0)
                        System.out.println("Searchnodes :" + cehcSearchNodes);
                } else { // is an abstract task
                    for (method m : ps.methods) {
                        progressionNetwork node = n.decompose(ps, m);

                        if (node.heuristic.goalRelaxedReachable()) {
                            if (node.isGoal())
                                return node.solution;

                            if (node.metric < bestMetric) {
                                bestMetric = node.metric;
                                System.out.println("-> " + node.metric);
                                List<Object> solution = (new CompleteEnforcedHillClimbing()).search(node);
                                if (solution != null)
                                    return solution;

                                System.out.println("<- " + bestMetric);
                            }
                            fringe.addLast(node);
                        }
                        cehcSearchNodes++;
                        if ((cehcSearchNodes % 10000) == 0)
                            System.out.println("Searchnodes :" + cehcSearchNodes);

                    }
                }
            }
        }
    }
}
