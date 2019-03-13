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

package de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedComposition;

import de.uniulm.ki.panda3.progression.TDGReachabilityAnalysis.IActionReachability;
import de.uniulm.ki.panda3.progression.TDGReachabilityAnalysis.TDGLandmarkFactory;
import de.uniulm.ki.panda3.progression.TDGReachabilityAnalysis.TaskReachabilityGraph;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dh on 05.05.17.
 */
public class RelaxedCompositionSTRIPS extends RelaxedCompositionEncoding {
    int numTasks;
    int numAnM;
    int numExtenedStateFeatures;

    public HashMap<ProMethod, Integer> MethodToIndex;
    public ProMethod[] IndexToMethod;

    public int[] reachable;
    public int[] reached;

    public int firstTdrIndex;
    public int lastTdrIndex;

    public int firstTaskCompIndex;
    public int lastTaskCompIndex;

    public int lastOverallIndex;

    public RelaxedCompositionSTRIPS(SasPlusProblem p) {
        assert (p.correctModel());
        this.createdFromStrips = p.createdFromStrips;
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
        generateTaskCompGraph(methods, initialTasks, true);
    }

    public void generateTaskCompGraph(HashMap<Task, List<ProMethod>> methods,
                                      List<ProgressionPlanStep> initialTasks, boolean calcTDR) {

        this.numAnM = createMethodLookupTable(methods);
        this.numTasks = ProgressionNetwork.indexToTask.length;
        //tdRechability = new TDGLandmarkFactory(methods, initialTasks, this.numTasks, this.numOfOperators);
        if(calcTDR)
          tdRechability = new TaskReachabilityGraph(methods, initialTasks, numTasks, this.numOfOperators);

        // set indices
        this.firstTdrIndex = this.numOfStateFeatures;
        this.lastTdrIndex = this.firstTdrIndex + this.numOfOperators - 1;
        this.firstTaskCompIndex = this.lastTdrIndex + 1;
        this.lastTaskCompIndex = this.firstTaskCompIndex + this.numTasks - 1;
        this.lastOverallIndex = this.lastTaskCompIndex;

        this.numExtenedStateFeatures = this.lastOverallIndex + 1;

        reachable = new int[numOfOperators];
        reached = new int[numTasks];
        for (int i = 0; i < numTasks; i++)
            reached[i] = firstTaskCompIndex + i;
        for (int i = 0; i < this.numOfOperators; i++)
            reachable[i] = firstTdrIndex + i;

        int[][] tPrecLists = new int[numAnM][];
        int[] tNumPrecs = new int[numAnM];
        int[][] tAddLists = new int[numAnM][];
        int[][] tDelLists = new int[numAnM][];
        String[] tOpNames = new String[numAnM];
        int[] tCosts = new int[numAnM];

        /**
         * The original actions get an additional prec that represents its top-down-reachability and an additional add
         * effect that marks this action (aka primitive task) as reached via composition.
         */
        for (int iA = 0; iA < this.numOfOperators; iA++) {
            tNumPrecs[iA] = this.precLists[iA].length + 1;

            tPrecLists[iA] = new int[tNumPrecs[iA]];
            int iP;
            for (iP = 0; iP < tNumPrecs[iA] - 1; iP++) {
                tPrecLists[iA][iP] = this.precLists[iA][iP];
            }
            tPrecLists[iA][iP] = reachable[iA];

            tAddLists[iA] = new int[this.addLists[iA].length + 1];
            int iE;
            for (iE = 0; iE < this.addLists[iA].length; iE++) {
                tAddLists[iA][iE] = this.addLists[iA][iE];
            }
            tAddLists[iA][iE] = reached[iA];

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
                tPrecLists[iM][iSubTask] = reached[ProgressionNetwork.taskToIndex.get(t)];
            }

            tAddLists[iM] = new int[1];
            tAddLists[iM][0] = reached[ProgressionNetwork.taskToIndex.get(m.m.abstractTask())];

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

        int group;
        if(indexToMutexGroup.length >0)
        group = indexToMutexGroup[indexToMutexGroup.length - 1] + 1;
        else
        group = 0;
        for (int i = indexToMutexGroup.length; i < tIndexToMutexGroup.length; i++)
            tIndexToMutexGroup[i] = group++;
        indexToMutexGroup = tIndexToMutexGroup;

        this.removeDublicates(false);
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
        varNames = tFactStrs;
    }

    @Override
    public BitSet initS0() {
        return new BitSet();
    }

    @Override
    public void setReachable(BitSet bSet, int i) {
        bSet.set(this.reachable[i]);
    }

    @Override
    public void setReached(BitSet bSet, int i) {
        bSet.set(this.reached[i]);
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

}
