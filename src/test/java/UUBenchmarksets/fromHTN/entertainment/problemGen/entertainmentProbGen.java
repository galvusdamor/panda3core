package UUBenchmarksets.fromHTN.entertainment.problemGen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 19.05.17.
 */
public class entertainmentProbGen {
    static String baseDir = "/home/dh/IdeaProjects/panda3core_with_planning_graph/src/test/java/UUBenchmarksets/fromHTN/entertainment/problems/";

    public static void main(String[] args) throws Exception {
        List<heProblem> problems = new ArrayList<>();
        //problems.add(getProblem1("01"));
        problems.add(getProblem2("01"));
        problems.add(getProblem3("02"));
        problems.add(getProblem4("03"));
        problems.add(getProblem6("04"));
        problems.add(getProblem7("05"));
        problems.add(getProblem5("06"));
        problems.add(getProblem8("07"));
        problems.add(getProblem9("08"));

        for (heProblem p : problems) {
            String filename = baseDir + p.getName() + ".lisp";
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            bw.write(p.toString());
            bw.close();
        }
    }

    private static heProblem getProblem7(String s) {
        heProblem p = getProblem6("");
        p.name = "p" + s + "-tv-dvd-gc-box";
        p.addDevice(DeviceFactory.getScartCable());
        p.addDevice(DeviceFactory.getCinchCable());
        p.addDevice(DeviceFactory.getCinchCable());

        heDevice amp = DeviceFactory.getActiveSpeaker();
        p.addDevice(amp);

        heDevice[] g1 = new heDevice[2];
        g1[0] = p.devices.get(1);
        g1[1] = amp;
        p.addAGoal(g1);

        return p;
    }

    private static heProblem getProblem6(String s) {
        heProblem p = new heProblem("p" + s + "-tv-dvd-gc");
        heDevice dvd = DeviceFactory.getDVDPlayerWithScart();
        p.addDevice(dvd);
        heDevice tv = DeviceFactory.getTV1(2);
        p.addDevice(tv);
        heDevice gc = DeviceFactory.getGameConsole();
        p.addDevice(gc);
        p.addDevice(DeviceFactory.getScartCable());
        p.addDevice(DeviceFactory.getScartCable());
        heDevice[] g1 = new heDevice[2];
        g1[0] = dvd;
        g1[1] = tv;
        heDevice[] g2 = new heDevice[2];
        g2[0] = gc;
        g2[1] = tv;
        p.addAvGoal(g1);
        p.addAvGoal(g2);
        return p;
    }

    private static heProblem getProblem1(String s) {
        heProblem p = new heProblem("p" + s + "-simple-dvd-tv");
        heDevice dvd = DeviceFactory.getDVDPlayerWithScart();
        p.addDevice(dvd);
        heDevice tv = DeviceFactory.getTVwithScart();
        p.addDevice(tv);
        p.addDevice(DeviceFactory.getScartCable());
        heDevice[] g = new heDevice[2];
        g[0] = dvd;
        g[1] = tv;
        p.addAvGoal(g);
        return p;
    }

    private static heProblem getProblem2(String s) {
        heProblem p = new heProblem("p" + s + "-split-with-adapter");
        heDevice dvd = DeviceFactory.getDVDPlayerWithScart();
        p.addDevice(dvd);
        heDevice tv = DeviceFactory.getTVwithCinch();
        p.addDevice(tv);
        heDevice adapter = DeviceFactory.getScartPlugToCinchPortAdapter();
        p.addDevice(adapter);
        p.addDevice(DeviceFactory.getScartCable());
        p.addDevice(DeviceFactory.getCinchCable());
        p.addDevice(DeviceFactory.getCinchCable());
        heDevice[] g = new heDevice[2];
        g[0] = dvd;
        g[1] = tv;
        p.addAvGoal(g);
        return p;
    }

    private static heProblem getProblem3(String s) {
        heProblem p = new heProblem("p" + s + "-split-with-cable");
        heDevice dvd = DeviceFactory.getDVDPlayerWithScart();
        p.addDevice(dvd);
        heDevice tv = DeviceFactory.getTVwithCinch();
        p.addDevice(tv);
        p.addDevice(DeviceFactory.getScartToCinchCable());
        heDevice[] g = new heDevice[2];
        g[0] = dvd;
        g[1] = tv;
        p.addAvGoal(g);
        return p;
    }

    private static heProblem getProblem4(String s) {
        heProblem p = new heProblem("p" + s + "-split-and-rejoin");
        heDevice dvd = DeviceFactory.getDVDPlayerWithScart();
        heDevice tv = DeviceFactory.getTVwithScart();
        p.addDevice(dvd);
        p.addDevice(tv);
        p.addDevice(DeviceFactory.getScartPlugToCinchPortAdapter());
        p.addDevice(DeviceFactory.getCinchPortToScartPlugAdapter());

        p.addDevice(DeviceFactory.getCinchCable());
        p.addDevice(DeviceFactory.getCinchCable());

        heDevice[] g = new heDevice[2];
        g[0] = dvd;
        g[1] = tv;
        p.addAvGoal(g);
        return p;

    }

    private static heProblem getProblem5(String s) {
        heProblem p = new heProblem("p" + s + "-use-twice");
        heDevice br = DeviceFactory.getBRPlayerWithScart();
        heDevice dvd = DeviceFactory.getDVDPlayerWithScart();
        heDevice gc = DeviceFactory.getGameConsole();
        heDevice tv = DeviceFactory.getTV1(1);
        p.addDevice(br);
        p.addDevice(dvd);
        p.addDevice(gc);
        p.addDevice(tv);
        p.addDevice(DeviceFactory.getMultiScartInput());

        p.addDevice(DeviceFactory.getScartCable());
        p.addDevice(DeviceFactory.getScartCable());
        p.addDevice(DeviceFactory.getScartCable());
        p.addDevice(DeviceFactory.getScartCable());

        heDevice[] g1 = new heDevice[2];
        g1[0] = br;
        g1[1] = tv;
        p.addAvGoal(g1);

        heDevice[] g2 = new heDevice[2];
        g2[0] = dvd;
        g2[1] = tv;
        p.addAvGoal(g2);

        heDevice[] g3 = new heDevice[2];
        g3[0] = gc;
        g3[1] = tv;
        p.addAvGoal(g3);
        return p;
    }

    private static heProblem getProblem8(String s) {
        heProblem p = getProblem5("");
        p.name = "p" + s + "-use-twice-box";
        p.addDevice(DeviceFactory.getScartCable());
        p.addDevice(DeviceFactory.getCinchCable());
        p.addDevice(DeviceFactory.getCinchCable());

        heDevice amp = DeviceFactory.getActiveSpeaker();
        p.addDevice(amp);

        heDevice[] g1 = new heDevice[2];
        g1[0] = p.devices.get(3);
        g1[1] = amp;
        p.addAGoal(g1);

        return p;
    }

    private static heProblem getProblem9(String s) {
        heProblem p = getProblem5("");
        p.name = "p" + s + "-use-twice-amp";
        p.addDevice(DeviceFactory.getCinchCable());
        p.addDevice(DeviceFactory.getSpeakerWire());
        p.addDevice(DeviceFactory.getSpeakerWire());

        heDevice amp = DeviceFactory.getAmplifier();
        heDevice box1 = DeviceFactory.getBox();
        heDevice box2 = DeviceFactory.getBox();

        p.addDevice(amp);
        p.addDevice(box1);
        p.addDevice(box2);

        heDevice[] g1 = new heDevice[2];
        g1[0] = p.devices.get(3);
        g1[1] = amp;
        p.addAGoal(g1);

        heDevice[] g2 = new heDevice[2];
        g2[0] = amp;
        g2[1] = box1;
        p.addAGoal(g2);

        heDevice[] g3 = new heDevice[2];
        g3[0] = amp;
        g3[1] = box2;
        p.addAGoal(g3);

        return p;
    }

}
