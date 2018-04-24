package de.uniulm.ki.panda3.util.shopReader.internalModel;

import java.util.List;

/**
 * Created by dh on 11.08.17.
 */
public class shOperator {
    public final String[] name;
    public final List<String[]> pre;
    public final List<String[]> add;
    public final List<String[]> del;

    public shOperator(String[] name, List<String[]> pre, List<String[]> add, List<String[]> del) {
        this.name = name;
        this.pre = pre;
        this.add = add;
        this.del = del;
    }
}
