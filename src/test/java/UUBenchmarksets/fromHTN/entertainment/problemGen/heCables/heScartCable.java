package UUBenchmarksets.fromHTN.entertainment.problemGen.heCables;

import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.hePlug;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heScartPlug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 19.05.17.
 */
public class heScartCable extends heCable {

    public heScartCable() {
        typeStr = "scart-cable-";
        plugs = new ArrayList<>();
        audioCable = true;
        videoCable = true;
        plugs.add(new heScartPlug());
        this.inPlug.add(true);
        this.outPlug.add(true);
        this.videoPlug.add(true);
        this.audioPlug.add(true);
        plugs.add(new heScartPlug());
        this.inPlug.add(true);
        this.outPlug.add(true);
        this.videoPlug.add(true);
        this.audioPlug.add(true);
    }
}
