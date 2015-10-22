package de.uniulm.ki.panda3.util;

import scala.collection.JavaConversions;
import scala.collection.Seq;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.WrappedArray;

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

}
