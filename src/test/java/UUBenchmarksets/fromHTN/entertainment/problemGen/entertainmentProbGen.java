package UUBenchmarksets.fromHTN.entertainment.problemGen;

import UUBenchmarksets.fromHTN.entertainment.problemGen.heCables.heCinchCable;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heCables.heScartCable;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heCables.heScartToCinchCable;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heDevices.heDevice;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heDevices.heDvdPlayer;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heDevices.heScartToCinchAdapter;
import UUBenchmarksets.fromHTN.entertainment.problemGen.heDevices.heTV;

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
        //problems.add(getProblem4());

        for (heProblem p : problems) {
            String filename = baseDir + p.getName() + ".lisp";
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            bw.write(p.toString());
            bw.close();
        }
    }

    private static heProblem getProblem1() {
        heProblem p = new heProblem("p01-simple-dvd-tv");
        heDevice dvd = new heDvdPlayer();
        p.addDevice(dvd);
        heDevice tv = heTV.getTVwithScart();
        p.addDevice(tv);
        p.addCable(new heScartCable());
        heDevice[] g = new heDevice[2];
        g[0] = dvd;
        g[1] = tv;
        p.addAvGoal(g);
        return p;
    }

    private static heProblem getProblem2() {
        heProblem p = new heProblem("p02-split-with-adapter");
        heDevice dvd = new heDvdPlayer();
        p.addDevice(dvd);
        heDevice tv = heTV.getTVwithCinch();
        p.addDevice(tv);
        heDevice adapter = new heScartToCinchAdapter();
        p.addDevice(adapter);
        p.addCable(new heScartCable());
        p.addCable(new heCinchCable());
        p.addCable(new heCinchCable());
        heDevice[] g = new heDevice[2];
        g[0] = dvd;
        g[1] = tv;
        p.addAvGoal(g);
        return p;
    }

    private static heProblem getProblem3() {
        heProblem p = new heProblem("p03-split-with-cable");
        heDevice dvd = new heDvdPlayer();
        p.addDevice(dvd);
        heDevice tv = heTV.getTVwithCinch();
        p.addDevice(tv);
        p.addCable(new heScartToCinchCable());
        heDevice[] g = new heDevice[2];
        g[0] = dvd;
        g[1] = tv;
        p.addAvGoal(g);
        return p;
    }
/* does not work, yet
    private static heProblem getProblem4() {
        heProblem p = new heProblem("p04-split-and-rejoin");
        heDevice dvd = new heDvdPlayer();
        p.addDevice(dvd);
        heDevice tv = heTV.getTVwithScart();
        p.addDevice(tv);
        heDevice adapter = new heScartToCinchAdapter();
        p.addDevice(adapter);
        p.addCable(new heScartCable());
        p.addCable(new heScartToCinchCable());
        heDevice[] g = new heDevice[2];
        g[0] = dvd;
        g[1] = tv;
        p.addAvGoal(g);
        return p;
    }*/
}
