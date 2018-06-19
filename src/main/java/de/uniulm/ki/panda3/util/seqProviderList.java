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
     *
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

    public java.util.Iterator<T> getIterator() {
        return list.iterator();
    }

    public void add(java.util.Iterator<T> iterator) {
        while (iterator.hasNext())
            list.add(iterator.next());
    }
}
