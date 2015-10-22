package de.uniulm.ki.panda3.symbolic.parser.hddl.internalmodel;

import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.logic.Variable;
import scala.collection.Seq;

/**
 * Created by dhoeller on 29.06.15.
 */
public class parserUtil {
    public static Variable getVarByName(Seq<Variable> parameters, String name) {
        Variable methodVar = null;
        for (int j = 0; j < parameters.size(); j++) {
            if (parameters.apply(j).name().equals(name)) {
                methodVar = parameters.apply(j);
                break;
            }
        }
        return methodVar;
    }

    public static Task taskByName(String taskname, Seq<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.apply(i);
            if (t.name().equals(taskname)) {
                return t;
            }
        }
        return null;
    }

}
