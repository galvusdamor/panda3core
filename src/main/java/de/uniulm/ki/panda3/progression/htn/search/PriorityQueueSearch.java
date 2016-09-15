package de.uniulm.ki.panda3.progression.htn.search;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;

import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by dh on 15.09.16.
 */
public class PriorityQueueSearch extends ProgressionSearchRoutine {

    public List<Object> search(progressionNetwork firstSearchNode) {
        System.out.println("\nStarting priority queue search");
        int searchnodes = 1;
        int bestMetric = Integer.MAX_VALUE;
        List<Object> solution = null;
        long time = System.currentTimeMillis();
        PriorityQueue<progressionNetwork> fringe = new PriorityQueue<>();
        fringe.add(firstSearchNode);

        planningloop:
        while (!fringe.isEmpty()) {
            progressionNetwork n = fringe.poll();
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

                    // todo: add unit propagation here
                    node.heuristic = n.heuristic.update(node, ps);
                    node.metric = node.solution.size() + node.heuristic.getHeuristic();
                    node.goalRelaxedReachable = node.heuristic.goalRelaxedReachable();

                    if (node.goalRelaxedReachable) {
                        // early goal test - NON-OPTIMAL
                        if (node.isGoal()) {
                            solution = node.solution;
                            break planningloop;
                        }

                        fringe.add(node);
                        if (node.metric < bestMetric) {
                            bestMetric = node.metric;
                            System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, time));
                        }
                    }
                    searchnodes++;
                    if ((searchnodes % 100) == 0)
                        System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, time));
                } else { // is an abstract task
                    for (method m : ps.methods) {
                        progressionNetwork node = n.decompose(ps, m);

                        // todo: add unit propagation here
                        node.heuristic = n.heuristic.update(node, ps, m);
                        node.metric = node.solution.size() + node.heuristic.getHeuristic();
                        node.goalRelaxedReachable = node.heuristic.goalRelaxedReachable();

                        if (node.goalRelaxedReachable) {

                            if (node.isGoal()) {
                                solution = node.solution;
                                break planningloop;
                            }

                            fringe.add(node);
                            if (node.metric < bestMetric) {
                                bestMetric = node.metric;
                                System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, time));
                            }
                        }
                        searchnodes++;
                        if ((searchnodes % 100) == 0)
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
