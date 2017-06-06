package de.uniulm.ki.panda3.progression.TDGReachabilityAnalysis;

import java.util.BitSet;

/**
 * Created by dh on 06.06.17.
 */
public interface IActionReachability {
    BitSet getReachableActions(int task);

}
