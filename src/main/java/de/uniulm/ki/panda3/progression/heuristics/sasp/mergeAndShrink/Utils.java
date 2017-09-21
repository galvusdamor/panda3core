package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import scala.ScalaReflectionException;
import scala.Tuple3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by biederma on 07.09.2017.
 */
public final class Utils {

    private Utils(){

    }

    public static boolean contains(int[] array, int number){

        for (int i=0; i<array.length; i++){
            if (array[i] == number) return true;
        }

        return false;
    }

    public static boolean containsEdge(ArrayList<Tuple3<Integer,Integer,Integer>> edges, Tuple3 edge){

        boolean contained = false;

        for (Tuple3 e : edges){

            if (e._1().equals(edge._1()) && e._2().equals(edge._2()) && e._3().equals(edge._3())){
                contained = true;
                break;
            }

        }


        return contained;

    }


    public static String eliminateDoubleRows(String inputString){

        String[] data = inputString.split("\n");

        String output = "";

        ArrayList<String > lines = new ArrayList<>();


        for (int i=0; i<data.length; i++) {
            String s = data[i];
            if(s.contains("->")) {
                if (!lines.contains(s)) {
                    lines.add(s);
                    output += "\n" + s;
                }
            }else{
                lines.add(s);
                output += "\n" + s;
            }
        }

        return output;
    }



}
