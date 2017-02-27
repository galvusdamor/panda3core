package de.uniulm.ki.panda3.progression.sasp;

/**
 * Created by dh on 24.02.17.
 */
public class testSASP {
    public static void main(String[] str) throws Exception {
        String s = "/home/dh/Schreibtisch/sasp/output.sas";
        SasPlusProblem sasp = new SasPlusProblem(s);
        System.out.println(sasp.toString());
    }
}
