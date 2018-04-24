package de.uniulm.ki.panda3.progression.htn.search.fringe;

import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;

import java.util.PriorityQueue;

/**
 * Created by dh on 26.07.17.
 */
public class QueueBasedFringe<T> implements IFringe<T> {
    PriorityQueue<T> queue = new PriorityQueue<>();

    @Override
    public T poll() {
        return queue.poll();
    }

    @Override
    public void add(T node, boolean prefered) {
        queue.add(node);
    }

    @Override
    public void add(T node) {
        queue.add(node);
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public int size() {
        return queue.size();
    }
}
