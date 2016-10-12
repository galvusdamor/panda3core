package de.uniulm.ki.panda3.progression.htn.search.searchRoutine;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dh on 15.09.16.
 */
public class EnforcedHillClimbing extends ProgressionSearchRoutine{

    public List<Object> search(ProgressionNetwork firstSearchNode) {
        System.out.println("\nStarting enforced hill climbing search");
        int searchnodes = 1;
        int bestMetric = firstSearchNode.metric;
        List<Object> solution = null;
        long time = System.currentTimeMillis();
        LinkedList<ProgressionNetwork> fringe = new LinkedList<>();
        fringe.add(firstSearchNode);

        planningloop:
        while (true) {
            if (fringe.isEmpty()) // failure
                return null;

            ProgressionNetwork n = fringe.removeFirst();

            operatorloop:
            for (ProgressionPlanStep ps : n.getFirst()) {
                if (ps.isPrimitive) {
                    int pre = operators.prec[ps.action].nextSetBit(0);
                    while (pre > -1) {
                        if (!n.state.get(pre))
                            continue operatorloop;
                        pre = operators.prec[ps.action].nextSetBit(pre + 1);
                    }

                    ProgressionNetwork node = n.apply(ps, false);
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
                        ProgressionNetwork node = n.decompose(ps, m);

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

    @Override
    public List<Object> search(ProgressionNetwork firstSearchNode, InformationCapsule info, TimeCapsule timing) {
        return null;
    }
}
