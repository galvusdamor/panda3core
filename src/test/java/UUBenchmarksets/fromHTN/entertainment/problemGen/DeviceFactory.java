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
        res.deviceType = "dvd-";
        res.connections.add(heDevice.connectionType.scart);
        res.isPort.add(true);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(true);
        return res;
    }

    public static heDevice getBRPlayerWithScart() {
        heDevice res = new heDevice();
        res.deviceType = "blu-ray-";
        res.connections.add(heDevice.connectionType.scart);
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
        tv.connections.add(heDevice.connectionType.cinch);
        tv.connections.add(heDevice.connectionType.cinch);
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
        tv.connections.add(heDevice.connectionType.scart);
        tv.isPort.add(true);
        tv.inConn.add(true);
        tv.outConn.add(false);
        tv.audioConn.add(true);
        tv.videoConn.add(true);
        return tv;
    }

    static public heDevice getTV1(int numScart) {
        heDevice tv = new heDevice();
        tv.deviceType = "tv1-";

        for (int i = 0; i < numScart; i++) {
            tv.connections.add(heDevice.connectionType.scart);
            tv.isPort.add(true);
            tv.inConn.add(true);
            tv.outConn.add(false);
            tv.audioConn.add(true);
            tv.videoConn.add(true);
        }

        tv.connections.add(heDevice.connectionType.cinch);
        tv.isPort.add(true);
        tv.inConn.add(false);
        tv.outConn.add(true);
        tv.audioConn.add(true);
        tv.videoConn.add(false);
        return tv;
    }

    static public heDevice getGameConsole() {
        heDevice gc = new heDevice();
        gc.deviceType = "game-console-";
        gc.connections.add(heDevice.connectionType.scart);
        gc.isPort.add(true);
        gc.inConn.add(false);
        gc.outConn.add(true);
        gc.audioConn.add(true);
        gc.videoConn.add(true);
        return gc;
    }

    static public heDevice getActiveSpeaker() {
        heDevice acSp = new heDevice();
        acSp.deviceType = "active-speaker-";
        for (int i = 0; i < 2; i++) {
            acSp.connections.add(heDevice.connectionType.cinch);
            acSp.isPort.add(true);
            acSp.inConn.add(true);
            acSp.outConn.add(false);
            acSp.audioConn.add(true);
            acSp.videoConn.add(false);
        }
        return acSp;
    }

    static public heDevice getAmplifier() {
        heDevice amp = new heDevice();
        amp.deviceType = "amplifier-";
        for (int i = 0; i < 4; i++) {
            amp.connections.add(heDevice.connectionType.cinch);
            amp.isPort.add(true);
            amp.inConn.add(true);
            amp.outConn.add(false);
            amp.audioConn.add(true);
            amp.videoConn.add(false);
        }
        for (int i = 0; i < 4; i++) {
            amp.connections.add(heDevice.connectionType.speakerWire);
            amp.isPort.add(true);
            amp.inConn.add(false);
            amp.outConn.add(true);
            amp.audioConn.add(true);
            amp.videoConn.add(false);
        }
        return amp;
    }


    static public heDevice getBox() {
        heDevice box = new heDevice();
        box.deviceType = "box-";
        box.connections.add(heDevice.connectionType.speakerWire);
        box.isPort.add(true);
        box.inConn.add(true);
        box.outConn.add(false);
        box.audioConn.add(true);
        box.videoConn.add(false);
        return box;
    }

    /*
     * Scart Cinch Adapter
     */
    public static heDevice getScartPlugToCinchPortAdapter() {
        heDevice res = new heDevice();
        res.deviceType = "scart-to-cinch-";

        //res.ports.add(new heScartPort());
        res.connections.add(heDevice.connectionType.scart);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.cinch);
        res.isPort.add(true);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(false);

        res.connections.add(heDevice.connectionType.cinch);
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

        res.connections.add(heDevice.connectionType.scart);
        res.isPort.add(false);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.cinch);
        res.isPort.add(true);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(false);

        res.connections.add(heDevice.connectionType.cinch);
        res.isPort.add(true);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(false);
        res.videoConn.add(true);

        return res;
    }

    public static heDevice getScartToCinchCable() {
        heDevice res = new heDevice();
        res.deviceType = "scart-to-cinch-cable-";

        res.connections.add(heDevice.connectionType.scart);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.cinch);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(false);

        res.connections.add(heDevice.connectionType.cinch);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(true);
        res.audioConn.add(false);
        res.videoConn.add(true);
        return res;
    }

    public static heDevice getMultiScartInput() {
        heDevice res = new heDevice();
        res.deviceType = "multi-scart-";

        res.connections.add(heDevice.connectionType.scart);
        res.isPort.add(true);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(true);

        for (int i = 0; i < 3; i++) {
            res.connections.add(heDevice.connectionType.scart);
            res.isPort.add(true);
            res.inConn.add(true);
            res.outConn.add(false);
            res.audioConn.add(true);
            res.videoConn.add(true);
        }
        return res;
    }

    /*
     * Cables
     */
    public static heDevice getHdmiCable() {
        heDevice res = new heDevice();
        res.deviceType = "hdmi-cable-";

        res.connections.add(heDevice.connectionType.hdmi);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.hdmi);
        res.isPort.add(false);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(true);

        return res;
    }

    public static heDevice getDviCable() {
        heDevice res = new heDevice();
        res.deviceType = "dvi-cable-";

        res.connections.add(heDevice.connectionType.dvi);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(false);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.dvi);
        res.isPort.add(false);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(false);
        res.videoConn.add(true);

        return res;
    }

    public static heDevice getHdmiDviCable() {
        heDevice res = new heDevice();
        res.deviceType = "hdmi-dvi-cable-";

        res.connections.add(heDevice.connectionType.hdmi);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(true);
        res.audioConn.add(false);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.dvi);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(true);
        res.audioConn.add(false);
        res.videoConn.add(true);

        return res;
    }

    public static heDevice getHeadphoneCinchCable() {
        heDevice res = new heDevice();
        res.deviceType = "headphone-cinch-cable-";

        res.connections.add(heDevice.connectionType.cinch);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(false);

        res.connections.add(heDevice.connectionType.headphone);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(false);

        return res;
    }

    public static heDevice getHdmiDviPortCable() {
        heDevice res = new heDevice();
        res.deviceType = "hdmi-dvip-cable-";

        res.connections.add(heDevice.connectionType.hdmi);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(true);
        res.audioConn.add(false);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.dvi);
        res.isPort.add(true);
        res.inConn.add(true);
        res.outConn.add(true);
        res.audioConn.add(false);
        res.videoConn.add(true);

        return res;
    }

    public static heDevice getCinchCable() {
        heDevice res = new heDevice();
        res.deviceType = "cinch-cable-";

        res.connections.add(heDevice.connectionType.cinch);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.cinch);
        res.isPort.add(false);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(true);

        return res;
    }

    public static heDevice getSpdif() {
        heDevice res = new heDevice();
        res.deviceType = "spdif-cable-";

        res.connections.add(heDevice.connectionType.spdif);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(false);

        res.connections.add(heDevice.connectionType.spdif);
        res.isPort.add(false);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(false);

        return res;
    }

    public static heDevice getScartCable() {
        heDevice res = new heDevice();
        res.deviceType = "scart-cable-";

        res.connections.add(heDevice.connectionType.scart);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.scart);
        res.isPort.add(false);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(true);

        return res;
    }

    public static heDevice getSpeakerWire() {
        heDevice res = new heDevice();
        res.deviceType = "speaker-wire-";

        res.connections.add(heDevice.connectionType.speakerWire);
        res.isPort.add(false);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(false);

        res.connections.add(heDevice.connectionType.speakerWire);
        res.isPort.add(false);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(false);

        return res;
    }
}
