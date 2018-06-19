// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.progression.htn.search.searchRoutine;

import de.uniulm.ki.panda3.progression.htn.search.SolutionStep;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;

/**
 * Created by dh on 15.09.16.
 */
public class EnforcedHillClimbing extends ProgressionSearchRoutine{

    public SolutionStep search(ProgressionNetwork firstSearchNode) {
        /*
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
        return solution;*/
        return null;
    }

    @Override
    public SolutionStep search(ProgressionNetwork firstSearchNode, InformationCapsule info, TimeCapsule timing) {
        return null;
    }
}
