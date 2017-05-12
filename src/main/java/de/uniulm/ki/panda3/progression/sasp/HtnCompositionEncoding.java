package de.uniulm.ki.panda3.progression.sasp;

import de.uniulm.ki.panda3.progression.htn.operators.method;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.TopDownReachabilityGraph;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.*;

/**
 * Created by dh on 05.05.17.
 */
public class HtnCompositionEncoding extends SasPlusProblem {
    int numTasks;
    int numAnM;
    int numExtenedStateFeatures;

    HashMap<method, Integer> MethodToIndex;
    method[] IndexToMethod;

    TopDownReachabilityGraph tdRechability;
    private int firstTdrIndex;
    private int lastTdrIndex;

    private int firstTaskCompIndex;
    private int lastTaskCompIndex;

    private int firstHtnIndex;
    private int lastOverallIndex;

    public HtnCompositionEncoding(String Filename) throws Exception {
        super(Filename);
    }

    public HtnCompositionEncoding(SasPlusProblem p) {
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
    }

    public void geneateTaskCompGraph(HashMap<Task, List<method>> methods,
                                     List<ProgressionPlanStep> initialTasks) {

        this.numAnM = createMethodLookupTable(methods);
        this.numTasks = ProgressionNetwork.indexToTask.length;
        tdRechability = new TopDownReachabilityGraph(methods, initialTasks, this.numTasks);

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
        }

        /**
         * Prepare one action for every method. Its preconditions represent one subtask that has been reached through
         * composition. Its effect marks the task decomposed by the method as composed.
         */
        for (int methodI = this.numOfOperators; methodI < numAnM; methodI++) {
            method m = this.getMethod(methodI);
            tNumPrecs[methodI] = m.subtasks.length;
            tPrecLists[methodI] = new int[tNumPrecs[methodI]];

            for (int iSubTask = 0; iSubTask < m.subtasks.length; iSubTask++) {
                Task t = m.subtasks[iSubTask];
                int taskIndex = getTaskIndex(t);
                tPrecLists[methodI][iSubTask] = taskIndex;
            }

            int compTaskIndex = getTaskIndex(m.m.abstractTask());
            tAddLists[methodI] = new int[1];
            tAddLists[methodI][0] = compTaskIndex;
        }
        this.numPrecs = tNumPrecs;
        this.precLists = tPrecLists;
        this.addLists = tAddLists;

        // extend delete lists with empty arrays (new ops have no del effects)
        for (int i = 0; i < this.delLists.length; i++)
            tDelLists[i] = this.delLists[i];
        for (int i = this.delLists.length; i < tDelLists.length; i++)
            tDelLists[i] = new int[0];
        this.delLists = tDelLists;

        this.numOfStateFeatures = numExtenedStateFeatures;
        this.calcInverseMappings();
        this.calcExtendedDelLists();
        /* todo
            - int[] firstIndex; // maps a mutex-group-index to the first index in the vector of state features
            - int[] lastIndex; // maps a mutex-group-index to the last index in the vector of state features
            - int[] indexToMutexGroup; // maps some feature-index to the corresponding mutex group
            - expandedDelLists
        */
    }

    private int createMethodLookupTable(HashMap<Task, List<method>> methods) {
        int methodID = this.numOfOperators;
        this.MethodToIndex = new HashMap<>();

        // count methods, create array
        int anzMethods = 0;
        for (List<method> val : methods.values())
            anzMethods += val.size();

        this.IndexToMethod = new method[anzMethods];

        for (List<method> val : methods.values())
            for (method m : val) {
                assert (!this.MethodToIndex.containsKey(m));
                this.MethodToIndex.put(m, methodID);
                this.setMethodIndex(methodID, m);
                methodID++;
            }

        return methodID;
    }

    private method getMethod(int index) {
        return IndexToMethod[index - this.numOfOperators];
    }

    private void setMethodIndex(int index, method m) {
        IndexToMethod[index - this.numOfOperators] = m;
    }

    private int getTaskIndex(Task t) {
        return this.firstTaskCompIndex + ProgressionNetwork.taskToIndex.get(t);
    }

}
