package de.uniulm.ki.panda3.progression.htn.search.fringe;

import java.util.PriorityQueue;

/**
 * Created by dh on 26.07.17.
 */
public class AlternatingFringe<T> implements IFringe<T> {
    PriorityQueue<T> std = new PriorityQueue<>();
    PriorityQueue<T> pref = new PriorityQueue<>();

    boolean prefRound = false;

    @Override
    public T poll() {
        if (pref.isEmpty())
            return std.poll();
        if (std.isEmpty())
            return pref.poll();
        prefRound = !prefRound;
        if (prefRound)
            return pref.poll();
        else return std.poll();
    }

    @Override
    public void add(T node, boolean prefered) {
        if (prefered)
            pref.add(node);
        else
            std.add(node);
    }

    @Override
    public void add(T node) {
        std.add(node);
    }


    @Override
    public boolean isEmpty() {
        return std.isEmpty() && pref.isEmpty();
    }

    @Override
    public void clear() {
        std.clear();
        pref.clear();
    }

    @Override
    public int size() {
        return std.size() + pref.size();
    }
}
