package de.uniulm.ki.panda3.util.shopReader.internalModel;

import java.util.*;

/**
 * Created by dh on 11.08.17.
 */
public class shMethod {
    static HashSet allNames = new HashSet();
    public final String[] decompTask;
    public final List<List<String[]>[]> ifThen = new ArrayList<>();

    public shMethod(String[] decompTask) {
        this.decompTask = decompTask;
    }

    public void addIfThen(List<String[]>[] ifThen) {
        this.ifThen.add(ifThen);
    }

    Set<String> taskVars = null;

    public Set<String> addedVarsInLayer(int l) {
        if (taskVars == null) {
            taskVars = varsOfTask();
        }
        HashSet<String> res = new HashSet<>();
        List<String[]> prec = ifThen.get(l)[0];
        List<String[]> tn = ifThen.get(l)[1];
        for (String[] onePrec : prec) {
            for (int i = 2; i < onePrec.length; i++) {
                if (!taskVars.contains(onePrec[i]))
                    res.add(onePrec[i]);
            }
        }
        for (String[] oneTask : tn) {
            for (int i = 1; i < oneTask.length; i++) {
                if (!taskVars.contains(oneTask[i]))
                    res.add(oneTask[i]);
            }
        }

        return res;
    }

    public Set<String> varsOfTask() {
        taskVars = new HashSet<>();
        for (int i = 1; i < decompTask.length; i++)
            taskVars.add(decompTask[i]);
        return taskVars;
    }

    public String getName() {
        String name = "m-" + decompTask[0] + "-";
        int i = 1;
        while (allNames.contains(name + i)) {
            i++;
        }
        name = name + i;
        allNames.add(name);
        return name;
    }
}
