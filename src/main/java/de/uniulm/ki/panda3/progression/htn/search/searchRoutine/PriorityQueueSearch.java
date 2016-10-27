package de.uniulm.ki.panda3.progression.htn.search.searchRoutine;

import de.uniulm.ki.panda3.progression.htn.htnPlanningInstance;
import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.proUtil.proPrinter;
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Created by dh on 15.09.16.
 */
public class PriorityQueueSearch extends ProgressionSearchRoutine {
    public static final String SEARCH_TIME = "99 progression:01:searchTime";
    public static final String STATUS = "99 progression:01:status";
    public static final String A_STAR = "99 progression:02:aStar";
    public static final String HEURISTIC = "99 progression:03:heuristic";
    public static final String NUM_SEARCH_NODES = "99 progression:04:numSearchNodes";
    public static final String UNIT_PROPAGATION = "99 progression:05:unitPropagation";
    public static final String NUM_PRIM_TASKS = "99 progression:06:numPrimTasks";
    public static final String NUM_SHOP_TASKS = "99 progression:07:numShopTasks";
    public static final String NUM_DECOMPOSITIONS = "99 progression:08:numDecompositions";
    public static final String INFERRED_TLT = "99 progression:09:inferredTlt";
    public static final String ENFORCED_PREFIX_LENGTH = "99 progression:10:enforcedPrefixLength";
    public static final String SOLUTION = "99 progression:11:solution";

    private boolean findShortest = false;
    boolean aStar = true;
    boolean deleteRelaxed = false;
    boolean output = true;
    boolean exitDueToTimeLimit = false;

    public PriorityQueueSearch() {

    }

    public PriorityQueueSearch(boolean aStar, boolean deleteRelaxed, boolean output, boolean findShortest) {
        this.aStar = aStar;
        this.deleteRelaxed = deleteRelaxed;
        this.output = output;
        this.findShortest = findShortest;
    }

    public List<Object> search(ProgressionNetwork firstSearchNode) {
        InformationCapsule ic = new InformationCapsule();
        TimeCapsule tc = new TimeCapsule();
        return search(firstSearchNode, ic, tc);
    }

    public List<Object> search(ProgressionNetwork firstSearchNode, InformationCapsule info, TimeCapsule timing) {
        if (output)
            System.out.println("\nStarting priority queue search");
        int searchnodes = 1;
        int foundPlans = 0;
        int planLength = -1;
        long foundFirstPlanAfter = 0;
        long foundShortestPlan = 0;
        int unitPropagation = 0;
        int bestMetric = Integer.MAX_VALUE;
        List<Object> solution = null;
        long totalSearchTime = System.currentTimeMillis();
        long lastInfo = System.currentTimeMillis();
        PriorityQueue<ProgressionNetwork> fringe = new PriorityQueue<>();
        fringe.add(firstSearchNode);

        timing.start(SEARCH_TIME);
        long startedSearch = System.currentTimeMillis();

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
                /* // Precondition!
                while ((node.getFirstAbstractTasks().size() == 0)
                        && (node.getFirstPrimitiveTasks().size() == 1)) {
                    ps = node.getFirstPrimitiveTasks().get(0);
                    node = node.apply(ps, deleteRelaxed);
                    unitPropagation++;
                    searchnodes++;
                }*/

                node.id = searchnodes;
                node.heuristic = n.heuristic.update(node, ps);
                node.metric = node.heuristic.getHeuristic();
                if (aStar) {
                    node.metric += node.solution.size();
                }

                node.goalRelaxedReachable = node.heuristic.goalRelaxedReachable();

                if (node.goalRelaxedReachable) {
                    // early goal test - NON-OPTIMAL
                    if (node.isGoal()) {
                        int numSteps = getStepCount(node.solution);
                        if ((foundPlans == 0) || (numSteps < planLength)) {
                            if (foundPlans == 0) {
                                foundFirstPlanAfter = System.currentTimeMillis();
                            }
                            System.out.println("Found solution " + (foundPlans + 1) + " length " + numSteps);
                            solution = node.solution;
                            foundShortestPlan = System.currentTimeMillis();
                            planLength = numSteps;
                            if (node.progressionTrace != null)
                                System.out.println(node.progressionTrace);
                        }
                        foundPlans++;
                        if (!this.findShortest) {
                            break planningloop;
                        }
                    } else {
                        fringe.add(node);
                    }
                    if (node.metric < bestMetric) {
                        bestMetric = node.metric;
                        if (output)
                            System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, totalSearchTime));
                    }
                }
                searchnodes++;
                if ((System.currentTimeMillis() - lastInfo) > 1000) {
                    if ((wallTime > 0) && ((System.currentTimeMillis() - totalSearchTime) > wallTime)) {
                        System.out.println("Reached time limit, search will stop.");
                        exitDueToTimeLimit = true;
                        break planningloop;
                    }
                    lastInfo = System.currentTimeMillis();
                    if (output)
                        System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, totalSearchTime));
                }
            }


            if (n.getFirstAbstractTasks().size() == 0) continue planningloop;

            ProgressionPlanStep oneAbs = n.getFirstAbstractTasks().get(htnPlanningInstance.random.nextInt(n.getFirstAbstractTasks().size()));
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

                    /*
                    if (node.isGoal()) {
                        solution = node.solution;
                        if (node.progressionTrace != null)
                            System.out.println(node.progressionTrace);

                        break planningloop;
                    }*/

                    fringe.add(node);
                    if (node.metric < bestMetric) {
                        bestMetric = node.metric;
                        if (output)
                            System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, totalSearchTime));
                    }
                }
                searchnodes++;
                if ((System.currentTimeMillis() - lastInfo) > 1000) {
                    if ((wallTime > 0) && ((System.currentTimeMillis() - totalSearchTime) > wallTime)) {
                        System.out.println("Reached time limit, search will stop.");
                        exitDueToTimeLimit = true;
                        break planningloop;
                    }
                    lastInfo = System.currentTimeMillis();
                    if (output)
                        System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, totalSearchTime));
                }
            }


        }
        System.out.println("Number of nodes in final fringe: " + fringe.size());
        timing.stop(SEARCH_TIME);

        if (this.findShortest)
            info.add("findShortestPlan", 1);
        else
            info.add("findShortestPlan", 0);
        info.add("foundShortestPlanAfter", (int) (foundFirstPlanAfter - startedSearch));
        info.add("foundFirstPlanAfter", (int) (foundShortestPlan - startedSearch));
        info.add("foundPlans", foundPlans);
        info.add("randomSeed", htnPlanningInstance.randomSeed);

        // write statistics
        if (solution != null)
            info.add(STATUS, "solved");
        else if (exitDueToTimeLimit)
            info.add(STATUS, "timeout");
        else
            info.add(STATUS, "proven_unsolvable");

        if (this.aStar) {
            info.add(A_STAR, 1);
        } else {
            info.add(A_STAR, 0);
        }

        info.add(HEURISTIC, firstSearchNode.heuristic.getClass().toString());

        info.add(NUM_SEARCH_NODES, searchnodes);
        info.add(UNIT_PROPAGATION, unitPropagation);
        setSolInfo(solution, info);

        if (output)
            System.out.println("Generated search nodes (total): " + searchnodes);
        if (output)
            System.out.println("Search time: " + (System.currentTimeMillis() - totalSearchTime) + " ms");
        return solution;
    }

    private int getStepCount(LinkedList<Object> solution) {
        int count = 0;
        for (Object a : solution) {
            if ((a instanceof Integer) && (!operators.ShopPrecActions.contains(a))) {
                count++;
            }
        }

        return count;
    }

    @Override
    public String SearchName() {
        return "Priority Queue";
    }

    private void setSolInfo(List<Object> solution, InformationCapsule ic) {
        String PrimitivePlan = "";
        String FirstDecTask = "";
        int numPrim = 0;
        int enforcedPrefLength = 0;
        int numShop = 0;
        int numDec = 0;

        if (solution != null) {
            for (Object a : solution) {
                if (a instanceof Integer) {
                    if (operators.ShopPrecActions.contains(a))
                        numShop++;
                    else {
                        numPrim++;
                        if (PrimitivePlan.length() > 0) {
                            PrimitivePlan += "&";
                        }
                        String primName = operators.IndexToAction[(Integer) a].longInfo();
                        PrimitivePlan += primName;
                        if (primName.startsWith("p_") && (primName.charAt(2) == '0' ||
                                primName.charAt(2) == '1' ||
                                primName.charAt(2) == '2' ||
                                primName.charAt(2) == '3' ||
                                primName.charAt(2) == '4' ||
                                primName.charAt(2) == '5' ||
                                primName.charAt(2) == '6' ||
                                primName.charAt(2) == '7' ||
                                primName.charAt(2) == '8' ||
                                primName.charAt(2) == '9')) {
                            enforcedPrefLength++;
                        }
                    }
                } else {
                    GroundedDecompositionMethod dm = (GroundedDecompositionMethod) a;
                    if (dm.groundAbstractTask().task().name().startsWith("tlt") && (dm.subPlanGroundedTasksWithoutInitAndGoal().size() > 0)) {
                        for (int i = 0; i < dm.subPlanGroundedTasksWithoutInitAndGoal().size(); i++) {
                            GroundTask ps = dm.subPlanGroundedTasksWithoutInitAndGoal().apply(i);
                            if (FirstDecTask.length() > 0)
                                FirstDecTask += "&";
                            FirstDecTask += ps.longInfo();
                        }
                    }
                    numDec++;
                }
            }
        }
        ic.add(NUM_PRIM_TASKS, numPrim);
        ic.add(NUM_SHOP_TASKS, numShop);
        ic.add(NUM_DECOMPOSITIONS, numDec);
        if (FirstDecTask.length() > 0) {
            ic.add(INFERRED_TLT, FirstDecTask);
            ic.add(ENFORCED_PREFIX_LENGTH, enforcedPrefLength);
        }
        ic.add(SOLUTION, PrimitivePlan);
    }
}
