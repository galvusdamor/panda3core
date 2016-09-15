package de.uniulm.ki.panda3.progression.htn.search;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dh on 15.09.16.
 */
public class EnforcedHillClimbing extends ProgressionSearchRoutine{

    public List<Object> search(progressionNetwork firstSearchNode) {
        System.out.println("\nStarting enforced hill climbing search");
        int searchnodes = 1;
        int bestMetric = firstSearchNode.metric;
        List<Object> solution = null;
        long time = System.currentTimeMillis();
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
                        // early goal test - NON-OPTIMAL
                        if (node.isGoal()) {
                            solution = node.solution;
                            break planningloop;
                        }


                        if (node.metric < bestMetric) {
                            bestMetric = node.metric;
                            fringe.clear();
                            fringe.add(node);
                            System.out.println("Found new best metric value: " + node.metric);
                            continue planningloop;
                        } else {
                            fringe.addLast(node);
                        }
                    }
                    searchnodes++;
                    if ((searchnodes % 10000) == 0)
                        System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, time));
                } else { // is an abstract task
                    for (method m : ps.methods) {
                        progressionNetwork node = n.decompose(ps, m);

                        if (node.heuristic.goalRelaxedReachable()) {

                            if (node.isGoal()) {
                                solution = node.solution;
                                break planningloop;
                            }

                            if (node.metric < bestMetric) {
                                bestMetric = node.metric;
                                fringe.clear();
                                fringe.add(node);
                                System.out.println("Found new best metric value: " + node.metric);
                                continue planningloop;
                            } else {
                                fringe.addLast(node);
                            }
                        }
                        searchnodes++;
                        if ((searchnodes % 10000) == 0)
                            System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, time));
                    }
                }
            }
        }

        System.out.println("Generated search nodes (total): " + searchnodes);
        System.out.println("Search time: " + (System.currentTimeMillis() - time) + " ms");
        return solution;
    }
}
