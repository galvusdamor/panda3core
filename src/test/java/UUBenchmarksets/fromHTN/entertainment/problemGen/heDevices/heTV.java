package UUBenchmarksets.fromHTN.entertainment.problemGen.heDevices;

import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heCinchPort;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.hePort;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heScartPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 19.05.17.
 */
public class heTV extends heDevice {

    public heTV() {
    }

    static public heTV getTVwithCinch(){
        heTV tv = new heTV();
        tv.deviceType = "simple-tv-";
        tv.ports.add(new heCinchPort());
        tv.ports.add(new heCinchPort());
        tv.inPort.add(true);
        tv.inPort.add(true);
        tv.outPort.add(false);
        tv.outPort.add(false);
        tv.audioPort.add(false);
        tv.audioPort.add(true);
        tv.videoPort.add(true);
        tv.videoPort.add(false);
        return tv;
    }

    static public heTV getTVwithScart(){
        heTV tv = new heTV();
        tv.deviceType = "simple-tv-";
        tv.ports.add(new heScartPort());
        tv.inPort.add(true);
        tv.outPort.add(false);
        tv.audioPort.add(true);
        tv.videoPort.add(true);
        return tv;
    }
}
