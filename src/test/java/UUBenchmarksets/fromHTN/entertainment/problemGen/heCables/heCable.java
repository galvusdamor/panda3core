package UUBenchmarksets.fromHTN.entertainment.problemGen.heCables;

import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.hePlug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 19.05.17.
 */
public abstract class heCable {
    protected String typeStr;
    public List<hePlug> plugs;
    protected boolean audioCable;
    protected boolean videoCable;
    protected List<Boolean> inPlug = new ArrayList<>();
    protected List<Boolean> outPlug = new ArrayList<>();
    protected List<Boolean> audioPlug = new ArrayList<>();
    protected List<Boolean> videoPlug = new ArrayList<>();

    public boolean isAudioCable() {
        return audioCable;
    }

    public boolean isVideoCable() {
        return videoCable;
    }

    public List<String> getPlugs(String cableName) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < plugs.size(); i++) {
            res.add(cableName + "-" + plugs.get(i).typeStr + "-" + (i + 1));
        }
        return res;
    }

    public String getPlugType(int i) {
        return plugs.get(i).typeStr;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public boolean isInPlug(int i) {
        return inPlug.get(i);
    }

    public boolean isOutPlug(int i) {
        return this.outPlug.get(i);
    }

    public boolean isAudioPlug(int i) {
        return this.audioPlug.get(i);
    }

    public boolean isVideoPlug(int i) {
        return this.videoPlug.get(i);
    }
}