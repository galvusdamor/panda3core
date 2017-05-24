package UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 21.05.17.
 */
public abstract class heConnection {
    static List<String[]> compabile = new ArrayList<>();
    public String typeStr;

    static {
        String[] tuple = new String[2];
        tuple[0] = new heScartPort().typeStr;
        tuple[1] = new heScartPlug().typeStr;
        compabile.add(tuple);
        tuple = new String[2];
        tuple[0] = new heCinchPort().typeStr;
        tuple[1] = new heCinchPlug().typeStr;
        compabile.add(tuple);
    }

    public static boolean compatible(String port, String plug) {
        for (String[] c : compabile)
            if (port.equals(c[0]) && plug.equals(c[1]))
                return true;
        return false;
    }
}
