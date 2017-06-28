package de.uniulm.ki.panda3.problemGenerators.derivedFromSTRIPS.transport;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dh on 17.05.17.
 */
public class ParamReader {

    private final String[] names;
    private final String[] description;
    Map<String, Integer> params = new HashMap<>();

    public ParamReader(String[] names, String[] description, int[] defaults) {
        for (int i = 0; i < names.length; i++)
            params.put(names[i], defaults[i]);
        this.names = names;
        this.description = description;
    }

    public int get(String key) {
        return params.get(key);
    }

    public void read(String[] args) {
        boolean printDesc = false;
        for (int i = 0; i < args.length; i++) {
            String[] kv = args[i].split("=");
            assert (kv.length == 2);
            // check whether the names contain them
            boolean found = false;
            for (int k = 0; k < names.length; k++)
                if (names[k].equals(kv[0])) {
                    found = true;
                    params.put(kv[0], Integer.parseInt(kv[1]));
                }
            if (!found)
                System.out.println("Could not find parameter " + kv[0]);
        }
    }
}
