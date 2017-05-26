package UUBenchmarksets.fromHTN.entertainment.problemGen.heCables;

import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heCinchPlug;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heScartPlug;

import java.util.ArrayList;

/**
 * Created by dh on 26.05.17.
 */
public class heScartToCinchCable extends heCable{
    public heScartToCinchCable(){
        typeStr = "scart-to-cinch-cable-";
        plugs = new ArrayList<>();
        //audioCable = true;
        //videoCable = true;
        plugs.add(new heScartPlug());
        this.inPlug.add(true);
        this.outPlug.add(true);
        this.videoPlug.add(true);
        this.audioPlug.add(true);

        plugs.add(new heCinchPlug());
        this.inPlug.add(true);
        this.outPlug.add(true);
        this.videoPlug.add(false);
        this.audioPlug.add(true);

        plugs.add(new heCinchPlug());
        this.inPlug.add(true);
        this.outPlug.add(true);
        this.videoPlug.add(true);
        this.audioPlug.add(false);
    }
}
