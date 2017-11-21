package de.uniulm.ki.panda3.progression.htn.search.searchRoutine;

import de.uniulm.ki.panda3.configuration.Information;
import de.uniulm.ki.panda3.progression.TDGReachabilityAnalysis.TaskReachabilityGraph;
import de.uniulm.ki.panda3.progression.htn.ProPlanningInstance;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.search.SolutionStep;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.htn.search.fringe.AlternatingFringe;
import de.uniulm.ki.panda3.progression.htn.search.fringe.IFringe;
import de.uniulm.ki.panda3.progression.htn.search.fringe.QueueBasedFringe;
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.SimpleDecompositionMethod;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;
import de.uniulm.ki.util.InformationCapsule;
import de.uniulm.ki.util.TimeCapsule;

import java.util.*;

/**
 * Created by dh on 15.09.16.
 */
public class PriorityQueueSearch extends ProgressionSearchRoutine {
    public int greediness = 1;

    static public enum abstractTaskSelection {
        branchOverAll, random, methodCount, decompDepth;

        public static abstractTaskSelection parse(String text) {
            if (text.equals("branchOverAll")) return branchOverAll;
            if (text.equals("random")) return random;
            if (text.equals("methodCount")) return methodCount;
            if (text.equals("decompositionDepth")) return decompDepth;
            throw new IllegalArgumentException("Unknown selection strategy " + text);
        }
    }

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

    public PriorityQueueSearch(boolean aStar, boolean output, boolean findShortest,
                               abstractTaskSelection taskSelectionStrategy) {
        this.aStar = aStar;
        this.output = output;
        this.findShortest = findShortest;
        this.taskSelection = taskSelectionStrategy;
    }

    public SolutionStep search(ProgressionNetwork firstSearchNode) {
        InformationCapsule ic = new InformationCapsule();
        TimeCapsule tc = new TimeCapsule();
        return search(firstSearchNode, ic, tc);
    }

    abstractTaskSelection taskSelection = abstractTaskSelection.random;

    public SolutionStep search(ProgressionNetwork firstSearchNode, InformationCapsule info, TimeCapsule timing) {
        if (output)
            System.out.println("\nStarting priority queue search");
        int searchnodes = 1;
        int foundPlans = 0;
        int planLength = -1;
        long foundFirstPlanAfter = 0;
        long foundShortestPlan = 0;
        int unitPropagation = 0;
        SolutionStep solution = null;
        long totalSearchTime = System.currentTimeMillis();
        long lastInfo = System.currentTimeMillis();
        //PriorityQueue<ProgressionNetwork> fringe = new PriorityQueue<>();

        IFringe<ProgressionNetwork> fringe;
        if (firstSearchNode.heuristic.supportsHelpfulActions) {
            ProgressionNetwork.useHelpfulActions = true;
            fringe = new AlternatingFringe<>();
            firstSearchNode.helpfulActions = new BitSet();
        } else {
            fringe = new QueueBasedFringe<>();
        }

        fringe.add(firstSearchNode);
        long startedSearch = System.currentTimeMillis();

        boolean collectTlts = false;
        LinkedList<Long> timeStamps = new LinkedList<>();
        LinkedList<String> tlts = new LinkedList<>();

        int restartCount = -1;
        long restart = -1;// (2 * 60 * 1000);
        long nextRestart = -1;//restartCount * restart;

        boolean helpfulAction = false;
        timing.start(SEARCH_TIME);
        planningloop:
        while (!fringe.isEmpty()) {
            ProgressionNetwork n = fringe.poll();
            actionloop:
            for (ProgressionPlanStep ps : n.getFirstPrimitiveTasks()) {
                if (!n.isApplicable(ps.action))
                    continue actionloop;

                ProgressionNetwork node = n.apply(ps);
                node.id = searchnodes;
                node.heuristic = n.heuristic.update(node, ps);
                node.goalRelaxedReachable = node.heuristic.goalRelaxedReachable();
                if (node.goalRelaxedReachable) {
                    node.heuristicVal = node.heuristic.getHeuristic();
                    if (aStar) {
                        node.metric = node.heuristicVal + (node.solution.getLength() / greediness);
                    } else {
                        node.metric = node.heuristicVal;
                    }

                    if (node.heuristic.supportsHelpfulActions) {
                        node.helpfulActions = node.heuristic.helpfulOps();
                        helpfulAction = n.isHelpFulAction(ps.action);
                    }

                    // early goal test - NON-OPTIMAL
                    if (node.isGoal()) {
                        int numSteps = node.solution.getPrimitiveCount();
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
                        if (!findShortest && !collectTlts) {
                            break planningloop;
                        }
                    } else {
                        fringe.add(node, helpfulAction);
                    }
                }
            }

            if (n.getFirstAbstractTasks().size() == 0) continue planningloop;
            // which task shall be decomposed?
            ProgressionPlanStep oneAbs = null;
            if (taskSelection == abstractTaskSelection.random)
                oneAbs = n.getFirstAbstractTasks().get(ProPlanningInstance.random.nextInt(n.getFirstAbstractTasks().size()));
            else if (taskSelection == abstractTaskSelection.methodCount) { // minimize branching
                int minMethods = Integer.MAX_VALUE;
                for (ProgressionPlanStep ps : n.getFirstAbstractTasks()) {
                    if (ps.methods.size() < minMethods) {
                        minMethods = ps.methods.size();
                        oneAbs = ps;
                    } else if ((ps.methods.size() == minMethods) && (ProPlanningInstance.random.nextBoolean())) {
                        minMethods = ps.methods.size();
                        oneAbs = ps;
                    }
                }
            } else {
                int minDepth = Integer.MAX_VALUE;
                for (ProgressionPlanStep ps : n.getFirstAbstractTasks()) {
                    int depth = TaskReachabilityGraph.maxDecompDepth[TaskReachabilityGraph.tToI(ps.getTask())];
                    if (depth < minDepth) {
                        minDepth = depth;
                        oneAbs = ps;
                    } else if ((ps.methods.size() == minDepth) && (ProPlanningInstance.random.nextBoolean())) {
                        minDepth = ps.methods.size();
                        oneAbs = ps;
                    }
                }
            }

            methodloop:
            for (ProMethod m : oneAbs.methods) {
                ProgressionNetwork node = n.decompose(oneAbs, m);
                // todo: add unit propagation here
                node.heuristic = n.heuristic.update(node, oneAbs, m);
                node.id = searchnodes++;
                node.goalRelaxedReachable = node.heuristic.goalRelaxedReachable();

                if (node.goalRelaxedReachable) {
                    node.heuristicVal = node.heuristic.getHeuristic();
                    if (aStar) {
                        node.metric = node.heuristicVal + (node.solution.getLength() / greediness);
                    } else {
                        node.metric = node.heuristicVal;
                    }

                    if (node.heuristic.supportsHelpfulActions) {
                        node.helpfulActions = node.heuristic.helpfulOps();
                        helpfulAction = n.isHelpFulMethod(m);
                    }

                    fringe.add(node, helpfulAction);
                }
            }
            if ((System.currentTimeMillis() - lastInfo) > 1000) {
                if ((restart > 0) && ((System.currentTimeMillis() - totalSearchTime) > nextRestart)) {
                    restartCount++;
                    nextRestart = restartCount * restart;
                    System.out.println("Seems to be a bad run, let's try again! -> restart");
                    fringe.clear();
                    fringe.add(firstSearchNode);
                    //ProPlanningInstance.randomSeed += 100;
                    //ProPlanningInstance.random = new Random(ProPlanningInstance.randomSeed);
                    continue planningloop;
                }
                if ((wallTime > 0) && ((System.currentTimeMillis() - totalSearchTime) > wallTime)) {
                    System.out.println("Reached time limit, search will stop.");
                    exitDueToTimeLimit = true;
                    break planningloop;
                }
                lastInfo = System.currentTimeMillis();
                if (output)
                    System.out.println(getInfoStr(searchnodes, fringe.size(), greediness, n, totalSearchTime));
            }
        }
        System.out.println("Number of nodes in final fringe: " + fringe.size());
        if (fringe.size() == 0) info.set(Information.SEARCH_SPACE_FULLY_EXPLORED(),"true");
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
        //info.add("30 progression:95:randomSeed", ProPlanningInstance.randomSeed);

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

        info.set(HEURISTIC, firstSearchNode.heuristic.getName());

        info.set(NUM_SEARCH_NODES, searchnodes);
        info.set(UNIT_PROPAGATION, unitPropagation);
        setSolInfo(solution, info);

        if (output) {
            System.out.println("Generated search nodes (total): " + searchnodes);
            System.out.println("Search time: " + (System.currentTimeMillis() - totalSearchTime) + " ms");
        }
        return solution;
    }

    // ABSTRACT CHOICE
    public SolutionStep searchWithAbstractBranching(ProgressionNetwork firstSearchNode, InformationCapsule info, TimeCapsule timing) {
        if (output)
            System.out.println("\nStarting priority queue search");
        int searchnodes = 1;
        int foundPlans = 0;
        int planLength = -1;
        long foundFirstPlanAfter = 0;
        long foundShortestPlan = 0;
        int unitPropagation = 0;
        int bestMetric = Integer.MAX_VALUE;
        SolutionStep solution = null;
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
                if (!n.isApplicable(ps.action))
                    continue actionloop;

                ProgressionNetwork node = n.apply(ps);

                node.id = searchnodes;
                node.heuristic = n.heuristic.update(node, ps);
                node.metric = node.heuristic.getHeuristic();
                if (aStar) {
                    node.metric += node.solution.getLength();
                }

                node.goalRelaxedReachable = node.heuristic.goalRelaxedReachable();

                if (node.goalRelaxedReachable) {
                    // early goal test - NON-OPTIMAL
                    if (node.isGoal()) {
                        int numSteps = node.solution.getPrimitiveCount();
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
                            System.out.println(getInfoStr(searchnodes, fringe.size(), greediness, n, totalSearchTime));
                    }
                }
                searchnodes++;
            }

            for (ProgressionPlanStep oneAbs : n.getFirstAbstractTasks()) {
                methodloop:
                for (ProMethod m : oneAbs.methods) {
                    ProgressionNetwork node = n.decompose(oneAbs, m);

                    // todo: add unit propagation here
                    node.heuristic = n.heuristic.update(node, oneAbs, m);
                    node.metric = node.heuristic.getHeuristic();
                    if (aStar) {
                        node.metric += node.solution.getLength();
                    }

                    node.goalRelaxedReachable = node.heuristic.goalRelaxedReachable();

                    if (node.goalRelaxedReachable) {

                        fringe.add(node);
                        if (node.metric < bestMetric) {
                            bestMetric = node.metric;
                            if (output)
                                System.out.println(getInfoStr(searchnodes, fringe.size(), greediness, n, totalSearchTime));
                        }
                    }
                    searchnodes++;
                }
            }
            if ((System.currentTimeMillis() - lastInfo) > 1000) {
                if ((restart > 0) && ((System.currentTimeMillis() - totalSearchTime) > nextRestart)) {
                    restartCount++;
                    nextRestart = restartCount * restart;
                    System.out.println("Seems to be a bad run, let's try again! -> restart");
                    fringe.clear();
                    fringe.add(firstSearchNode);
                    //ProPlanningInstance.randomSeed += 100;
                    //ProPlanningInstance.random = new Random(ProPlanningInstance.randomSeed);
                    continue planningloop;
                }
                if ((wallTime > 0) && ((System.currentTimeMillis() - totalSearchTime) > wallTime)) {
                    System.out.println("Reached time limit, search will stop.");
                    exitDueToTimeLimit = true;
                    break planningloop;
                }
                lastInfo = System.currentTimeMillis();
                if (output)
                    System.out.println(getInfoStr(searchnodes, fringe.size(), greediness, n, totalSearchTime));
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
        //info.add("30 progression:95:randomSeed", ProPlanningInstance.randomSeed);

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
        for (Object a : node.solution.getSolution()) {
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


    @Override
    public String SearchName() {
        return "Priority Queue";
    }

    private void setSolInfo(SolutionStep solution, InformationCapsule ic) {
        String PrimitivePlan = "";
        String FirstDecTask = "";
        int numPrim = 0;
        int enforcedPrefLength = 0;
        int numShop = 0;
        int numDec = 0;

        if (solution != null) {
            for (Object a : solution.getSolution()) {
                if (a instanceof Integer) {
                    if (ProgressionNetwork.ShopPrecActions.contains(a))
                        numShop++;
                    else {
                        numPrim++;
                        if (PrimitivePlan.length() > 0) {
                            PrimitivePlan += "&";
                        }
                        String primName = ProgressionNetwork.indexToTask[(Integer) a].longInfo();
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
                    SimpleDecompositionMethod dm = (SimpleDecompositionMethod) a;
                    if (dm.abstractTask().name().startsWith("tlt") && (dm.subPlan().planStepsWithoutInitGoal().size() > 0)) {
                        for (int i = 0; i < dm.subPlan().planStepsWithoutInitGoal().size(); i++) {
                            Task ps = dm.subPlan().planStepsWithoutInitGoal().apply(i).schema();
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
        //ic.set(SOLUTION, PrimitivePlan);
    }
}
