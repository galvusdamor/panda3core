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
                bw.write("                :subtasks (and (mtlt)\n");
            }
            bw.write(line + "\n");
        }
        br.close();
        bw.close();
    }
}