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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Created by dh on 02.09.16.
 */
public class switchTLT {
    public static void main(String[] str) throws Exception {
        if (str.length == 0)
            System.out.println("Please specify the file to translate");
        String task = "tlt";
        if (str.length > 1)
            task = str[1];
        BufferedReader br = new BufferedReader(new FileReader(str[0]));
        int addPos = str[0].lastIndexOf(".");
        String newName = str[0].substring(0, addPos) + "-tlt" + str[0].substring(addPos);
        BufferedWriter bw = new BufferedWriter(new FileWriter(newName));
        while (br.ready()) {
            String line = br.readLine();
            if (line.trim().startsWith("(:htn")) {
                bw.write(line + "\n");
                bw.write(";;" + br.readLine() + "\n");
                do {
                    line = br.readLine();
                    bw.write(";;" + line + "\n");
                } while (line.trim().startsWith("(task"));
                bw.write("                :subtasks (and (" + task + ")\n");
            }
            bw.write(line + "\n");
        }
        br.close();
        bw.close();
    }
}
