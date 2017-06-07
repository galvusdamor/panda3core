package de.uniulm.ki.panda3.progression.heuristics.sasp;

import de.uniulm.ki.panda3.progression.proUtil.fastIntegerDataStructures.UUIntStack;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;

import java.util.BitSet;

/**
 * Created by dh on 15.05.17.
 */
public class hFF extends RelaxedTaskGraph {
    int[] emptyAchieverList;

    public hFF(SasPlusProblem p) {
        super(p);
        this.evalBestAchievers = true;
        this.trackPCF = false;
        this.earlyAbord = true;
        emptyAchieverList = new int[this.waitingForNodes.length];
        for (int i = 0; i < emptyAchieverList.length; i++)
            emptyAchieverList[i] = -1;
    }

    int[] achiever;

    @Override
    protected void evalAchiever(int nodeId, int bestAchiever) {
        assert bestAchiever >= 0;
        achiever[nodeId] = bestAchiever;
    }

    @Override
    public int calcHeu(BitSet s0, BitSet g) {
        achiever = emptyAchieverList.clone();

        super.calcHeu(s0, g);

        UUIntStack fringe = new UUIntStack();
        int f = g.nextSetBit(0);
        while (f >= 0) {
            fringe.push(f);
            f = g.nextSetBit(f + 1);
        }
        return calcFF(s0, fringe);
    }

    private int calcFF(BitSet s0, UUIntStack fringe) {
        int h = 0;
        BitSet done = new BitSet();
        while (!fringe.isEmpty()) {
            int f = fringe.pop();
            if (done.get(f) || s0.get(f) || waitingForNodes[f].length == 0)
                continue;
            if (isAndNode.get(f)) {
                h += this.costs[f];
                for (int n : waitingForNodes[f])
                    fringe.push(n);
            } else if (achiever[f] > -1)
                fringe.push(achiever[f]);
            done.set(f);
        }
        return h;
    }

    @Override
    public int calcHeu(int[] s0, int[] g) {
        achiever = emptyAchieverList.clone();
        super.calcHeu(s0, g);
        BitSet s0Set = new BitSet();
        for (int i : s0)
            s0Set.set(i);
        UUIntStack fringe = new UUIntStack();
        for (int f : g)
            fringe.push(f);
        return calcFF(s0Set, fringe);
    }

    @Override
    int eAND() {
        return 0;
    }

    @Override
    int eOR() {
        return Integer.MAX_VALUE;
    }

    @Override
    int combineAND(int x, int y) {
        return x + y;
    }

    @Override
    int combineOR(int x, int y) {
        return Integer.min(x, y);
    }
}
