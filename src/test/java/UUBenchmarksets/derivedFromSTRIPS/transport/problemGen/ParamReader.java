package UUBenchmarksets.derivedFromSTRIPS.transport.problemGen;

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

        }
    }
}
