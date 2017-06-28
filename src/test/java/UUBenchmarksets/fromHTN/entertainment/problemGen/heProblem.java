package UUBenchmarksets.fromHTN.entertainment.problemGen;

import UUBenchmarksets.fromHTN.entertainment.problemGen.heCables.heCable;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heConnections.heConnection;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heDevices.heDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dh on 20.05.17.
 */
public class heProblem {

    private List<heCable> cables;
    private List<heDevice> devices;
    private String name;
    private List<heDevice[]> aGoals;
    private List<heDevice[]> avGoals;
    private List<heDevice[]> vGoals;

    public heProblem(String name) {
        this.name = name;
        this.cables = new ArrayList<>();
        this.devices = new ArrayList<>();
        aGoals = new ArrayList<>();
        vGoals = new ArrayList<>();
        avGoals = new ArrayList<>();
    }

    public void addCable(heCable cable) {
        cables.add(cable);
    }

    public void addDevice(heDevice device) {
        this.devices.add(device);
    }

    @Override
    public String toString() {
        List<String> cableDefs = new ArrayList<>();
        List<String> plugDefs = new ArrayList<>();
        List<String> audioCables = new ArrayList<>();
        List<String> videoCables = new ArrayList<>();
        Map<String, List<String>> plugOf = new HashMap<>();
        Map<String, List<String>> typeDefPlug = new HashMap<>();

        List<String> deviceDefs = new ArrayList<>();
        List<String> portDefs = new ArrayList<>();
        List<String> inPorts = new ArrayList<>();
        List<String> outPorts = new ArrayList<>();
        List<String> audioPorts = new ArrayList<>();
        List<String> videoPorts = new ArrayList<>();
        Map<String, List<String>> portOf = new HashMap<>();
        Map<String, List<String>> typeDefPort = new HashMap<>();

        for (heCable c : cables) {
            int nextIndex = getNextIndex(cableDefs, c.getTypeStr());
            String cableName = c.getTypeStr() + nextIndex;
            cableDefs.add(cableName);
            if (c.isAudioCable())
                audioCables.add(cableName);
            if (c.isVideoCable())
                videoCables.add(cableName);
            List<String> myPlugs = c.getPlugs(cableName);
            plugDefs.addAll(myPlugs);
            plugOf.put(cableName, myPlugs);
            for (int i = 0; i < myPlugs.size(); i++) {
                String type = c.getPlugType(i);
                if (!typeDefPlug.containsKey(type))
                    typeDefPlug.put(type, new ArrayList<>());
                typeDefPlug.get(type).add(myPlugs.get(i));
                if (c.isInPlug(i))
                    inPorts.add(myPlugs.get(i));
                if (c.isOutPlug(i))
                    outPorts.add(myPlugs.get(i));
                if (c.isAudioPlug(i))
                    audioPorts.add(myPlugs.get(i));
                if (c.isVideoPlug(i))
                    videoPorts.add(myPlugs.get(i));
            }
        }

        for (heDevice d : devices) {
            int nextIndex = getNextIndex(cableDefs, d.getType());
            String deviceName = d.getType() + nextIndex;
            d.setName(deviceName);
            deviceDefs.add(deviceName);
            List<String> myPorts = d.getPorts(deviceName);
            portDefs.addAll(myPorts);
            portOf.put(deviceName, myPorts);
            for (int i = 0; i < myPorts.size(); i++) {
                String type = d.getPortType(i);
                if (!typeDefPort.containsKey(type))
                    typeDefPort.put(type, new ArrayList<>());
                typeDefPort.get(type).add(myPorts.get(i));
                if (d.isInPort(i))
                    inPorts.add(myPorts.get(i));
                if (d.isOutPort(i))
                    outPorts.add(myPorts.get(i));
                if (d.isAudioPort(i))
                    audioPorts.add(myPorts.get(i));
                if (d.isVideoPort(i))
                    videoPorts.add(myPorts.get(i));
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(define (problem " + this.getName() + ")\n" +
                " (:domain entertainment)\n" +
                " (:objects\n");
        writeList(deviceDefs, sb);
        sb.append(" - equipment\n");
        writeList(cableDefs, sb);
        sb.append(" - equipment\n");
        writeList(portDefs, sb);
        sb.append(" - connector\n");
        writeList(plugDefs, sb);
        sb.append(" - connector\n");

        sb.append(" )\n" +
                " (:htn\n" +
                "  :tasks (and\n");

        for (heDevice[] goal : this.aGoals)
            sb.append("     (a_connect " + goal[0].getName() + " " + goal[1].getName() + ")\n");
        for (heDevice[] goal : this.vGoals)
            sb.append("     (v_connect " + goal[0].getName() + " " + goal[1].getName() + ")\n");
        for (heDevice[] goal : this.avGoals)
            sb.append("     (av_connect " + goal[0].getName() + " " + goal[1].getName() + ")\n");

        sb.append("   )\n" +
                "  :ordering ( )\n" +
                "  :constraints ( ))\n" +
                " (:init\n");

        writeList(plugDefs, sb, "(unused ", ")");
        sb.append("\n");
        writeList(portDefs, sb, "(unused ", ")");
        sb.append("\n\n");

        writeList(outPorts, sb, "(out_connector ", ")");
        sb.append("\n");
        writeList(inPorts, sb, "(in_connector ", ")");
        sb.append("\n\n");

        writeList(audioPorts, sb, "(audio_connector ", ")");
        sb.append("\n");
        writeList(videoPorts, sb, "(video_connector ", ")");
        sb.append("\n\n");

        /*
        writeList(audioCables, sb, "(audio_cable ", ")");
        sb.append("\n");
        writeList(videoCables, sb, "(video_cable ", ")");
        sb.append("\n\n");*/

        for (String key : portOf.keySet()) {
            String pref = "(conn_of " + key + " ";
            writeList(portOf.get(key), sb, pref, ")");
            sb.append("\n");
        }

        for (String key : plugOf.keySet()) {
            String pref = "(conn_of " + key + " ";
            writeList(plugOf.get(key), sb, pref, ")");
            sb.append("\n\n");
        }

        for (String portType : typeDefPort.keySet()) {
            for (String plugType : typeDefPlug.keySet()) {
                if (heConnection.compatible(portType, plugType)) {
                    for (String port : typeDefPort.get(portType))
                        for (String plug : typeDefPlug.get(plugType)) {
                            sb.append("  (compatible ");
                            sb.append(port);
                            sb.append(" ");
                            sb.append(plug);
                            sb.append(")\n");
                            sb.append("  (compatible ");
                            sb.append(plug);
                            sb.append(" ");
                            sb.append(port);
                            sb.append(")\n");
                        }
                }
            }
        }
        sb.append(" )\n)\n");
        return sb.toString();
    }

    private void writeList(List<String> list, StringBuilder sb) {
        writeList(list, sb, "", "");
    }

    private void writeList(List<String> list, StringBuilder sb, String pref, String postf) {
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                sb.append("  ");
            } else {
                sb.append(" ");
            }
            sb.append(pref);
            sb.append(list.get(i));
            sb.append(postf);
        }
    }

    private int getNextIndex(List<String> cableDefs, String nameStr) {
        int i = 1;
        outerloop:
        while (true) {
            String lookFor = nameStr + i;
            for (String alreadyThere : cableDefs) {
                if (alreadyThere.equals(lookFor)) {
                    i++;
                    continue outerloop;
                }
            }
            return i;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addAvGoal(heDevice[] g) {
        this.avGoals.add(g);
    }

    public void addAGoal(heDevice[] g) {
        this.aGoals.add(g);
    }

    public void addVGoal(heDevice[] g) {
        this.vGoals.add(g);
    }
}
