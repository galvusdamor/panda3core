package de.uniulm.ki.panda3.progression.sasp;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by Daniel Höller on 24.02.17.
 */
public class SasPlusProblem {
    /**
     * Class to parse Malte Helmert's SAS+ format
     * <p>
     * see: http://www.fast-downward.org/TranslatorOutputFormat
     */
    private static String noVal = "<none of those>";

    private int version;
    private int error = 0;
    private boolean actionCosts = false;

    // number of elements
    private int numOfVars;
    private int numOfMutexGroups;
    private int numOfGoalPairs;
    private int numOfOperators;

    public int[] axioms;
    public int[] ranges; // variable-index -> number of values the var can have
    private String[] varNames; // variable-index ->  name-string of the var
    private String[][] values; // var-index, value-index -> value-string (this is either an atom from the domain, or the noVal-String)

    private int[] s0; // variable-index -> value set in s0
    private int[][] goal; // enum of pairs [var-index, value-needed]

    private String[] opNames; // operator-index -> name-string

    /* prevail conditions vs. preconditions vs. effect condition
     *
     * - prevail conditions must hold for the operator to be applicable (aka STRIPS-precondition), but do not show up
     *   in the effect
     * - preconditions describe the value a variable that DO show up in the effect must have (or -1 if there is no
     *   such value) (this is also a STRIPS-precondition)
     * - the effect condition is an precondition of a conditional effect (that might be included in the domain or be
     *   produced by some transformation)
     */

    // prevail conditions
    private int[] numPrevailConditions; // operator-index -> number of prevail-conditions
    private int[][][] prevailConditions; // operator-index, condition-index -> pair [variable-index, value]

    // effect conditions (aka STRIPS-conditional-effects)
    private int[][] numberOfEffectConditions; // operator-index -> number of effect-conditions
    private int[][][] effectConditions; // operator-index, effect-index -> pair [variable-index, value]

    /* effects
     * - some variable needs to have value before-hand and gets a new value afterwards
     * - i.e. in a STRIPS manner, the following lines include preconditions as well as effects
     */
    private int[] numEffects; // operator-index -> number of effects
    private int[][] effectVar; // operator-index, effect-index -> variable-id
    private int[][] effectVarPrec; // operator-index, effect-index -> old var index (a precondition)
    private int[][] effectVarEff;  // operator-index, effect-index -> new var index (the effect)

    private int[] costs; // operator-index -> action costs

    // our representation
    private int numOfStateFeatures; // num of features used in our representation
    private int[] firstIndex; // maps a mutex-group-index to the first index in the vector of state features
    private int[] lastIndex; // maps a mutex-group-index to the last index in the vector of state features

    // translations
    // int -> Object
    // Object -> int
    // action in old model <-> action in new model

    public void prepareInteralRep() {
        numOfStateFeatures = 0;
        firstIndex = new int[numOfVars];
        lastIndex = new int[numOfVars];

        for (int ivar = 0; ivar < numOfVars; ivar++) {
            firstIndex[ivar] = numOfStateFeatures;
            numOfStateFeatures += ranges[ivar];
            lastIndex[ivar] = numOfStateFeatures - 1;
        }
    }

    public SasPlusProblem(String Filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(Filename));
        String line = br.readLine();
        if (!line.equals("begin_version")) {
            this.error = 1;
            System.out.println("Error: (SAS+ parser) Did not find version element.");
            return;
        }
        readVersion(br);
        line = br.readLine();

        if (!line.equals("begin_metric")) {
            this.error = 1;
            System.out.println("Error: (SAS+ parser) Did not find metric element.");
            return;
        }
        readMetric(br);

        // read variable definition
        numOfVars = Integer.parseInt(br.readLine());
        varNames = new String[numOfVars];
        axioms = new int[numOfVars];
        ranges = new int[numOfVars];
        values = new String[numOfVars][];

        for (int i = 0; i < numOfVars; i++) {
            readVariable(br, i);
        }

        // read mutex groups
        numOfMutexGroups = Integer.parseInt(br.readLine());

        for (int i = 0; i < numOfMutexGroups; i++) {
            readMutex(br, i);
        }
        line = br.readLine();

        // read s0 and goal
        if (!line.equals("begin_state")) {
            this.error = 1;
            System.out.println("Error: (SAS+ parser) Did not find (initial) state element.");
            return;
        }
        readS0(br);
        line = br.readLine();

        if (!line.equals("begin_goal")) {
            this.error = 1;
            System.out.println("Error: (SAS+ parser) Did not find goal element.");
            return;
        }
        readGoal(br);
        line = br.readLine();

        // read operators
        this.numOfOperators = Integer.parseInt(br.readLine());
        this.opNames = new String[this.numOfOperators];
        this.numEffects = new int[this.numOfOperators];
        this.numPrevailConditions = new int[this.numOfOperators];
        this.prevailConditions = new int[this.numOfOperators][][];
        this.numberOfEffectConditions = new int[this.numOfOperators][];
        this.effectConditions = new int[this.numOfOperators][][];
        this.effectVar = new int[this.numOfOperators][];
        this.effectVarPrec = new int[this.numOfOperators][];
        this.effectVarEff = new int[this.numOfOperators][];
        this.costs = new int[this.numOfOperators];

        for (int i = 0; i < this.numOfOperators; i++) {
            readOperator(br, i);
        }
    }

    private void readOperator(BufferedReader br, int i) throws Exception {
        if (!br.readLine().equals("begin_operator")) {
            this.error = 1;
            System.out.println("Error: (SAS+ parser) Unexpected structure of operator element.");
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
        effectVarPrec[i] = new int[this.numEffects[i]]; // this may be -1, which means that there is no specific value needed
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
            System.out.println("Error: (SAS+ parser) Unexpected structure of operator element.");
        }
    }

    private void readGoal(BufferedReader br) throws Exception {
        this.numOfGoalPairs = Integer.parseInt(br.readLine());
        this.goal = new int[this.numOfGoalPairs][];
        for (int i = 0; i < this.numOfGoalPairs; i++) {
            String[] pair = br.readLine().trim().split(" ");
            this.goal[i] = new int[2];
            this.goal[i][0] = Integer.parseInt(pair[0]);
            this.goal[i][1] = Integer.parseInt(pair[1]);
        }
    }

    private void readS0(BufferedReader br) throws Exception {
        this.s0 = new int[this.numOfVars];
        for (int i = 0; i < this.numOfVars; i++) {
            this.s0[i] = Integer.parseInt(br.readLine());
        }
        if (!br.readLine().equals("end_state")) {
            this.error = 1;
            System.out.println("Error: (SAS+ parser) Unexpected structure of (initial) state element.");
        }
    }

    private void readMutex(BufferedReader br, int i) throws Exception {
        while (!br.readLine().equals("begin_variable"))
            br.readLine();
    }

    private void readVariable(BufferedReader br, int i) throws Exception {
        if (!br.readLine().equals("begin_variable")) {
            this.error = 1;
            System.out.println("Error: (SAS+ parser) Unexpected structure of variable element.");
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
                    System.out.println("Error: (SAS+ parser) Unexpected structure of variable element.");
                }
                values[i][j] = val.substring("Atom ".length(), val.length());
            }
        }
        if (!br.readLine().equals("end_variable")) {
            this.error = 1;
            System.out.println("Error: (SAS+ parser) Unexpected structure of variable element.");
        }
    }

    private void readMetric(BufferedReader br) throws Exception {
        String v = br.readLine();
        int m = Integer.parseInt(v);
        if (m == 1)
            this.actionCosts = true;
        if ((m < 0) || (m > 1) || (!br.readLine().equals("end_metric"))) {
            this.error = 1;
            System.out.println("Error: (SAS+ parser) Unexpected structure of metric element.");
        }

    }

    private void readVersion(BufferedReader br) throws Exception {
        String v = br.readLine();
        this.version = Integer.parseInt(v);
        if (!br.readLine().equals("end_version")) {
            this.error = 1;
            System.out.println("Error: (SAS+ parser) Unexpected structure of version element.");
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

        for (int i = 0; i < numOfVars; i++) {
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
        for (int i = 0; i < numOfVars; i++) {
            if (i > 0)
                sb.append("; ");
            sb.append(varNames[i]);
            sb.append("=");
            sb.append(values[i][s0[i]]);
        }
        sb.append("}\n\n");

        sb.append("g {");
        for (int i = 0; i < numOfGoalPairs; i++) {
            if (i > 0)
                sb.append("; ");
            sb.append(varNames[goal[i][0]]);
            sb.append("=");
            sb.append(values[goal[i][0]][goal[i][1]]);
        }
        sb.append("}\n\n");

        for (int i = 0; i < numOfOperators; i++) {
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
                if (effectVar[i][j] != -1 && effectVarPrec[i][j] != -1)
                    sb.append(values[effectVar[i][j]][effectVarPrec[i][j]]);
                else
                    sb.append("none-of-them");
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
