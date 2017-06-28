package UUBenchmarksets.fromHTN.entertainment.problemGen.heDevices;

import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.hePort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 19.05.17.
 */
public abstract class heDevice {
    private String deviceName; // the name -> set by the problem and unique
    protected String deviceType; // the type -> there might be more than one
    protected List<Boolean> inPort = new ArrayList<>();
    protected List<Boolean> outPort = new ArrayList<>();
    protected List<Boolean> audioPort = new ArrayList<>();
    protected List<Boolean> videoPort = new ArrayList<>();

    public List<hePort> ports = new ArrayList<>();

    public List<String> getPorts(String cableName) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < ports.size(); i++) {
            res.add(cableName + "-" + ports.get(i).typeStr + "-" + (i + 1));
        }
        return res;
    }

    public boolean isInPort(int i) {
        return inPort.get(i);
    }

    public boolean isOutPort(int i) {
        return outPort.get(i);
    }

    public boolean isAudioPort(int i) {
        return audioPort.get(i);
    }

    public boolean isVideoPort(int i) {
        return videoPort.get(i);
    }

    public void setName(String name) {
        this.deviceName = name;
    }

    public String getName() {
        return this.deviceName;
    }

    public String getPortType(int i) {
        return ports.get(i).typeStr;
    }

    public String getType() {
        return deviceType;
    }
}
