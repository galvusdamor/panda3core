package de.uniulm.ki.panda3.progression.htn.search.searchRoutine;

import de.uniulm.ki.panda3.progression.htn.htnPlanningInstance;
import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.operators.operators;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.htn.search.loopDetection.VisitedList;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.TopDownReachabilityGraph;
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;

import java.util.*;

/**
 * Created by dh on 15.09.16.
 */
public class PriorityQueueSearch extends ProgressionSearchRoutine {
    static public enum abstractTaskSelection {branchOverAll, random, methodCount, decompDepth}

    public static final String SEARCH_TIME = "30 progression:01:searchTime";
    public static final String STATUS = "30 progression:01:status";
    public static final String A_STAR = "30 progression:02:aStar";
    public static final String HEURISTIC = "30 progression:03:heuristic";
    public static final String NUM_SEARCH_NODES = "30 progression:04:numSearchNodes";
    public static final String UNIT_PROPAGATION = "30 progression:05:unitPropagation";
    public static final String NUM_PRIM_TASKS = "30 progression:06:numPrimTasks";
    public static final String NUM_SHOP_TASKS = "30 progression:07:numShopTasks";
    public static final String NUM_DECOMPOSITIONS = "30 progression:08:numDecompositions";
    public static final String INFERRED_TLT = "30 progression:09:inferredTlt";
    public static final String ENFORCED_PREFIX_LENGTH = "30 progression:10:enforcedPrefixLength";
    public static final String SOLUTION = "30 progression:11:solution";

    private boolean findShortest = false;
    boolean aStar = true;
    boolean deleteRelaxed = false;
    boolean output = true;
    boolean exitDueToTimeLimit = false;

    public PriorityQueueSearch() {

    }

    public PriorityQueueSearch(boolean aStar, boolean deleteRelaxed, boolean output, boolean findShortest,
                               abstractTaskSelection taskSelectionStrategy) {
        this.aStar = aStar;
        this.deleteRelaxed = deleteRelaxed;
        this.output = output;
        this.findShortest = findShortest;
        this.taskSelection = taskSelectionStrategy;
    }

    public List<Object> search(ProgressionNetwork firstSearchNode) {
        InformationCapsule ic = new InformationCapsule();
        TimeCapsule tc = new TimeCapsule();
        return search(firstSearchNode, ic, tc);
    }

    abstractTaskSelection taskSelection = abstractTaskSelection.random;

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

        boolean collectTlts = false;
        LinkedList<Long> timeStamps = new LinkedList<>();
        LinkedList<String> tlts = new LinkedList<>();

        int restartCount = -1;
        long restart = -1;// (2 * 60 * 1000);
        long nextRestart = -1;//restartCount * restart;

        //VisitedList visited = new VisitedList();
        //visited.addIfNotIn(firstSearchNode);

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
                //if (visited.addIfNotIn(node))
                //    continue actionloop;

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
                        if (collectTlts) {
                            collectPlanRecData(timeStamps, tlts, node);
                        }
                        foundPlans++;
                        if (!this.findShortest && !collectTlts) {
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
                    if ((restart > 0) && ((System.currentTimeMillis() - totalSearchTime) > nextRestart)) {
                        restartCount++;
                        nextRestart = restartCount * restart;
                        System.out.println("Seems to be a bad run, let's try again! -> restart");
                        fringe.clear();
                        fringe.add(firstSearchNode);
                        htnPlanningInstance.randomSeed += 100;
                        htnPlanningInstance.random = new Random(htnPlanningInstance.randomSeed);
                        continue planningloop;
                    }
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

            // which task shall be decomposed?
            ProgressionPlanStep oneAbs = null;
            if (taskSelection == abstractTaskSelection.random)
                oneAbs = n.getFirstAbstractTasks().get(htnPlanningInstance.random.nextInt(n.getFirstAbstractTasks().size()));
            else if (taskSelection == abstractTaskSelection.methodCount) { // minimize branching
                int minMethods = Integer.MAX_VALUE;
                for (ProgressionPlanStep ps : n.getFirstAbstractTasks()) {
                    if (ps.methods.size() < minMethods) {
                        minMethods = ps.methods.size();
                        oneAbs = ps;
                    } else if ((ps.methods.size() == minMethods) && (htnPlanningInstance.random.nextBoolean())) {
                        minMethods = ps.methods.size();
                        oneAbs = ps;
                    }
                }
            } else {
                int minDepth = Integer.MAX_VALUE;
                for (ProgressionPlanStep ps : n.getFirstAbstractTasks()) {
                    int depth = TopDownReachabilityGraph.maxDecompDepth[TopDownReachabilityGraph.mappingget(ps.getTask())];
                    if (depth < minDepth) {
                        minDepth = depth;
                        oneAbs = ps;
                    } else if ((ps.methods.size() == minDepth) && (htnPlanningInstance.random.nextBoolean())) {
                        minDepth = ps.methods.size();
                        oneAbs = ps;
                    }
                }
            }

            methodloop:
            for (method m : oneAbs.methods) {
                ProgressionNetwork node = n.decompose(oneAbs, m);
                //if (visited.addIfNotIn(node))
                //    continue methodloop;

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
                    if ((restart > 0) && ((System.currentTimeMillis() - totalSearchTime) > nextRestart)) {
                        restartCount++;
                        nextRestart = restartCount * restart;
                        System.out.println("Seems to be a bad run, let's try again! -> restart");
                        fringe.clear();
                        fringe.add(firstSearchNode);
                        htnPlanningInstance.randomSeed += 100;
                        htnPlanningInstance.random = new Random(htnPlanningInstance.randomSeed);
                        continue planningloop;
                    }
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

        /*
        try {
            BufferedWriter bwAllSols = new BufferedWriter(new FileWriter("/home/dh/Schreibtisch/anytime/times.txt"));
            Iterator<Long> iter1 = timeStamps.iterator();
            Iterator<String> iter2 = tlts.iterator();
            while (iter1.hasNext()) {
                int time = (int) (iter1.next() - startedSearch);
                bwAllSols.write(" " + time);
                bwAllSols.write(" ");
                bwAllSols.write(iter2.next());
                bwAllSols.write("\n");
            }
            bwAllSols.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        if (this.findShortest)
            info.add("30 progression:91:findShortestPlan", 1);
        else
            info.add("30 progression:91:findShortestPlan", 0);
        info.add("30 progression:92:foundShortestPlanAfter", (int) (foundFirstPlanAfter - startedSearch));
        info.add("30 progression:93:foundFirstPlanAfter", (int) (foundShortestPlan - startedSearch));
        info.add("30 progression:94:foundPlans", foundPlans);
        info.add("30 progression:95:randomSeed", htnPlanningInstance.randomSeed);

        // write statistics
        if (solution != null)
            info.set(STATUS, "solved");
        else if (exitDueToTimeLimit)
            info.set(STATUS, "timeout");
        else
            info.set(STATUS, "proven_unsolvable");

        if (this.aStar) {
            info.set(A_STAR, 1);
        } else {
            info.set(A_STAR, 0);
        }

        info.set(HEURISTIC, firstSearchNode.heuristic.getClass().toString());

        info.set(NUM_SEARCH_NODES, searchnodes);
        info.set(UNIT_PROPAGATION, unitPropagation);
        setSolInfo(solution, info);

        if (output)
            System.out.println("Generated search nodes (total): " + searchnodes);
        if (output)
            System.out.println("Search time: " + (System.currentTimeMillis() - totalSearchTime) + " ms");
        return solution;
    }

    // ABSTRACT CHOICE
    public List<Object> searchWithAbstractBranching(ProgressionNetwork firstSearchNode, InformationCapsule info, TimeCapsule timing) {
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

        boolean collectTlts = false;
        LinkedList<Long> timeStamps = new LinkedList<>();
        LinkedList<String> tlts = new LinkedList<>();

        int restartCount = -1;
        long restart = -1;// (2 * 60 * 1000);
        long nextRestart = -1;//restartCount * restart;

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
                        if (collectTlts) {
                            collectPlanRecData(timeStamps, tlts, node);
                        }
                        foundPlans++;
                        if (!this.findShortest && !collectTlts) {
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
                    if ((restart > 0) && ((System.currentTimeMillis() - totalSearchTime) > nextRestart)) {
                        restartCount++;
                        nextRestart = restartCount * restart;
                        System.out.println("Seems to be a bad run, let's try again! -> restart");
                        fringe.clear();
                        fringe.add(firstSearchNode);
                        htnPlanningInstance.randomSeed += 100;
                        htnPlanningInstance.random = new Random(htnPlanningInstance.randomSeed);
                        continue planningloop;
                    }
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

            for (ProgressionPlanStep oneAbs : n.getFirstAbstractTasks()) {
                methodloop:
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

                        fringe.add(node);
                        if (node.metric < bestMetric) {
                            bestMetric = node.metric;
                            if (output)
                                System.out.println(getInfoStr(searchnodes, fringe.size(), bestMetric, n, totalSearchTime));
                        }
                    }
                    searchnodes++;
                    if ((System.currentTimeMillis() - lastInfo) > 1000) {
                        if ((restart > 0) && ((System.currentTimeMillis() - totalSearchTime) > nextRestart)) {
                            restartCount++;
                            nextRestart = restartCount * restart;
                            System.out.println("Seems to be a bad run, let's try again! -> restart");
                            fringe.clear();
                            fringe.add(firstSearchNode);
                            htnPlanningInstance.randomSeed += 100;
                            htnPlanningInstance.random = new Random(htnPlanningInstance.randomSeed);
                            continue planningloop;
                        }
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
        }
        System.out.println("Number of nodes in final fringe: " + fringe.size());
        timing.stop(SEARCH_TIME);

        if (this.findShortest)
            info.add("30 progression:91:findShortestPlan", 1);
        else
            info.add("30 progression:91:findShortestPlan", 0);
        info.add("30 progression:92:foundShortestPlanAfter", (int) (foundFirstPlanAfter - startedSearch));
        info.add("30 progression:93:foundFirstPlanAfter", (int) (foundShortestPlan - startedSearch));
        info.add("30 progression:94:foundPlans", foundPlans);
        info.add("30 progression:95:randomSeed", htnPlanningInstance.randomSeed);

        // write statistics
        if (solution != null)
            info.set(STATUS, "solved");
        else if (exitDueToTimeLimit)
            info.set(STATUS, "timeout");
        else
            info.set(STATUS, "proven_unsolvable");

        if (this.aStar) {
            info.set(A_STAR, 1);
        } else {
            info.set(A_STAR, 0);
        }

        info.set(HEURISTIC, firstSearchNode.heuristic.getClass().toString());

        info.set(NUM_SEARCH_NODES, searchnodes);
        info.set(UNIT_PROPAGATION, unitPropagation);
        setSolInfo(solution, info);

        if (output)
            System.out.println("Generated search nodes (total): " + searchnodes);
        if (output)
            System.out.println("Search time: " + (System.currentTimeMillis() - totalSearchTime) + " ms");
        return solution;
    }

    public void collectPlanRecData(LinkedList<Long> timeStamps, LinkedList<String> tlts, ProgressionNetwork node) {
        timeStamps.add(System.currentTimeMillis());
        findTlt:
        for (Object a : node.solution) {
            if (a instanceof Integer)
                continue;
            GroundedDecompositionMethod dm = (GroundedDecompositionMethod) a;
            if (dm.groundAbstractTask().task().name().startsWith("tlt") && (dm.subPlanGroundedTasksWithoutInitAndGoal().size() > 0)) {
                for (int i = 0; i < dm.subPlanGroundedTasksWithoutInitAndGoal().size(); i++) {
                    GroundTask ps2 = dm.subPlanGroundedTasksWithoutInitAndGoal().apply(i);
                    tlts.add(ps2.longInfo());
                    break findTlt;
                }
            }
        }
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
        ic.set(NUM_PRIM_TASKS, numPrim);
        ic.set(NUM_SHOP_TASKS, numShop);
        ic.set(NUM_DECOMPOSITIONS, numDec);
        if (FirstDecTask.length() > 0) {
            ic.set(INFERRED_TLT, FirstDecTask);
            ic.set(ENFORCED_PREFIX_LENGTH, enforcedPrefLength);
        }
        ic.set(SOLUTION, PrimitivePlan);
    }
}
