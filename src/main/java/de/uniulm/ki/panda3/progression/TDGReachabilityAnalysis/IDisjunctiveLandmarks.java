package de.uniulm.ki.panda3.progression.TDGReachabilityAnalysis;

import java.util.BitSet;

/**
 * Created by dh on 07.06.17.
 */
public interface IDisjunctiveLandmarks {
    BitSet[] getDisjLandmarks(int task);
}
