package de.uniulm.ki.panda3.planRecognition;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by dh on 04.11.17.
 */
public class randomSelect {
    public static void main(String[] in) {
        Random ran = new Random(42);
        Set<Integer> alreadyIn = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            int val = ran.nextInt(1080);
            while (alreadyIn.contains(val))
                val = ran.nextInt(1080);
            alreadyIn.add(val);
            System.out.print("*" + myFormat(val) + "*");
            System.out.print(" ");
        }
        System.out.println();
    }

    private static String myFormat(int i) {
        String pref = "";
        while ((pref + i).length() < 4)
            pref += "0";
        return pref + i;
    }
}
