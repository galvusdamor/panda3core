package de.uniulm.ki.panda3.progression.htn.search.searchRoutine;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Created by dh on 15.09.16.
 */
public class PriorityQueueSearch extends ProgressionSearchRoutine {
    boolean aStar = true;
    boolean deleteRelaxed = false;
    boolean output = true;

    public PriorityQueueSearch() {

    }

    public PriorityQueueSearch(boolean aStar, boolean deleteRelaxed, boolean output) {
        this.aStar = aStar;
        this.deleteRelaxed = deleteRelaxed;
        this.output = output;
    }

    public List<Object> search(ProgressionNetwork firstSearchNode) {
        InformationCapsule ic = new InformationCapsule();
        TimeCapsule tc = new TimeCapsule();
        if (output)
            System.out.println("\nStarting priority queue search");
        Random ran = new Random(42);
        int searchnodes = 1;
        int bestMetric = Integer.MAX_VALUE;
        List<Object> solution = null;
        long totalSearchTime = System.currentTimeMillis();
        long lastInfo = System.currentTimeMillis();
        PriorityQueue<ProgressionNetwork> fringe = new PriorityQueue<>();
        fringe.add(firstSearchNode);

        tc.start("search_time");

        planningloop:
        while (!fringe.isEmpty()) {
            ProgressionNetwork n = fringe.poll();
            actionloop:
            for (ProgressionPlanStep ps : n.getFirstPrimitiveTasks()) {
                int pre = operators.prec[ps.action].nextSetBit(0);
                while (pre > -1) {
                    if (!n.state.get(pre))
                        continue actionloop;
                    pre = operators.prec[ps.action].nextSetBit(pre + 1);
                }

                ProgressionNetwork node = n.apply(ps, deleteRelaxed);

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
                        System.out.println(node.progressionTrace);

                        break planningloop;
                    }

                    fringe.add(node);
                    if (node.metric < bestMetric) {
                        bestMetric = node.metric;
                        if (output)
                            System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, totalSearchTime));
                    }
                }
                searchnodes++;
                if ((System.currentTimeMillis() - lastInfo) > 1000) {
                    lastInfo = System.currentTimeMillis();
                    if (output)
                        System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, totalSearchTime));
                }
            }


            if (n.getFirstAbstractTasks().size() == 0) continue planningloop;

            ProgressionPlanStep oneAbs = n.getFirstAbstractTasks().get(ran.nextInt(n.getFirstAbstractTasks().size()));
            /*ProgressionPlanStep oneAbs = null;
            int minMethods = Integer.MAX_VALUE;

            for (ProgressionPlanStep ps : n.getFirstAbstractTasks()) {
                int sum = 0;
                for(method x : ps.methods){
                    sum += x.tasks.length;
                }
                if (sum < minMethods) {
                    minMethods = sum;
                    oneAbs = ps;
                }
            }*/

            for (method m : oneAbs.methods) {
                ProgressionNetwork node = n.decompose(oneAbs, m);

                // todo: add unit propagation here
                node.heuristic = n.heuristic.update(node, oneAbs, m);
                node.metric = node.heuristic.getHeuristic();
                if (aStar) {
                    node.metric += node.solution.size();
                }

                node.goalRelaxedReachable = node.heuristic.goalRelaxedReachable();

                if (node.goalRelaxedReachable) {

                    if (node.isGoal()) {
                        solution = node.solution;
                        System.out.println(node.progressionTrace);
                        break planningloop;
                    }

                    fringe.add(node);
                    if (node.metric < bestMetric) {
                        bestMetric = node.metric;
                        if (output)
                            System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, totalSearchTime));
                    }
                }
                searchnodes++;
                if ((System.currentTimeMillis() - lastInfo) > 1000) {
                    lastInfo = System.currentTimeMillis();
                    if (output)
                        System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, totalSearchTime));
                }
            }
        }
        ic.add("searchnodes", searchnodes);
        //ic.addToDistribution();
        tc.stop("search_time");

        if (output)
            System.out.println("Generated search nodes (total): " + searchnodes);
        if (output)
            System.out.println("Search time: " + (System.currentTimeMillis() - totalSearchTime) + " ms");
        return solution;
    }
}
