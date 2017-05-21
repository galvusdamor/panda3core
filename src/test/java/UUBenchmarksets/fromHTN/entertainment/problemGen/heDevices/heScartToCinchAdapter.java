package UUBenchmarksets.fromHTN.entertainment.problemGen.heDevices;

import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heCinchPort;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heScartPort;

/**
 * Created by dh on 19.05.17.
 */
public class heScartToCinchAdapter extends heDevice {

    public heScartToCinchAdapter() {
        deviceType = "scart-to-cinch-";
        ports.add(new heScartPort());
        ports.add(new heCinchPort());
        ports.add(new heCinchPort());
        inPort.add(true);
        inPort.add(false);
        inPort.add(false);
        outPort.add(false);
        outPort.add(true);
        outPort.add(true);
        audioPort.add(true);
        audioPort.add(true);
        audioPort.add(false);
        videoPort.add(true);
        videoPort.add(false);
        videoPort.add(true);
    }
}
