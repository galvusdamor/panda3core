package UUBenchmarksets.fromHTN.entertainment.problemGen.heDevices;

import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.hePort;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heScartPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 19.05.17.
 */
public class heDvdPlayer extends heDevice {
    private heDvdPlayer() {
    }

    public static heDevice getSimpleDVDPlayer() {
        heDvdPlayer res = new heDvdPlayer();
        res.deviceType = "simple-dvd-";
        res.ports.add(new heScartPort());
        res.inPort.add(false);
        res.outPort.add(true);
        res.audioPort.add(true);
        res.videoPort.add(true);
        return res;
    }
}
