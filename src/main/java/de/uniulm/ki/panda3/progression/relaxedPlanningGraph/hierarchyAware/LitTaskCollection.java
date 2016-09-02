package de.uniulm.ki.panda3.progression.relaxedPlanningGraph.hierarchyAware;

import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.logic.Constant;
import de.uniulm.ki.panda3.symbolic.logic.Variable;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import scala.collection.Iterator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dh on 24.08.16.
 */
public class LitTaskCollection<T> {
    private HashMap<T, Object> reachableTaskParamLists;
    private int size;

    public LitTaskCollection() {
        this.reachableTaskParamLists = new HashMap<T, Object>();
    }

    public boolean containsNadd(T firstKey, Iterator<Variable> vars, Map<Variable, Constant> grounding) {
        boolean found = true;
        HashMap<Constant, Object> map;
        if (this.reachableTaskParamLists.containsKey(firstKey)) {
            map = (HashMap<Constant, Object>) this.reachableTaskParamLists.get(firstKey);
        } else {
            found = false;
            map = new HashMap<Constant, Object>();
            this.reachableTaskParamLists.put(firstKey, map);
        }

        while (vars.hasNext()) {
            Constant c = grounding.get(vars.next());
            if ((!found) || (!map.containsKey(c))) {
                found = false;
                HashMap<Constant, Object> oldMap = map;
                map = new HashMap<Constant, Object>();
                oldMap.put(c, map);
            } else {
                map = (HashMap<Constant, Object>) map.get(c);
            }
        }
        if (!found)
            this.size++;
        return found;
    }

    public int size() {
        return size;
    }
}
