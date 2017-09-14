package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import scala.Tuple3;

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
}
