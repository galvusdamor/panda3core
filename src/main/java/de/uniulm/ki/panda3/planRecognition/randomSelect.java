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
