package de.uniulm.ki.panda3.progression.htn.search.fringe;

/**
 * Created by dh on 26.07.17.
 */
public interface IFringe<T> {
    T poll();

    void add(T node, boolean prefered);

    void add(T node);

    boolean isEmpty();

    void clear();

    int size();
}
