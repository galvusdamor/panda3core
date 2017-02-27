package de.uniulm.ki.panda3.progression.sasp;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by dh on 24.02.17.
 */
public class SasPlusProblem {
    private static String noVal = "<none of those>";

    private int version;
    private int error = 0;
    private boolean actionCosts = false;

    // number of elemets
    private int numberOfVars;
    private int numberOfMutexGroups;
    private int numberOfGoalPairs;
    private int numberOfOperators;

    public int[] axioms;
    public int[] ranges;
    private String[] varNames;
    private String[][] values;

    private int[] s0;
    private int[][] goal;

    private String[] opNames;
    private int[] numPrevailConditions;
    private int[][][] prevailConditions; // operator, conditionId ->
    int[] numEffects;
    int[] costs;
    private int[][] numberOfEffectConditions;
    private int[][][] effectConditions; // operatorId, effectId -> pair(int valueId, int value)
    private int[][] effectVar; // operatorId, effectId -> int variableId
    private int[][] effectVarPrec; // operatorId, effectId -> int variableId
    private int[][] effectVarEff;  // operatorId, effectId -> int variableId

    public SasPlusProblem(String Filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(Filename));
        String line = br.readLine();
        if (!line.equals("begin_version")) {
            this.error = 1;
            System.out.println("Error: (SAS+-parser) Did not find version element.");
            return;
        }
        readVersion(br);
        line = br.readLine();

        if (!line.equals("begin_metric")) {
            this.error = 1;
            System.out.println("Error: (SAS+-parser) Did not find metric element.");
            return;
        }
        readMetric(br);

        // read variable definition
        numberOfVars = Integer.parseInt(br.readLine());
        varNames = new String[numberOfVars];
        axioms = new int[numberOfVars];
        ranges = new int[numberOfVars];
        values = new String[numberOfVars][];

        for (int i = 0; i < numberOfVars; i++) {
            readVariable(br, i);
        }

        // read mutex groups
        numberOfMutexGroups = Integer.parseInt(br.readLine());

        for (int i = 0; i < numberOfMutexGroups; i++) {
            readMutex(br, i);
        }
        line = br.readLine();

        // read s0 and goal
        if (!line.equals("begin_state")) {
            this.error = 1;
            System.out.println("Error: (SAS+-parser) Did not find (initial) state element.");
            return;
        }
        readS0(br);
        line = br.readLine();

        if (!line.equals("begin_goal")) {
            this.error = 1;
            System.out.println("Error: (SAS+-parser) Did not find goal element.");
            return;
        }
        readGoal(br);
        line = br.readLine();

        // read operators
        this.numberOfOperators = Integer.parseInt(br.readLine());
        this.opNames = new String[this.numberOfOperators];
        this.numEffects = new int[this.numberOfOperators];
        this.numPrevailConditions = new int[this.numberOfOperators];
        this.prevailConditions = new int[this.numberOfOperators][][];
        this.numberOfEffectConditions = new int[this.numberOfOperators][];
        this.effectConditions = new int[this.numberOfOperators][][];
        this.effectVar = new int[this.numberOfOperators][];
        this.effectVarPrec = new int[this.numberOfOperators][];
        this.effectVarEff = new int[this.numberOfOperators][];
        this.costs = new int[this.numberOfOperators];

        for (int i = 0; i < this.numberOfOperators; i++) {
            readOperator(br, i);
        }
    }

    private void readOperator(BufferedReader br, int i) throws Exception {
        if (!br.readLine().equals("begin_operator")) {
            this.error = 1;
            System.out.println("Error: (SAS+-parser) Unexpected structure of operator element.");
        }

        this.opNames[i] = br.readLine();
        this.numPrevailConditions[i] = Integer.parseInt(br.readLine());
        this.prevailConditions[i] = new int[this.numPrevailConditions[i]][];

        for (int j = 0; j < this.numPrevailConditions[i]; j++) {
            this.prevailConditions[i][j] = new int[2];
            String[] pair = br.readLine().split(" ");
            this.prevailConditions[i][j][0] = Integer.parseInt(pair[0]);
            this.prevailConditions[i][j][1] = Integer.parseInt(pair[1]);
        }

        this.numEffects[i] = Integer.parseInt(br.readLine());
        numberOfEffectConditions[i] = new int[this.numEffects[i]];
        effectVar[i] = new int[this.numEffects[i]];
        effectVarPrec[i] = new int[this.numEffects[i]];
        effectVarEff[i] = new int[this.numEffects[i]];

        for (int j = 0; j < this.numEffects[i]; j++) {
            String[] eff = br.readLine().split(" ");
            int k = 0;
            this.numberOfEffectConditions[i][j] = Integer.parseInt(eff[k++]);
            for (int l = 0; l < this.numberOfEffectConditions[i][j]; l++) {
                effectConditions[i][j] = new int[2];
                effectConditions[i][j][0] = Integer.parseInt(eff[k++]);
                effectConditions[i][j][1] = Integer.parseInt(eff[k++]);
            }
            effectVar[i][j] = Integer.parseInt(eff[k++]);
            effectVarPrec[i][j] = Integer.parseInt(eff[k++]);
            effectVarEff[i][j] = Integer.parseInt(eff[k++]);
        }

        this.costs[i] = Integer.parseInt(br.readLine());

        if (!br.readLine().equals("end_operator")) {
            this.error = 1;
            System.out.println("Error: (SAS+-parser) Unexpected structure of operator element.");
        }
    }

    private void readGoal(BufferedReader br) throws Exception {
        this.numberOfGoalPairs = Integer.parseInt(br.readLine());
        this.goal = new int[this.numberOfGoalPairs][];
        for (int i = 0; i < this.numberOfGoalPairs; i++) {
            String[] pair = br.readLine().trim().split(" ");
            this.goal[i] = new int[2];
            this.goal[i][0] = Integer.parseInt(pair[0]);
            this.goal[i][1] = Integer.parseInt(pair[1]);
        }
    }

    private void readS0(BufferedReader br) throws Exception {
        this.s0 = new int[this.numberOfVars];
        for (int i = 0; i < this.numberOfVars; i++) {
            this.s0[i] = Integer.parseInt(br.readLine());
        }
        if (!br.readLine().equals("end_state")) {
            this.error = 1;
            System.out.println("Error: (SAS+-parser) Unexpected structure of (initial) state element.");
        }
    }

    private void readMutex(BufferedReader br, int i) throws Exception {
        while (!br.readLine().equals("begin_variable"))
            br.readLine();
    }

    private void readVariable(BufferedReader br, int i) throws Exception {
        if (!br.readLine().equals("begin_variable")) {
            this.error = 1;
            System.out.println("Error: (SAS+-parser) Unexpected structure of variable element.");
        }
        varNames[i] = br.readLine();
        axioms[i] = Integer.parseInt(br.readLine());
        ranges[i] = Integer.parseInt(br.readLine());

        values[i] = new String[ranges[i]];
        for (int j = 0; j < ranges[i]; j++) {
            String val = br.readLine();
            if (val.equals(noVal)) {
                values[i][j] = val;
            } else {
                if (!val.startsWith("Atom ")) {
                    this.error = 1;
                    System.out.println("Error: (SAS+-parser) Unexpected structure of variable element.");
                }
                values[i][j] = val.substring("Atom ".length(), val.length());
            }
        }
        if (!br.readLine().equals("end_variable")) {
            this.error = 1;
            System.out.println("Error: (SAS+-parser) Unexpected structure of variable element.");
        }
    }

    private void readMetric(BufferedReader br) throws Exception {
        String v = br.readLine();
        int m = Integer.parseInt(v);
        if (m == 1)
            this.actionCosts = true;
        if ((m < 0) || (m > 1) || (!br.readLine().equals("end_metric"))) {
            this.error = 1;
            System.out.println("Error: (SAS+-parser) Unexpected structure of metric element.");
        }

    }

    private void readVersion(BufferedReader br) throws Exception {
        String v = br.readLine();
        this.version = Integer.parseInt(v);
        if (!br.readLine().equals("end_version")) {
            this.error = 1;
            System.out.println("Error: (SAS+-parser) Unexpected structure of version element.");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("Version: ");
        sb.append(this.version);

        sb.append("\n\nMetric: ");
        if (!this.actionCosts)
            sb.append("no ");
        sb.append("action costs\n\n");

        for (int i = 0; i < numberOfVars; i++) {
            sb.append(varNames[i]);
            sb.append(" {");
            for (int j = 0; j < ranges[i]; j++) {
                if (j > 0)
                    sb.append("; ");
                sb.append(values[i][j]);
            }
            sb.append("}\n\n");
        }

        sb.append("Skipped mutex groups\n\n");

        sb.append("s0 {");
        for (int i = 0; i < numberOfVars; i++) {
            if (i > 0)
                sb.append("; ");
            sb.append(varNames[i]);
            sb.append("=");
            sb.append(values[i][s0[i]]);
        }
        sb.append("}\n\n");

        sb.append("g {");
        for (int i = 0; i < numberOfGoalPairs; i++) {
            if (i > 0)
                sb.append("; ");
            sb.append(varNames[goal[i][0]]);
            sb.append("=");
            sb.append(values[goal[i][0]][goal[i][1]]);
        }
        sb.append("}\n\n");

        for (int i = 0; i < numberOfOperators; i++) {
            sb.append(opNames[i]);
            sb.append(" {\n");
            sb.append("Prevail-Cond: {");
            for (int j = 0; j < numPrevailConditions[i]; j++) {
                if (j > 0)
                    sb.append("; ");
                sb.append(varNames[prevailConditions[i][j][0]]);
                sb.append("=");
                sb.append(values[prevailConditions[i][j][0]][prevailConditions[i][j][1]]);
            }
            sb.append("}\n");
            sb.append("Precs:        {");
            for (int j = 0; j < numEffects[i]; j++) {
                if (j > 0)
                    sb.append("; ");

                sb.append(varNames[effectVar[i][j]]);
                sb.append("=");
                sb.append(values[effectVar[i][j]][effectVarPrec[i][j]]);
            }
            sb.append("}\n");
            sb.append("Effects:      {");
            for (int j = 0; j < numEffects[i]; j++) {
                if (j > 0)
                    sb.append("; ");

                sb.append(varNames[effectVar[i][j]]);
                sb.append("=");
                sb.append(values[effectVar[i][j]][effectVarEff[i][j]]);
            }
            sb.append("}\n");
            sb.append("Costs:        ");
            sb.append(costs[i]);
            sb.append("\n");
            sb.append("}\n\n");
        }

        return sb.toString();
    }
}
