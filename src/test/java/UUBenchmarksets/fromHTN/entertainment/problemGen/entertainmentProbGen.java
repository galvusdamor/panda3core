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
        problems.add(getProblem1());
        problems.add(getProblem2());
        problems.add(getProblem3());
        problems.add(getProblem4());
        problems.add(getProblem5());

        for (heProblem p : problems) {
            String filename = baseDir + p.getName() + ".lisp";
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            bw.write(p.toString());
            bw.close();
        }
    }

    private static heProblem getProblem1() {
        heProblem p = new heProblem("p01-simple-dvd-tv");
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

    private static heProblem getProblem2() {
        heProblem p = new heProblem("p02-split-with-adapter");
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

    private static heProblem getProblem3() {
        heProblem p = new heProblem("p03-split-with-cable");
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

    private static heProblem getProblem4() {
        heProblem p = new heProblem("p04-split-and-rejoin");
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

    private static heProblem getProblem5() {
        heProblem p = new heProblem("p05-use-twice");
        heDevice dvd1 = DeviceFactory.getDVDPlayerWithScart();
        heDevice dvd2 = DeviceFactory.getDVDPlayerWithScart();
        heDevice dvd3 = DeviceFactory.getDVDPlayerWithScart();
        heDevice tv = DeviceFactory.getTVwithScart();
        p.addDevice(dvd1);
        p.addDevice(dvd2);
        p.addDevice(dvd3);
        p.addDevice(tv);
        p.addDevice(DeviceFactory.getMultiScartInput());

        p.addDevice(DeviceFactory.getScartCable());
        p.addDevice(DeviceFactory.getScartCable());
        p.addDevice(DeviceFactory.getScartCable());
        p.addDevice(DeviceFactory.getScartCable());

        heDevice[] g1 = new heDevice[2];
        g1[0] = dvd1;
        g1[1] = tv;
        p.addAvGoal(g1);

        heDevice[] g2 = new heDevice[2];
        g2[0] = dvd2;
        g2[1] = tv;
        p.addAvGoal(g2);

        heDevice[] g3 = new heDevice[2];
        g3[0] = dvd3;
        g3[1] = tv;
        p.addAvGoal(g3);
        return p;
    }
}
