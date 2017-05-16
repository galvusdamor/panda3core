package de.uniulm.ki.panda3.progression.htn.operators;

import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral;
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask;

import java.util.*;

/**
 * Created by dhoeller on 23.07.16.
 */
public class operators {
    /*
    public static HashMap<GroundLiteral, Integer> LiteralToIndex;
    public static GroundLiteral[] IndexToLiteral;

    public static HashMap<GroundTask, Integer> ActionToIndex;
    public static GroundTask[] IndexToAction;


    // The following representation is used in state-transition.
    // Is is a standard Strips set representation.
    public static BitSet[] prec;
    public static BitSet[] add;
    public static BitSet[] del;

    public static int numActions;
    public static int numStateFeatures;

    // The following representation is used in rpg-calculation
    public static int[][] precList; // [0][1, 3] means that action 0 needs state-features 1 and 3 to be applicable
    public static int[][] addList;  // [1][2, 5] means that action 1 adds state-features 2 and 5

    public static BitSet goal;
    public static int[] goalList;

    public static Set<Integer> ShopPrecActions = new HashSet<>();

    // The method representation used in progression search
    public static HashMap<Task, HashMap<GroundTask, List<method>>> methods;

    public static void finalizeMethods() {
        for (HashMap<GroundTask, List<method>> x : operators.methods.values()) {
            for (List<method> y : x.values()) {
                for (method z : y) {
                    z.finalizeMethod();
                }
            }
        }
    }*/
}
