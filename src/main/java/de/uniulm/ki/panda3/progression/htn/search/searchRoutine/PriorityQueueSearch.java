package de.uniulm.ki.panda3.progression.htn.search.searchRoutine;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;

import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by dh on 15.09.16.
 */
public class PriorityQueueSearch extends ProgressionSearchRoutine {
    boolean aStar = true;

    public List<Object> search(ProgressionNetwork firstSearchNode) {
        System.out.println("\nStarting priority queue search");
        int searchnodes = 1;
        int bestMetric = Integer.MAX_VALUE;
        List<Object> solution = null;
        long totalSearchTime = System.currentTimeMillis();
        long lastInfo = System.currentTimeMillis();
        PriorityQueue<ProgressionNetwork> fringe = new PriorityQueue<>();
        fringe.add(firstSearchNode);

        planningloop:
        while (!fringe.isEmpty()) {
            ProgressionNetwork n = fringe.poll();
            operatorloop:
            for (ProgressionPlanStep ps : n.getFirst()) {
                if (ps.isPrimitive) {
                    int pre = operators.prec[ps.action].nextSetBit(0);
                    while (pre > -1) {
                        if (!n.state.get(pre))
                            continue operatorloop;
                        pre = operators.prec[ps.action].nextSetBit(pre + 1);
                    }

                    ProgressionNetwork node = n.apply(ps);

                    // todo: add unit propagation here
                    node.heuristic = n.heuristic.update(node, ps);
                    node.metric = node.heuristic.getHeuristic();
                    if (aStar) {
                        node.metric += node.solution.size();
                    }

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
                            System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, totalSearchTime));
                        }
                    }
                    searchnodes++;
                    if ((System.currentTimeMillis() - lastInfo) > 1000) {
                        lastInfo = System.currentTimeMillis();
                        System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, totalSearchTime));
                    }
                } else { // is an abstract task
                    for (method m : ps.methods) {
                        ProgressionNetwork node = n.decompose(ps, m);

                        // todo: add unit propagation here
                        node.heuristic = n.heuristic.update(node, ps, m);
                        node.metric = node.heuristic.getHeuristic();
                        if(aStar){
                            node.metric += node.solution.size();
                        }

                        node.goalRelaxedReachable = node.heuristic.goalRelaxedReachable();

                        if (node.goalRelaxedReachable) {

                            if (node.isGoal()) {
                                solution = node.solution;
                                break planningloop;
                            }

                            fringe.add(node);
                            if (node.metric < bestMetric) {
                                bestMetric = node.metric;
                                System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, totalSearchTime));
                            }
                        }
                        searchnodes++;
                        if ((System.currentTimeMillis() - lastInfo) > 1000) {
                            lastInfo = System.currentTimeMillis();
                            System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, totalSearchTime));
                        }
                    }
                }
            }
        }

        System.out.println("Generated search nodes (total): " + searchnodes);
        System.out.println("Search time: " + (System.currentTimeMillis() - totalSearchTime) + " ms");
        return solution;
    }
}
