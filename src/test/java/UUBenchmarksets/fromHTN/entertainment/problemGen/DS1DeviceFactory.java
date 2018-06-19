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

/**
 * Created by dh on 17.08.17.
 */
public class DS1DeviceFactory {
    public static heDevice getReceiver() {
        heDevice res = new heDevice();
        res.deviceType = "sat-receiver-";
        for (int i = 0; i < 3; i++) {
            res.connections.add(heDevice.connectionType.scart);
            res.isPort.add(true);
            res.inConn.add(false);
            res.outConn.add(true);
            res.audioConn.add(true);
            res.videoConn.add(true);
        }
        for (int i = 0; i < 2; i++) {
            res.connections.add(heDevice.connectionType.cinch);
            res.isPort.add(true);
            res.inConn.add(false);
            res.outConn.add(true);
            res.audioConn.add(false);
            res.videoConn.add(true);
        }

        res.connections.add(heDevice.connectionType.cinch);
        res.isPort.add(true);
        res.inConn.add(false);
        res.outConn.add(true);
        res.audioConn.add(true);
        res.videoConn.add(false);

        return res;
    }

    public static heDevice getBRPlayer() {
        heDevice res = new heDevice();
        res.deviceType = "blu-ray-";
        res.connections.add(heDevice.connectionType.hdmi);
        res.isPort.add(true);
        res.inConn.add(false);
        res.outConn.add(true);
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

    public static heDevice getTV() {
        heDevice res = new heDevice();
        res.deviceType = "tv-";
        for (int i = 0; i < 4; i++) {
            res.connections.add(heDevice.connectionType.hdmi);
            res.isPort.add(true);
            res.inConn.add(true);
            res.outConn.add(false);
            res.audioConn.add(true);
            res.videoConn.add(true);
        }

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

        res.connections.add(heDevice.connectionType.vga);
        res.isPort.add(true);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(false);
        res.videoConn.add(true);

        res.connections.add(heDevice.connectionType.headphone);
        res.isPort.add(true);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(false);

        res.connections.add(heDevice.connectionType.spdif);
        res.isPort.add(true);
        res.inConn.add(true);
        res.outConn.add(false);
        res.audioConn.add(true);
        res.videoConn.add(false);

        return res;
    }

    public static heDevice getAmp() {
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

        for (int i = 0; i < 2; i++) {
            amp.connections.add(heDevice.connectionType.cinch);
            amp.isPort.add(true);
            amp.inConn.add(true);
            amp.outConn.add(false);
            amp.audioConn.add(false);
            amp.videoConn.add(true);
        }

        amp.connections.add(heDevice.connectionType.cinch);
        amp.isPort.add(true);
        amp.inConn.add(false);
        amp.outConn.add(true);
        amp.audioConn.add(false);
        amp.videoConn.add(true);

        for (int i = 0; i < 5; i++) {
            amp.connections.add(heDevice.connectionType.speakerWire);
            amp.isPort.add(true);
            amp.inConn.add(false);
            amp.outConn.add(true);
            amp.audioConn.add(true);
            amp.videoConn.add(false);
        }

        for (int i = 0; i < 6; i++) {
            amp.connections.add(heDevice.connectionType.hdmi);
            amp.isPort.add(true);
            amp.inConn.add(true);
            amp.outConn.add(false);
            amp.audioConn.add(true);
            amp.videoConn.add(true);
        }
        amp.connections.add(heDevice.connectionType.hdmi);
        amp.isPort.add(true);
        amp.inConn.add(false);
        amp.outConn.add(true);
        amp.audioConn.add(true);
        amp.videoConn.add(true);

        amp.connections.add(heDevice.connectionType.spdif);
        amp.isPort.add(true);
        amp.inConn.add(true);
        amp.outConn.add(false);
        amp.audioConn.add(true);
        amp.videoConn.add(false);

        return amp;
    }
}
