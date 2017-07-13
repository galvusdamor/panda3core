package UUBenchmarksets.fromHTN.entertainment.problemGen;

/**
 * Created by dh on 12.07.17.
 */
public class DeviceFactory {
    /*
     * DVD Player
     */
    public static heDevice getDVDPlayerWithScart() {
        heDevice res = new heDevice();
        res.deviceType = "simple-dvd-";
        res.connections.add(heDevice.connectionType.Scart);
        res.isPort.add(true);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(true);
        return res;
    }

    /*
     * TV
     */
    static public heDevice getTVwithCinch() {
        heDevice tv = new heDevice();
        tv.deviceType = "simple-tv-";
        tv.connections.add(heDevice.connectionType.Cinch);
        tv.connections.add(heDevice.connectionType.Cinch);
        tv.isPort.add(true);
        tv.isPort.add(true);
        tv.inConn.add(true);
        tv.inConn.add(true);
        tv.outConn.add(false);
        tv.outConn.add(false);
        tv.audioConn.add(false);
        tv.audioConn.add(true);
        tv.videoConn.add(true);
        tv.videoConn.add(false);
        return tv;
    }

    static public heDevice getTVwithScart() {
        heDevice tv = new heDevice();
        tv.deviceType = "simple-tv-";
        tv.connections.add(heDevice.connectionType.Scart);
        tv.isPort.add(true);
        tv.inConn.add(true);
        tv.outConn.add(false);
        tv.audioConn.add(true);
        tv.videoConn.add(true);
        return tv;
    }

    /*
     * Scart Cinch Adapter
     */
    public static heDevice getScartPlugToCinchPortAdapter() {
        heDevice res = new heDevice();
        res.deviceType = "scart-to-cinch-";

        //res.ports.add(new heScartPort());
        res.connections.add(heDevice.connectionType.Scart);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.Cinch);
        res.isPort.add(true);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(false);

        res.connections.add(heDevice.connectionType.Cinch);
        res.isPort.add(true);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(false);
        res.videoConn.add(true);
        return res;
    }

    public static heDevice getCinchPortToScartPlugAdapter() {
        heDevice res = new heDevice();
        res.deviceType = "cinch-to-scart-";

        res.connections.add(heDevice.connectionType.Scart);
        res.isPort.add(false);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.Cinch);
        res.isPort.add(true);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(false);

        res.connections.add(heDevice.connectionType.Cinch);
        res.isPort.add(true);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(false);
        res.videoConn.add(true);

        return res;
    }

    public static heDevice getScartToCinchCable(){
        heDevice res = new heDevice();
        res.deviceType = "scart-to-cinch-cable-";

        res.connections.add(heDevice.connectionType.Scart);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.Cinch);
        res.isPort.add(false);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(false);

        res.connections.add(heDevice.connectionType.Cinch);
        res.isPort.add(false);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(false);
        res.videoConn.add(true);
        return res;
    }

    /*
     * Cables
     */
    public static heDevice getCinchCable() {
        heDevice res = new heDevice();
        res.deviceType = "cinch-cable-";

        res.connections.add(heDevice.connectionType.Cinch);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.Cinch);
        res.isPort.add(false);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(true);

        return res;
    }

    public static heDevice getScartCable() {
        heDevice res = new heDevice();
        res.deviceType = "scart-cable-";

        res.connections.add(heDevice.connectionType.Scart);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.Scart);
        res.isPort.add(false);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(true);

        return res;
    }
}
