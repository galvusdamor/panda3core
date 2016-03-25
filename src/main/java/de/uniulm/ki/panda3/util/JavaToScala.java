package de.uniulm.ki.panda3.util;

import scala.collection.JavaConversions;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.WrappedArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhoeller on 22.04.15.
 */
public class JavaToScala {
    public static <T> Seq<T> toScalaSeq(List<T> javaList) {
        return JavaConversions.asScalaBuffer(javaList);

    }

    public static <T> scala.collection.immutable.List<T> toScalaList(List<T> javaList) {
        return JavaConversions.asScalaBuffer(javaList).toList();
    }

    public static <T> Seq<T> concatScalaSeqs(Seq<T> scalaSeq1, Seq<T> scalaSeq2) {
        List<T> javaList1 = toJavaList(scalaSeq1);
        List<T> javaList2 = toJavaList(scalaSeq2);
        javaList1 = concatJavaLists(javaList1, javaList2);
        return JavaToScala.toScalaSeq(javaList1);
    }

    public static <T> Seq<T> concatJavaListToScalaSeq(Seq<T> scalaSeq1, List<T> javaList2) {
        List<T> javaList1 = toJavaList(scalaSeq1);
        javaList1 = concatJavaLists(javaList1, javaList2);
        return JavaToScala.toScalaSeq(javaList1);
    }

    public static <T> Seq<T> toScalaSeq(T javaObject) {
        ArrayList<T> javaList = new ArrayList<T>();
        javaList.add(javaObject);
        return JavaToScala.toScalaSeq(javaList);
    }

    public static <T> java.util.List<T> toJavaList(Seq<T> scalaSeq) {
        return JavaConversions.seqAsJavaList(scalaSeq);

    }

    public static <K, V> scala.collection.immutable.Map<K, V> toScalaMap(java.util.Map<K, V> javaMap) {
        return JavaConverters.mapAsScalaMapConverter(javaMap).asScala().toMap( scala.Predef$.MODULE$.<scala.Tuple2<K, V>>conforms());
    }

    public static <T> ArrayList<T> concatJavaLists(List<T> javaList1, List<T> javaList2) {
        ArrayList<T> tempList = new ArrayList<T>();
        for (int a = 0; a < javaList1.size(); a++) {
            tempList.add(javaList1.get(a));
        }
        for (int a = 0; a < javaList2.size(); a++) {
            tempList.add(javaList2.get(a));
        }
        return tempList;
    }

    public static <T> Seq<T> nil() {
        return JavaToScala.toScalaSeq(new ArrayList<T>());
    }
}