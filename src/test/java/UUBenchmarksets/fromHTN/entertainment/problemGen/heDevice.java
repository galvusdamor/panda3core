// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package UUBenchmarksets.fromHTN.entertainment.problemGen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 19.05.17.
 */
public class heDevice {
    enum connectionType {vga, hdmi, dvi, spdif, headphone, scart, cinch, speakerWire}

    private String deviceName; // the name -> set by the problem and unique
    protected String deviceType; // the type -> there might be more than one
    protected List<Boolean> isPort = new ArrayList<>(); // it may be a port or a plug
    protected List<Boolean> inConn = new ArrayList<>();
    protected List<Boolean> outConn = new ArrayList<>();
    protected List<Boolean> audioConn = new ArrayList<>();
    protected List<Boolean> videoConn = new ArrayList<>();

    public List<connectionType> connections = new ArrayList<>();

    public List<String> getConnections() {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < connections.size(); i++) {
            res.add(deviceName + "-" + connections.get(i).toString() + "-" + (i + 1));
        }
        return res;
    }

    public boolean isPort(int i) {
        return isPort.get(i);
    }

    public boolean isInConn(int i) {
        return inConn.get(i);
    }

    public boolean isOutConn(int i) {
        return outConn.get(i);
    }

    public boolean isAudioConn(int i) {
        return audioConn.get(i);
    }

    public boolean isVideoConn(int i) {
        return videoConn.get(i);
    }

    public void setName(String name) {
        this.deviceName = name;
    }

    public String getName() {
        return this.deviceName;
    }

    public String getConnType(int i) {
        return connections.get(i).toString();
    }

    public String getType() {
        return deviceType;
    }
}
