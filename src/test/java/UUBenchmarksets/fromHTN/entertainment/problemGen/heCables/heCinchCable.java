package UUBenchmarksets.fromHTN.entertainment.problemGen.heCables;

import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heCinchPlug;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heScartPlug;

import java.util.ArrayList;

/**
 * Created by dh on 19.05.17.
 */
public class heCinchCable extends heCable {
    public heCinchCable(){
        typeStr = "cinch-cable-";
        plugs = new ArrayList<>();
        audioCable = true;
        videoCable = true;
        plugs.add(new heCinchPlug());
        plugs.add(new heCinchPlug());
    }
}
