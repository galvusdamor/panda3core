package de.uniulm.ki.panda3.progression.relaxedPlanningGraph.hierarchyAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dh on 12.10.16.
 */
public class reachablibityNode {
    public static void main(String[] str) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put("test", 1);
        Integer i = map.get("test");
        i++;
        System.out.println("test has val of " + map.get("test"));
    }
}