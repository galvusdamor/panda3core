package de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel;

import de.uniulm.ki.panda3.symbolic.logic.Constant;
import scala.collection.immutable.Seq;
import scala.collection.immutable.VectorBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dhoeller on 15.04.15.
 */
public class internalSortsAndConsts {

    HashMap<String, String> parentof = new HashMap<>();
    HashMap<String, VectorBuilder<Constant>> constsOfType = new HashMap<>();

    public final int cOK = 0;
    public final int cMinorError = -1;

    public void addParent(String child, String parent) {
        parentof.put(child, parent);
        if (parentof.get(parent) == null)
            parentof.put(parent, null);
    }

    public void addConst(String type, String constName) {
        VectorBuilder<Constant> constList = constsOfType.get(type);
        if (constList == null) {
            constList = new VectorBuilder<>();
            constsOfType.put(type, constList);
        }
        constList.$plus$eq(new Constant(constName));
    }

    /*
     * The following method returns all types in such an order that the parents of a type are necessarily ordered
     * after all its types (hope that is possible in any case ;-) )
     */
    public List<String> allTypeNamesInRightOrder() {
        List<String> alltypes = new ArrayList<>();
        for (String type : parentof.keySet()) {
            alltypes.add(type);
        }
        List<String> res = new LinkedList<>();

        while (!alltypes.isEmpty()) {
            for (String type : alltypes) {
                boolean hasNewChild = false;
                for (String other : parentof.keySet()) {
                    String parentOfOther = parentof.get(other);
                    if (parentOfOther == null) {// root of type hierarchy
                        continue;
                    }
                    if (parentOfOther.equals(type) && !res.contains(other)) {
                        hasNewChild = true;
                        break;
                    }
                }
                if (!hasNewChild) {
                    res.add(type);
                    alltypes.remove(type);
                    break;
                }
            }
        }
        return res;
    }

    public Seq<Constant> getConsts(String type) {
        VectorBuilder<Constant> res = constsOfType.get(type);
        if (res == null) {
            res = new VectorBuilder<>();
        }
        return res.result();
    }

    public int checkConsistency() {
        int result = cOK;
        for (String type : constsOfType.keySet()) {
            if (parentof.get(type) == null) {
                System.out.println("Found constant of a type that is not given in type hierarchy: " + type);
                // todo: what to do?

                result = cMinorError;
            }
        }
        // todo: check if const-names are unique
        // todo: check if any type has no consts and prune (better later)
        return result;
    }

    public List<String> getSubSorts(String type) {
        List<String> res = new LinkedList<>();
        for (String subtype : parentof.keySet()) {
            String parent = parentof.get(subtype);
            if ((parent != null) && (parent.equals(type))) {
                res.add(subtype);
            }
        }
        return res;
    }
}
