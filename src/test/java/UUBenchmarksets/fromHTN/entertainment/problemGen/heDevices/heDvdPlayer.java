package UUBenchmarksets.fromHTN.entertainment.problemGen.heDevices;

import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.hePort;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heScartPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 19.05.17.
 */
public class heDvdPlayer extends heDevice {
    public heDvdPlayer() {
        deviceType = "simple-dvd-";
        ports.add(new heScartPort());
        inPort.add(false);
        outPort.add(true);
        audioPort.add(true);
        videoPort.add(true);
    }
}
