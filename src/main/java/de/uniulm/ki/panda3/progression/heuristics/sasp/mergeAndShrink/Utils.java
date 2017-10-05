package de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink;

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import scala.ScalaReflectionException;
import scala.Tuple3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

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



    public static ArrayList<Integer> findContainedIDs(HashMap<Integer, ArrayList<Integer>> idMapping, int varIndex){

        ArrayList<Integer> containedIDs = new ArrayList<>();

        for (int i: idMapping.keySet()){
            if (idMapping.get(i).contains(varIndex)){
                containedIDs.add(i);
            }
        }

        return containedIDs;
    }


    public static ArrayList<Integer> dismissNotContainedIndexes(int[] allIndexes, ArrayList<Integer> containedIndexes){

        ArrayList<Integer> result = new ArrayList<>();

        for (int i : allIndexes){
            if (containedIndexes.contains(i)) result.add(i);
        }

        return result;
    }


    public static String getMultiIDString(SasPlusProblem p, int multiID, HashMap<Integer, ArrayList<Integer>> idMapping){

        String s =  multiID + ": \n";

        ArrayList<Integer> varIDs = idMapping.get(multiID);

        for (int i: varIDs){
            s += i + ": " +p.factStrs[i] +"\n";
        }

        return s;

    }


    public static Tuple3[] convertEdgeArrayListToTuple3(ArrayList<Tuple3<Integer,Integer,Integer>> edges){

        Tuple3[] edgeTuple = new Tuple3[edges.size()];
        for (int i=0; i<edges.size(); i++){
            edgeTuple[i] = edges.get(i);
        }

        return edgeTuple;

    }

    public static Integer[] convertNodeIDArrayListToArray(HashMap<Integer, ArrayList<Integer>> idMapping){

        Integer[] nodeIDS = idMapping.keySet().toArray(new Integer[idMapping.keySet().size()]);

        return nodeIDS;
    }

}
