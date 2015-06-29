package de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel;

import de.uniulm.ki.panda3.symbolic.logic.Constant;
import scala.collection.immutable.Seq;
import scala.collection.immutable.VectorBuilder;

import java.util.HashMap;
import java.util.Set;

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
    }

    public void addConst(String type, String constName) {
        VectorBuilder<Constant> constList = constsOfType.get(type);
        if (constList == null) {
            constList = new VectorBuilder<>();
            constsOfType.put(type, constList);
        }
        constList.$plus$eq(new Constant(constName));
    }

    public Set<String> allTypeNames = parentof.keySet();

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
}
