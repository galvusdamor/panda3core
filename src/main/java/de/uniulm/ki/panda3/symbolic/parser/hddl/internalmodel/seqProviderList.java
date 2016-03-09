package de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel;

import scala.collection.Seq;
import scala.collection.immutable.VectorBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhoeller on 05.03.16.
 */
public class seqProviderList<T> {
    private final ArrayList<T> list;

    public seqProviderList() {
        this.list = new ArrayList<T>();
    }

    public void add(T t) {
        list.add(t);
    }

    public T get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public Seq<T> result() {
        VectorBuilder<T> res = new VectorBuilder<T>();
        for (T o : this.list) {
            res.$plus$eq(o);
        }
        return res.result();
    }
}
