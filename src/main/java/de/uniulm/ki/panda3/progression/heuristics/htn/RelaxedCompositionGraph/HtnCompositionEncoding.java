package de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedCompositionGraph;

import de.uniulm.ki.panda3.progression.TDGReachabilityAnalysis.IActionReachability;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.TDGReachabilityAnalysis.TDGLandmarkFactory;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.*;

/**
 * Created by dh on 05.05.17.
 */
public class HtnCompositionEncoding extends SasPlusProblem {
    int numTasks;
    int numAnM;
    int numExtenedStateFeatures;

    public HashMap<ProMethod, Integer> MethodToIndex;
    public ProMethod[] IndexToMethod;

    public IActionReachability tdRechability;
    public int firstTdrIndex;
    public int lastTdrIndex;

    public int firstTaskCompIndex;
    public int lastTaskCompIndex;

    public int firstHtnIndex;
    public int lastOverallIndex;
    public int methodCosts = 1;

    public HtnCompositionEncoding(String Filename) throws Exception {
        super(Filename);
    }

    public HtnCompositionEncoding(SasPlusProblem p) {
        assert (p.correctModel());

        this.numOfStateFeatures = p.numOfStateFeatures;
        this.numOfOperators = p.numOfOperators;
        this.firstIndex = p.firstIndex;
        this.lastIndex = p.lastIndex;
        this.indexToMutexGroup = p.indexToMutexGroup;
        this.precLists = p.precLists;
        this.addLists = p.addLists;
        this.delLists = p.delLists;
        this.expandedDelLists = p.expandedDelLists;
        this.precToTask = p.precToTask;
        this.addToTask = p.addToTask;
        this.numPrecs = p.numPrecs;
        this.s0List = p.s0List;
        this.costs = p.costs;
        this.opNames = p.opNames;
        this.varNames = p.varNames;
        this.values = p.values;
        this.gList = p.gList;
        this.factStrs = p.factStrs;
    }

    public void generateTaskCompGraph(HashMap<Task, List<ProMethod>> methods,
                                      List<ProgressionPlanStep> initialTasks) {

        this.numAnM = createMethodLookupTable(methods);
        this.numTasks = ProgressionNetwork.indexToTask.length;
        tdRechability = new TDGLandmarkFactory(methods, initialTasks, this.numTasks, this.numOfOperators);

        // set indices
        this.firstTdrIndex = this.numOfStateFeatures;
        this.lastTdrIndex = this.firstTdrIndex + this.numOfOperators - 1;
        this.firstTaskCompIndex = this.lastTdrIndex + 1;
        this.lastTaskCompIndex = this.firstTaskCompIndex + this.numTasks - 1;
        this.firstHtnIndex = this.firstTdrIndex;
        this.lastOverallIndex = this.lastTaskCompIndex;

        this.numExtenedStateFeatures = this.lastOverallIndex + 1;

        int[][] tPrecLists = new int[numAnM][];
        int[][] tAddLists = new int[numAnM][];
        int[][] tDelLists = new int[numAnM][];
        String[] tOpNames = new String[this.numAnM];
        int[] tCosts = new int[numAnM];

        /**
         * The original actions get an additional prec that represents its top-down-reachability and an additional add
         * effect that marks this action (aka primitive task) as reached via composition.
         */
        int[] tNumPrecs = new int[this.numAnM];
        for (int iA = 0; iA < this.numOfOperators; iA++) {
            tNumPrecs[iA] = this.precLists[iA].length + 1;

            tPrecLists[iA] = new int[tNumPrecs[iA]];
            int iP;
            for (iP = 0; iP < tNumPrecs[iA] - 1; iP++) {
                tPrecLists[iA][iP] = this.precLists[iA][iP];
            }
            tPrecLists[iA][iP] = firstTdrIndex + iA;

            tAddLists[iA] = new int[this.addLists[iA].length + 1];
            int iE;
            for (iE = 0; iE < this.addLists[iA].length; iE++) {
                tAddLists[iA][iE] = this.addLists[iA][iE];
            }
            tAddLists[iA][iE] = firstTaskCompIndex + iA;

            tOpNames[iA] = opNames[iA];
            tCosts[iA] = costs[iA];
        }

        /**
         * Prepare one action for every method. Its preconditions represent one subtask that has been reached through
         * composition. Its effect marks the task decomposed by the method as composed.
         */
        for (int iM = this.numOfOperators; iM < numAnM; iM++) {
            ProMethod m = this.getMethod(iM);
            tNumPrecs[iM] = m.subtasks.length;
            tPrecLists[iM] = new int[tNumPrecs[iM]];

            for (int iSubTask = 0; iSubTask < m.subtasks.length; iSubTask++) {
                Task t = m.subtasks[iSubTask];
                int taskIndex = getTaskIndex(t);
                tPrecLists[iM][iSubTask] = taskIndex;
            }

            int compTaskIndex = getTaskIndex(m.m.abstractTask());
            tAddLists[iM] = new int[1];
            tAddLists[iM][0] = compTaskIndex;

            tOpNames[iM] = m.m.name() + "@" + m.m.abstractTask().shortInfo();
            tCosts[iM] = this.methodCosts;
        }
        this.numPrecs = tNumPrecs;
        this.precLists = tPrecLists;
        this.addLists = tAddLists;
        this.opNames = tOpNames;
        this.costs = tCosts;

        // extend delete lists with empty arrays (new ops have no del effects)
        for (int i = 0; i < this.delLists.length; i++)
            tDelLists[i] = this.delLists[i];
        for (int i = this.delLists.length; i < tDelLists.length; i++)
            tDelLists[i] = new int[0];
        this.delLists = tDelLists;

        this.numOfStateFeatures = numExtenedStateFeatures;
        this.numOfOperators = numAnM;

        int[] tIndexToMutexGroup = new int[numExtenedStateFeatures];
        for (int i = 0; i < indexToMutexGroup.length; i++)
            tIndexToMutexGroup[i] = indexToMutexGroup[i];

        int group = indexToMutexGroup[indexToMutexGroup.length - 1] + 1;
        for (int i = indexToMutexGroup.length; i < tIndexToMutexGroup.length; i++)
            tIndexToMutexGroup[i] = group++;
        indexToMutexGroup = tIndexToMutexGroup;

        this.calcMutexGroupIndices();
        this.calcRanges();
        this.calcInverseMappings();
        this.calcExtendedDelLists();

        String[] tFactStrs = new String[numOfStateFeatures];

        for (int i = 0; i < firstTdrIndex; i++) {
            tFactStrs[i] = factStrs[i];
        }

        for (int i = firstTdrIndex; i <= lastTdrIndex; i++) {
            tFactStrs[i] = "tdr-" + opNames[i - firstTdrIndex];
        }

        for (int i = firstTaskCompIndex; i <= lastTaskCompIndex; i++) {
            tFactStrs[i] = "bur-" + ProgressionNetwork.indexToTask[i - firstTaskCompIndex].shortInfo();
        }
        factStrs = tFactStrs;
        //System.out.println(this.toString());
        assert (this.correctModel());
    }

    private int createMethodLookupTable(HashMap<Task, List<ProMethod>> methods) {
        int methodID = this.numOfOperators;
        this.MethodToIndex = new HashMap<>();

        // count methods, create array
        int anzMethods = 0;
        for (List<ProMethod> val : methods.values())
            anzMethods += val.size();

        this.IndexToMethod = new ProMethod[anzMethods];

        for (List<ProMethod> val : methods.values())
            for (ProMethod m : val) {
                assert (!this.MethodToIndex.containsKey(m));
                this.MethodToIndex.put(m, methodID);
                this.setMethodIndex(methodID, m);
                methodID++;
            }

        return methodID;
    }

    private ProMethod getMethod(int index) {
        return IndexToMethod[index - this.numOfOperators];
    }

    private void setMethodIndex(int index, ProMethod m) {
        IndexToMethod[index - this.numOfOperators] = m;
    }

    private int getTaskIndex(Task t) {
        return this.firstTaskCompIndex + ProgressionNetwork.taskToIndex.get(t);
    }
}
