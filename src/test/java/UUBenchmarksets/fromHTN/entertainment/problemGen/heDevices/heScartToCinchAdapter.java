package UUBenchmarksets.fromHTN.entertainment.problemGen.heDevices;

import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heCinchPort;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heScartPort;

/**
 * Created by dh on 19.05.17.
 */
public class heScartToCinchAdapter extends heDevice {

    public static heScartToCinchAdapter getScartToCinchAdapter() {
        heScartToCinchAdapter res = new heScartToCinchAdapter();
        res.deviceType = "scart-to-cinch-";

        //res.ports.add(new heScartPort());
        res.ports.add(new heScartPort());
        res.inPort.add(true);
        res.outPort.add(false);
        res.audioPort.add(true);
        res.videoPort.add(true);

        res.ports.add(new heCinchPort());
        res.inPort.add(false);
        res.outPort.add(true);
        res.audioPort.add(true);
        res.videoPort.add(false);

        res.ports.add(new heCinchPort());
        res.inPort.add(false);
        res.outPort.add(true);
        res.audioPort.add(false);
        res.videoPort.add(true);
        return res;
    }
    public static heScartToCinchAdapter getCinchToScartAdapter() {
        heScartToCinchAdapter res = new heScartToCinchAdapter();
        res.deviceType = "cinch-to-scart-";

        res.ports.add(new heScartPort());
        res.inPort.add(false);
        res.outPort.add(true);
        res.audioPort.add(true);
        res.videoPort.add(true);

        res.ports.add(new heCinchPort());
        res.inPort.add(true);
        res.outPort.add(false);
        res.audioPort.add(true);
        res.videoPort.add(false);

        res.ports.add(new heCinchPort());
        res.inPort.add(true);
        res.outPort.add(false);
        res.audioPort.add(false);
        res.videoPort.add(true);

        return res;
    }
}
