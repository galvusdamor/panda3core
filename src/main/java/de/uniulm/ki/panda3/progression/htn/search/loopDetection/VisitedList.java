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

package de.uniulm.ki.panda3.progression.htn.search.loopDetection;

import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;

import java.util.*;

/**
 * Created by dh on 21.09.16.
 */
public class VisitedList {

    private static final short cUnequal = -1;
    private static final short cIdentical = 0;
    private static final short cRefIdentical = 1;

    private final Map<Integer, Map<Integer, Map<BitSet, List<List<ProgressionPlanStep>[]>>>> visited;
    private int calls = 0;
    private int hits = 0;
    private long totaltime = 0;

    public int getNumCalls() {
        return this.calls;
    }

    public int getNumHits() {
        return this.hits;
    }

    public long getTotalTime() {
        return this.totaltime;
    }

    public VisitedList() {
        visited = new HashMap<>();
    }

    public boolean addIfNotIn(ProgressionNetwork n) {
        long time = System.currentTimeMillis();
        this.calls++;

        // number of tasks
        int t = n.getNumberOfTasks();
        Map<Integer, Map<BitSet, List<List<ProgressionPlanStep>[]>>> sameTaskNum = visited.get(t);
        if (sameTaskNum == null) {
            // prepare network
            List<ProgressionPlanStep>[] network = new List[2];
            network[0] = n.getFirstPrimitiveTasks();
            network[1] = n.getFirstAbstractTasks();
            List<List<ProgressionPlanStep>[]> networks = new LinkedList<>();
            networks.add(network);

            // prepare maps: most inner to most outer
            Map<BitSet, List<List<ProgressionPlanStep>[]>> samePrimTaskNum = new HashMap<>();
            samePrimTaskNum.put(n.state, networks);
            sameTaskNum = new HashMap<>();
            sameTaskNum.put(n.getNumberOfPrimitiveTasks(), samePrimTaskNum);
            visited.put(t, sameTaskNum);

            this.totaltime += (System.currentTimeMillis() - time);
            return false;
        } else {
            // number of primitive tasks
            t = n.getNumberOfPrimitiveTasks();
            Map<BitSet, List<List<ProgressionPlanStep>[]>> samePrimTaskNum = sameTaskNum.get(t);
            if (samePrimTaskNum == null) {
                // prepare network
                List<ProgressionPlanStep>[] network = new List[2];
                network[0] = n.getFirstPrimitiveTasks();
                network[1] = n.getFirstAbstractTasks();
                List<List<ProgressionPlanStep>[]> networks = new LinkedList<>();
                networks.add(network);

                // prepare maps: most inner to most outer
                samePrimTaskNum = new HashMap<>();
                samePrimTaskNum.put(n.state, networks);
                sameTaskNum.put(n.getNumberOfPrimitiveTasks(), samePrimTaskNum);

                this.totaltime += (System.currentTimeMillis() - time);
                return false;
            } else {
                // state
                List<List<ProgressionPlanStep>[]> networks = samePrimTaskNum.get(n.state);
                if (networks == null) {
                    // prepare network
                    List<ProgressionPlanStep>[] network = new List[2];
                    network[0] = n.getFirstPrimitiveTasks();
                    network[1] = n.getFirstAbstractTasks();
                    networks = new LinkedList<>();
                    networks.add(network);

                    // prepare maps: most inner to most outer
                    samePrimTaskNum.put(n.state, networks);

                    this.totaltime += (System.currentTimeMillis() - time);
                    return false;
                } else {
                    // network
                    for (List<ProgressionPlanStep>[] network : networks) {
                        String storedNet = ProgressionNetwork.networkToString(network[0], network[1]);
                        if (equalNetworks(n.getFirstPrimitiveTasks(), n.getFirstAbstractTasks(), network[0], network[1])) {
                            this.hits++;
                            this.totaltime += (System.currentTimeMillis() - time);
                            return true;
                        }
                    }
                    // no match
                    List<ProgressionPlanStep>[] network = new List[2];
                    network[0] = n.getFirstPrimitiveTasks();
                    network[1] = n.getFirstAbstractTasks();
                    networks.add(network);
                    this.totaltime += (System.currentTimeMillis() - time);
                    return false;
                }
            }
        }
    }


    private boolean equalNetworks(List<ProgressionPlanStep> primA, List<ProgressionPlanStep> absA, List<ProgressionPlanStep> primB, List<ProgressionPlanStep> absB) {
        HashMap<ProgressionPlanStep, ProgressionPlanStep> A2B = new HashMap<>();
        HashMap<ProgressionPlanStep, ProgressionPlanStep> B2A = new HashMap<>();
        if ((primA.size() != primB.size()) || (absA.size() != absB.size()))
            return false;

        // extract nodes from A that need to be matched
        ProgressionPlanStep[] primNodes = new ProgressionPlanStep[primA.size()];
        ProgressionPlanStep[] absNodes = new ProgressionPlanStep[absA.size()];
        Iterator<ProgressionPlanStep> aPrimIter = primA.iterator();
        for (int i = 0; i < primA.size(); i++) {
            primNodes[i] = aPrimIter.next();
        }
        Iterator<ProgressionPlanStep> aAbsIter = absA.iterator();
        for (int i = 0; i < absA.size(); i++) {
            absNodes[i] = aAbsIter.next();
        }

        // prepare iterators and get first vals
        Iterator<ProgressionPlanStep>[] primVals = new Iterator[primA.size()];
        Iterator<ProgressionPlanStep>[] absVals = new Iterator[absA.size()];
        if (!initMappings(A2B, B2A, primNodes, primB, primVals)
                || !initMappings(A2B, B2A, absNodes, absB, absVals))
            return false; // there is no value for some node

        // create combinations
        boolean primHasNext = true;
        boolean absHasNext = true;

        while (primHasNext) {
            while (absHasNext) {
                Set<ProgressionPlanStep> allA = new HashSet<>();
                allA.addAll(primA);
                allA.addAll(absA);

                if (checkRestOfNetwork(allA, A2B, B2A))
                    return true;
                absHasNext = getNextMapping(absNodes, absB, null, absVals, A2B, B2A);
            }
            primHasNext = getNextMapping(primNodes, primB, null, primVals, A2B, B2A);
            if (primHasNext) {
                absHasNext = initMappings(A2B, B2A, absNodes, absB, absVals);
            }
        }

        return false;
    }

    /**
     * Creates iterators for each value and an initial mapping.
     *
     * @param mapA2B   [in/out] mapping from A to B
     * @param mapB2A   [in/out] mapping from B to A
     * @param nodes    [in] contains the nodes from network A that need a match from graph B
     * @param valList  [in] nodes from network B (in the current layer) that are used as possible values
     * @param valIters [out] for every node in A, an iterator of possible values is included in this
     * @return returns whether an initial mapping was found
     */
    private boolean initMappings(HashMap<ProgressionPlanStep, ProgressionPlanStep> mapA2B, HashMap<ProgressionPlanStep, ProgressionPlanStep> mapB2A, ProgressionPlanStep[] nodes, List<ProgressionPlanStep> valList, Iterator<ProgressionPlanStep>[] valIters) {
        for (int i = 0; i < valIters.length; i++) {
            valIters[i] = valList.iterator();
            ProgressionPlanStep val = valIters[i].next();
            while (compatible(nodes[i], val) == cUnequal) { // todo: already set? (mapB2A.containsKey(val))
                if (!valIters[i].hasNext())
                    return false;
                val = valIters[i].next();
            }
            mapA2B.put(nodes[i], val);
            mapB2A.put(val, nodes[i]);
        }
        return true;
    }

    /**
     * Finds next compatible mapping.
     *
     * @param nodes    [in] nodes that have to be mapped
     * @param valList  [in] when iterators have to be created, this can either be done by using this val list or by using the val set (next param)
     * @param valSets  [in] when iterators have to be created, this can either be done by using this val set or by using the list set (last param)
     * @param valIters [in/out] iterators with possible values, might be reseted
     * @param mapA2B   [in/out] mapping from A to B
     * @param mapB2A   [in/out] mapping from B to A
     * @return
     */
    private boolean getNextMapping(ProgressionPlanStep[] nodes, List<ProgressionPlanStep> valList, Set<ProgressionPlanStep>[] valSets, Iterator<ProgressionPlanStep>[] valIters, HashMap<ProgressionPlanStep, ProgressionPlanStep> mapA2B, HashMap<ProgressionPlanStep, ProgressionPlanStep> mapB2A) {
        loopNextCombination:
        for (int i = valIters.length - 1; i >= 0; i--) {
            // remove old assignment
            mapB2A.remove(mapA2B.remove(nodes[i]));

            // find new one
            boolean precessNextI = false;
            boolean restarted = false;
            while (valIters[i].hasNext() || (!restarted)) {
                if (valIters[i].hasNext()) {
                    ProgressionPlanStep val = valIters[i].next();
                    if (compatible(nodes[i], val) != cUnequal) { // todo check if already set
                        mapA2B.put(nodes[i], val);
                        mapB2A.put(val, nodes[i]);
                        if (!restarted) {
                            // when a compatible value has been found without restart, the next i does not need to be processed
                            break loopNextCombination;
                        } else {
                            // when the value has been found after the restart, a new value for the next has to be found
                            precessNextI = true;
                        }
                    }
                } else
                    // the reset is done IN THE WHILE loop -> the first compatible is set after the loop
                    if (!restarted && (i > 0)) {
                        restarted = true; // need to restart only once
                        if (valList != null)
                            valIters[i] = valList.iterator();
                        else
                            valIters[i] = valSets[i].iterator();
                    }
            }
            if (!precessNextI) { // even after the restart, no new value has been found
                // revert all values
                for (int j = i + 1; j < valIters.length; j++) {
                    mapB2A.remove(mapA2B.remove(nodes[j]));
                }
                return false;
            }
        }
        return true;
    }

    private boolean checkRestOfNetwork(Set<ProgressionPlanStep> nodesA, HashMap<ProgressionPlanStep, ProgressionPlanStep> a2B, HashMap<ProgressionPlanStep, ProgressionPlanStep> b2A) {
        Set<ProgressionPlanStep> nextLayerNodes = new HashSet<>();

        ProgressionPlanStep[] thisLayerNodes = new ProgressionPlanStep[nodesA.size()];
        Set<ProgressionPlanStep>[] valSets = new Set[nodesA.size()];
        Iterator<ProgressionPlanStep>[] valIters = new Iterator[nodesA.size()];
        int nodeI = 0;

        // init iterators, find first match
        for (ProgressionPlanStep n : nodesA) {
            nextLayerNodes.addAll(n.successorList);
            Set<ProgressionPlanStep> valList = a2B.get(n).successorList;
            Iterator<ProgressionPlanStep> valIter = valList.iterator();

            boolean foundinitial = false;
            loopFindFirst:
            while (valIter.hasNext()) {
                ProgressionPlanStep aVal = valIter.next();
                if (compatible(n, aVal) != cUnequal) {
                    if ((a2B.containsKey(n)) && (!a2B.get(n).equals(aVal))) {
                        continue loopFindFirst;
                    }
                    foundinitial = true;
                    a2B.put(n, aVal);
                    b2A.put(aVal, n);
                    break loopFindFirst;
                }
            }
            if (!foundinitial) {
                for (int i = 0; i < nodeI; i++) { // revert current assignments
                    b2A.remove(a2B.remove(thisLayerNodes[i]));
                }
                return false;
            }
            thisLayerNodes[nodeI] = n;
            valSets[nodeI] = valList;
            valIters[nodeI] = valIter;
            nodeI++;
        }

        // step through matches, call recursion
        boolean hasNextMatch = true;
        while (hasNextMatch) {
            if (checkRestOfNetwork(nextLayerNodes, a2B, b2A)) {
                return true;
            }
            hasNextMatch = getNextMapping(thisLayerNodes, null, valSets, valIters, a2B, b2A);
        }

        return false;
    }

    private short compatible(ProgressionPlanStep some, ProgressionPlanStep other) {
        if (!some.getTask().equals(other.getTask())) {
            return cUnequal;
        } else if (!(some.successorList.size() == other.successorList.size()))
            return cUnequal;
        else {
            return cIdentical;
        }
    }

}
