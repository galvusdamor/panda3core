package de.uniulm.ki.panda3.progression.htn.search.searchRoutine;

import de.uniulm.ki.panda3.progression.htn.representation.SolutionStep;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;

/**
 * Created by dh on 15.09.16.
 */
public class CompleteEnforcedHillClimbing extends ProgressionSearchRoutine {


    int cehcSearchNodes = 1;

    public SolutionStep search(ProgressionNetwork firstSearchNode) {
        /*
        int bestMetric = firstSearchNode.metric;
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
                        ProgressionNetwork node = n.decompose(ps, m);

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
        */
        return null;
    }

    @Override
    public SolutionStep search(ProgressionNetwork firstSearchNode, InformationCapsule info, TimeCapsule timing) {
        return null;
    }
}
