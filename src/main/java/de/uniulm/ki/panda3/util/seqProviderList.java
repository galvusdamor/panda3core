package de.uniulm.ki.panda3.util;

import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.Set;
import scala.collection.immutable.VectorBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhoeller on 05.03.16.
 */
public class seqProviderList<T> {
    private final ArrayList<T> list;

    public List<T> getList() {
        return list;
    }

    public seqProviderList() {
        this.list = new ArrayList<T>();
    }

    public seqProviderList(Seq<T> someSeq) {
        this.list = new ArrayList<T>();
        this.add(someSeq);
    }

    public seqProviderList(Set<T> vars) {
        this.list = new ArrayList<T>();
        this.add(vars);
    }

    public void add(T t) {
        list.add(t);
    }

    public void add(Seq<T> someSeq) {
        for (int i = 0; i < someSeq.size(); i++) {
            list.add(someSeq.apply(i));
        }
    }

    public void add(Set<T> someSet) {
        Iterator<T> iter = someSet.iterator();
        while (iter.hasNext()) {
            this.add(iter.next());
        }
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

    /**
     * returns the elements starting by the given index
     * @param startIndex the first element that is returned
     * @return
     */
    public Seq<T> result(int startIndex) {
        VectorBuilder<T> res = new VectorBuilder<T>();
        for (int i = 0; i < list.size(); i++) {
            T o = this.list.get(i);
            res.$plus$eq(o);
        }
        return res.result();
    }

    public Set<T> resultSet() {
        Seq<T> res = this.result();
        return res.toSet();
    }

    public boolean contains(T t) {
        return this.contains(t);
    }
}
