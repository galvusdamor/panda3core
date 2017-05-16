package de.uniulm.ki.panda3.progression.sasp;

import de.uniulm.ki.panda3.progression.sasp.heuristics.RelaxedTaskGraph;
import de.uniulm.ki.panda3.progression.sasp.heuristics.hAdd;
import de.uniulm.ki.panda3.progression.sasp.heuristics.hLmCut;
import de.uniulm.ki.panda3.progression.sasp.heuristics.hMax;

/**
 * Created by dh on 24.02.17.
 */
public class testSASP {
    public static void main(String[] str) throws Exception {
        String s = "/home/dh/Schreibtisch/sasp/strips/output.sas";
        SasPlusProblem sasp = new SasPlusProblem(s);
        sasp.prepareEfficientRep();
        RelaxedTaskGraph rtg = new hLmCut(sasp);
        int val = rtg.calcHeu(sasp.s0List, sasp.gList);
        System.out.println("hVal: " + val);
    }
}
