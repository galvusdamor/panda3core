package de.uniulm.ki.panda3.progression.proUtil;

import de.uniulm.ki.panda3.progression.htn.operators.method;

import java.util.List;

/**
 * Created by dhoeller on 27.07.16.
 */
public class dotNode implements Comparable<dotNode> {

    static int getID = 0;

    public int interalID = getID++;
    public final int id;
    public final String visibleName;
    private String style = "";

    public dotNode(int identifier, String visibleName) {
        this.id = identifier;
        this.visibleName = visibleName;
    }

    public dotNode(int identifier, String visibleName, String style) {
        this(identifier, visibleName);
        this.style = style;
    }

    @Override
    public int compareTo(dotNode other) {
        return (this.id - other.id);
    }
}
