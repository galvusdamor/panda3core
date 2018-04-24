package de.uniulm.ki.panda3.planRecognition.kitchen;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dh on 30.10.17.
 */
public class kitchenProblemGen {
    static String outputFolder = "/media/dh/Volume/repositories/private-documents/evaluation-domains/kitchen/problems/";
    static String problemTemplate = "/media/dh/Volume/repositories/private-documents/evaluation-domains/kitchen/p-template.lisp";
    static String gtFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/kitchen/groundtruth.txt";
    static String generatorFile = "/media/dh/Volume/repositories/private-documents/evaluation-domains/kitchen/generatorMethods.txt";

    public static void main(String[] in) throws Exception {
        System.out.println("Generating problems...");
        List<String> starters = new ArrayList<>();
        starters.add("(makeTomatoSoup ?POT)\n");
        starters.add("(makeLettuce ?BOWL)\n");
        starters.add("(makeTomatoMozzarella ?BOWL)\n");
        starters.add("(makeBruchetta)\n");
        starters.add("(makeCarrotSoup ?POT)\n");

        List<String> main = new ArrayList<>();
        String[] noodles = {
                "(makeNoodles spaghetti ?POT)\n",
                //"(makeNoodles macaroni ?POT)\n",
                //"(makeNoodles farfalle ?POT)\n",
                "(makeNoodles cannelloni ?POT)\n",
                "(makeNoodles tortellini ?POT)\n",
                "(makeNoodles ravioli ?POT)\n"};
        String[] noodleSauce = {
                "(makeBolognese ?PAN)\n",
                "(makeCarbonara ?PAN)\n",
                "(makeAllArrabbiata ?POT)\n"};
        for (String n : noodles) {
            for (String s : noodleSauce) {
                main.add(n + s);
            }
        }

        String[] beilagen = {
                "(makeBoiledPotatoes ?POT)\n",
                "(makeSkinnedPotatoes ?POT)\n",
                "(makeRice ?POT)\n"};
        String[] gemuese = {
                "(makeBeans ?POT)\n",
                "(makePea ?POT)\n"};
        String[] fleisch = {
                "(makeTrout ?PAN)\n",
                "(makeChicken ?RT)\n",
                "(makeSchnitzel ?PAN)\n"
        };
        for (String bei : beilagen) {
            for (String gem : gemuese) {
                for (String flei : fleisch) {
                    main.add(bei + flei + gem);
                }
            }
        }

        List<String> dessert = new ArrayList<>();
        dessert.add("(makeVanillaPudding ?POT)\n");
        dessert.add("(makeVanillaRaspberryIce ?BOWL)\n");
        dessert.add("(makeTiramisu ?BOWL)\n");
        dessert.add("(makeMascarpone ?BOWL)\n");
        dessert.add("(makePancakes ?PAN)\n");

        gt = new BufferedWriter(new FileWriter(gtFile));

        for (String m : main) {
            writeMeal(m);
            for (String s : starters) {
                writeMeal(s + m);
            }
            for (String d : dessert) {
                writeMeal(m + d);
            }
        }
        for (String m : main) {
            for (String s : starters) {
                for (String d : dessert) {
                    writeMeal(s + m + d);
                }
            }
        }

        BufferedWriter br = new BufferedWriter(new FileWriter(generatorFile));

        br.write(getMethod("generator-" + (1), "mtlt", "(makeMain)\n"));
        br.write(getMethod("generator-" + (2), "mtlt", "(makeStarter)\n(makeMain)\n"));
        br.write(getMethod("generator-" + (3), "mtlt", "(makeMain)\n(makeDessert)\n"));
        br.write(getMethod("generator-" + (4), "mtlt", "(makeStarter)\n(makeMain)\n(makeDessert)\n"));

        System.out.println("Starters    : " + starters.size());
        for (int i = 0; i < starters.size(); i++) {
            br.write(getMethod("starter-" + (i + 1), "makeStarter", starters.get(i)));
        }
        System.out.println("Main Dishes : " + main.size());
        for (int i = 0; i < main.size(); i++) {
            br.write(getMethod("main-" + (i + 1), "makeMain", main.get(i)));
        }
        System.out.println("Desserts    : " + dessert.size());
        for (int i = 0; i < dessert.size(); i++) {
            br.write(getMethod("dessert-" + (i + 1), "makeDessert", dessert.get(i)));
        }
        for (String key : allConsts.keySet()) {
            System.out.println("Need " + allConsts.get(key) + " variables of type " + key);
        }
        gt.close();
        br.close();

        System.out.println("Mean goal count " + ((double) sum / (double) num));
    }

    private static String getMethod(String mName, String tlt, String meal) {
        List<String[]> allParams = new ArrayList<>();
        HashMap<String, Integer> constNum = new HashMap<>();
        for (int i = 0; i < meal.length(); i++) {
            String oneParam = "";
            if (meal.charAt(i) == '?') {
                while ((meal.charAt(i) != ' ') && (meal.charAt(i) != ')')) {
                    oneParam += meal.charAt(i++);
                }
                if (!constNum.containsKey(oneParam)) {
                    constNum.put(oneParam, 0);
                }
                int num = constNum.get(oneParam) + 1;
                constNum.put(oneParam, num);
                String[] str = new String[2];
                str[0] = oneParam;
                if (oneParam.equals("?POT")) {
                    str[1] = "?pot" + num + " - cookingPot";
                } else if (oneParam.equals("?BOWL")) {
                    str[1] = "?bowl" + num + " - bowl";
                } else if (oneParam.equals("?PAN")) {
                    str[1] = "?pan" + num + " - pan";
                } else if (oneParam.equals("?RT")) {
                    str[1] = "?rt" + num + " - roastingTin";
                } else {
                    System.out.println("Type not found: " + oneParam);
                }
                allParams.add(str);
            }
        }
        String paramStr = "";
        for (String[] rep : allParams) {
            paramStr += rep[1] + " ";
            meal = meal.replaceFirst("\\" + rep[0], rep[1].split(" ")[0]);
        }

        String m = "\n" +
                "  (:method " + mName + "\n" +
                "    :parameters (" + paramStr.trim() + ")\n" +
                "    :task (" + tlt + ")\n" +
                "    :subtasks (and\n" +
                "       " + meal.replaceAll("\n", "\n       ") + ")\n" +
                "  )\n";
        return m;
    }

    static BufferedWriter gt;
    static int sum = 0;

    static int num = 0;
    static String res = "";

    private static void writeMeal(String m) throws Exception {
        if (res.length() == 0) {
            readProblemTemplate();
        }
        num++;
        String filename = getFileName();
        m = nameConsts(m);
        String task = res.replaceFirst("NNN", "" + num);
        m = "   " + m.replaceAll("\n", "\n   ").trim();
        task = task.replace(";; ADD TASKS HERE", m);
        writeToFile(filename, task);
        int tlts = countTLTs(m);
        sum += tlts;
        gt.write(m.replaceAll("\n   ", "; ").trim() + "\n");//+ "\t" + tlts
    }

    private static int countTLTs(String m) {
        int count = 1;
        for (int i = 0; i < m.length(); i++) {
            if (m.charAt(i) == '\n')
                count++;
        }
        return count;
    }

    static HashMap<String, Integer> allConsts = new HashMap<>();

    private static String nameConsts(String m) {
        HashMap<String, Integer> consts = new HashMap<>();
        while (m.contains("?")) {
            int start = m.indexOf("?");
            int length = 0;
            while (!(m.charAt(start + length) == ' ') && !(m.charAt(start + length) == ')')) {
                length++;
            }
            String key = m.substring(start + 1, start + length);
            String cName = key.toLowerCase();
            if (!consts.containsKey(key)) {
                consts.put(key, 0);
            }
            if (!allConsts.containsKey(key)) {
                allConsts.put(key, 0);
            }
            consts.put(key, consts.get(key) + 1);
            if (consts.get(key) > allConsts.get(key))
                allConsts.put(key, consts.get(key));
            cName += consts.get(key);
            m = m.substring(0, start) + cName + m.substring(start + length);
        }
        return m;
    }

    private static void writeToFile(String filename, String task) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write(task);
        bw.close();
    }

    private static String getFileName() {
        String numStr = "";
        while ((numStr + num).length() < 4)
            numStr += "0";
        numStr += num;
        return outputFolder + "p-" + numStr + "-kitchen.lisp";
    }

    private static void readProblemTemplate() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(problemTemplate));
        while (br.ready())
            res += br.readLine() + "\n";
        br.close();
    }
}
