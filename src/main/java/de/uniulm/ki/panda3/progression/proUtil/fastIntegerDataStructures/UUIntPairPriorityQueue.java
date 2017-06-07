package de.uniulm.ki.panda3.progression.proUtil.fastIntegerDataStructures;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by dh on 15.05.17.
 */
public class UUIntPairPriorityQueue {
    int[][] pairs;
    int nextIndex;

    public UUIntPairPriorityQueue() {
        pairs = new int[2049][];
        nextIndex = 1;
        pairs[0] = new int[2];
        pairs[0][0] = Integer.MIN_VALUE;

        //assert (testMe());
    }

    private boolean testMe() {
        Random r = new Random(42);
        for (int i = 0; i < 100; i++) {
            int samples = r.nextInt(129 * 10);
            int[] reference = new int[samples];
            if (i == 0) {
                int val = r.nextInt(Integer.MAX_VALUE);
                for (int j = 0; j < samples; j++) {
                    this.add(val, 0);
                    reference[j] = val;
                }
            } else {
                for (int j = 0; j < samples; j++) {
                    int val = r.nextInt(Integer.MAX_VALUE);
                    this.add(val, 0);
                    reference[j] = val;
                }
            }

            assert this.size() == samples;
            Arrays.sort(reference);
            for (int j = 0; j < samples; j++) {
                int min = this.minPair()[0];
                assert (min == reference[j]);
            }
            assert this.size() == 0;
        }

        return true;
    }

    public boolean isEmpty() {
        return nextIndex == 1;
    }

    public int size() {
        return (nextIndex - 1);
    }

    public void add(int sortBy, int someInt) {
        int[] pair = new int[2];
        pair[0] = sortBy;
        pair[1] = someInt;
        this.add(pair);
    }

    public void add(int[] pair) {
        if (nextIndex == pairs.length)
            pairs = Arrays.copyOf(pairs, (pairs.length - 1) * 2 + 1);

        pairs[nextIndex] = pair;

        int current = nextIndex;
        while (pairs[current][0] < pairs[current / 2][0]) {
            int[] swap = pairs[current];
            pairs[current] = pairs[current / 2];
            pairs[current / 2] = swap;
            current /= 2;
        }
        nextIndex++;
    }

    public int[] minPair() {
        int[] res = pairs[1];
        pairs[1] = pairs[--nextIndex];

        int current = 1;
        boolean swapped = true;
        while (swapped) {
            int child = current * 2;
            if (child >= nextIndex)
                break;

            if ((current * 2 + 1 < nextIndex) && (pairs[current * 2 + 1][0] < pairs[current * 2][0]))
                child++;
            if (pairs[child][0] < pairs[current][0]) {
                swapped = true;
                int[] swap = pairs[current];
                pairs[current] = pairs[child];
                pairs[child] = swap;
                current = child;
            } else swapped = false;
        }
        return res;
    }
}
