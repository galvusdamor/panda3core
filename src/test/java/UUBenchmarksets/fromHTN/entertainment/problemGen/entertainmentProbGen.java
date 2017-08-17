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

        // DS1
        problems.add(getProblemDS1("09", 0));
        problems.add(getProblemDS1("10", 1));
        problems.add(getProblemDS1("11", 2));
        problems.add(getProblemDS1("12", 3));

        for (heProblem p : problems) {
            String filename = baseDir + p.getName() + ".lisp";
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            bw.write(p.toString());
            bw.close();
        }
    }

    private static heProblem getProblemDS1(String s, int level) {
        heProblem p = getProblem6("");
        p.name = "p" + s;
        if (level == 0) {
            p.name+= "-tv-ds1-one-dev";
        } else if (level == 1) {
            p.name+= "-tv-ds1-two-dev";
        } else if (level == 2) {
            p.name+= "-tv-ds1-one-dev-and-speaker";
        } else if (level == 3) {
            p.name+= "-tv-ds1-teo-dev-and-speaker";
        }
        // cables
        p.addDevice(DeviceFactory.getScartToCinchCable());
        for (int i = 0; i < 4; i++)
            p.addDevice(DeviceFactory.getCinchCable());
        for (int i = 0; i < 2; i++)
            p.addDevice(DeviceFactory.getHdmiCable());
        p.addDevice(DeviceFactory.getDviCable());
        p.addDevice(DeviceFactory.getHdmiDviCable());
        p.addDevice(DeviceFactory.getHeadphoneCinchCable());
        for (int i = 0; i < 2; i++)
            p.addDevice(DeviceFactory.getHdmiDviPortCable());
        p.addDevice(DeviceFactory.getSpdif());

        // devices
        heDevice brPlayer = DS1DeviceFactory.getBRPlayer();
        p.addDevice(brPlayer);
        heDevice receiver = DS1DeviceFactory.getReceiver();
        p.addDevice(receiver);
        heDevice tv = DS1DeviceFactory.getTV();
        p.addDevice(tv);
        heDevice amp = DS1DeviceFactory.getAmp();
        p.addDevice(amp);

        heDevice[] g1 = new heDevice[2];
        g1[0] = brPlayer;
        g1[1] = tv;

        heDevice[] g2 = new heDevice[2];
        g2[0] = brPlayer;
        g2[1] = amp;

        p.addVGoal(g1); // br to tv
        p.addAGoal(g2); // br to amp
        if ((level == 1) || (level == 3)) {
            heDevice[] g3 = new heDevice[2];
            g3[0] = receiver;
            g3[1] = tv;

            heDevice[] g4 = new heDevice[2];
            g4[0] = receiver;
            g4[1] = amp;

            p.addVGoal(g3); // sat to tv
            p.addAGoal(g4); // sat to amp
        }
        if ((level == 2) || (level == 3)) {
            heDevice box1 = DeviceFactory.getBox();
            heDevice box2 = DeviceFactory.getBox();
            p.addDevice(box1);
            p.addDevice(box2);
            p.addDevice(DeviceFactory.getSpeakerWire());
            p.addDevice(DeviceFactory.getSpeakerWire());

            heDevice[] g5 = new heDevice[2];
            g5[0] = amp;
            g5[1] = box1;

            heDevice[] g6 = new heDevice[2];
            g6[0] = amp;
            g6[1] = box2;
            p.addAGoal(g5); // amp box1
            p.addAGoal(g6); // amp box2
        }
        return p;
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
