package de.uniulm.ki.panda3.progression.bottomUpTDG;

import de.uniulm.ki.panda3.symbolic.domain.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhoeller on 08.07.16.
 */
public class taskSymbolNode {
    private final Task task;
    private List<taskSymbolNode> children = new ArrayList<>();
    private boolean isRoot = false;

    public taskSymbolNode() {
        this.task = null;
        this.isRoot = true;
    }

    public taskSymbolNode(Task task) {
        this.task = task;
    }

    public void addChild(taskSymbolNode child) {
        this.children.add(child);
    }
}
