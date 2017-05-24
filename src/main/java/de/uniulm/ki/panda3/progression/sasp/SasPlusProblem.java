package de.uniulm.ki.panda3.progression.sasp;

import de.uniulm.ki.panda3.symbolic.domain.Task;
import scala.Tuple2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.*;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Daniel Höller on 24.02.17.
 */
public class SasPlusProblem {
    /**
     * Class to use Malte Helmert's SAS+ format.
     * <p>
     * Please be aware that there are several domain transformations that make our representation
     * differ from the original one -- these maintain *all (and only) the public* fields of the
     * class.
     * <p>
     * see: http://www.fast-downward.org/TranslatorOutputFormat
     */
    private static String noVal = "<none of those>";
    private static String negation = "not->";

    private int version;
    private int error = 0;
    private boolean actionCosts = false;

    // number of elements
    private int numOfVars;
    private int numOfMutexGroups;
    private int numOfGoalPairs;
    public int numOfOperators;

    private int[] axioms;
    public int[] ranges; // variable-index -> number of values the var can have
    protected String[] varNames; // variable-index ->  name-string of the var
    protected String[][] values; // var-index, value-index -> value-string (this is either an atom from the domain, or the noVal-String)

    private int[] s0; // variable-index -> value set in s0
    private int[][] goal; // enum of pairs [var-index, value-needed]

    public String[] opNames; // operator-index -> name-string

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
    private int[][] numOfEffectConditions; // operator-index, effect-index -> number of effect-conditions
    private int[][][] effectConditions; // operator-index, effect-index -> pair [variable-index, value]

    /* effects
     * - some variable needs to have value before-hand and gets a new value afterwards
     * - i.e. in a STRIPS manner, the following lines include preconditions as well as effects
     */
    private int[] numEffects; // operator-index -> number of effects
    private int[][] effectVar; // operator-index, effect-index -> variable-id
    private int[][] effectVarPrec; // operator-index, effect-index -> old var index (a precondition)
    private int[][] effectVarEff;  // operator-index, effect-index -> new var index (the effect)

    public int[] costs; // operator-index -> action costs

    // ********************
    //  our representation
    // ********************
    public int numOfStateFeatures; // num of features used in our representation
    public int[] firstIndex; // maps a mutex-group-index to the first index in the vector of state features
    public int[] lastIndex; // maps a mutex-group-index to the last index in the vector of state features
    public int[] indexToMutexGroup; // maps some feature-index to the corresponding mutex group

    // maps an action-index to an array of preconditions. The array contains the indices of state features that need
    // to be set
    public int[][] precLists;

    // maps an action-index to an array of add-effects. The array contains the indices of state features that are
    // added
    public int[][] addLists;

    // maps an action-index to an array of del-effects. The array contains the indices of state features that are
    // deleted
    public int[][] delLists;

    // the following del-list contains all literals that are mutex to the one that is set as add effect, i.e. it
    // contains (1) the same del-effects as the list before or (2) more -> use in POCL-planning to get more threats
    public int[][] expandedDelLists;

    public int[][] precToTask; // 1 -> [4, 5, 7] means that the tasks 4, 5 and 7 all have precondition 1
    public int[][] addToTask; // 1 -> [4, 5, 7] means that the tasks 4, 5 and 7 all add fact no. 1
    public int[] numPrecs; // gives the number of preconditions for each action

    public int[] s0List;
    private BitSet s0Bitset = null;
    public int[] gList;

    public String[] factStrs;

    public BitSet getS0() {
        if (s0Bitset == null) {
            s0Bitset = new BitSet(numOfStateFeatures);
            for (int f : s0List)
                s0Bitset.set(f);
        }
        return s0Bitset;
    }

    public String factName(int i) {
        return factStrs[i];
    }


    public String printOp(int i) {
        String res = "";
        res += opNames[i];
        res += printFactList(precLists[i]);
        res += printFactList(addLists[i]);
        res += printFactList(delLists[i]);
        return res;
    }

    private String printFactList(int[] list) {
        String res = "{";
        for (int i = 0; i < list.length; i++) {
            if (i > 0)
                res += ", ";
            res += factName(list[i]);
        }
        return res + "}";
    }

    public void prepareEfficientRep() {
        numOfStateFeatures = 0;
        firstIndex = new int[numOfVars];
        lastIndex = new int[numOfVars];

        for (int ivar = 0; ivar < numOfVars; ivar++) {
            firstIndex[ivar] = numOfStateFeatures;
            numOfStateFeatures += ranges[ivar];
            lastIndex[ivar] = numOfStateFeatures - 1;
        }

        indexToMutexGroup = new int[numOfStateFeatures];
        for (int i = 0; i < numOfVars; i++) {
            for (int j = firstIndex[i]; j <= lastIndex[i]; j++) {
                indexToMutexGroup[j] = i;
            }
        }

        // prepare action-related stuff
        precLists = new int[numOfOperators][];
        addLists = new int[numOfOperators][];
        delLists = new int[numOfOperators][];
        expandedDelLists = new int[numOfOperators][];
        numPrecs = new int[numOfOperators];

        for (int i = 0; i < numOfOperators; i++) {
            List<Integer> precList = new ArrayList<>();
            List<Integer> addList = new ArrayList<>();
            List<Integer> delList = new ArrayList<>();

            // proceed prevail conditions
            for (int j = 0; j < numPrevailConditions[i]; j++) {
                int varIndex = prevailConditions[i][j][0];
                int valIndex = prevailConditions[i][j][1];
                precList.add(firstIndex[varIndex] + valIndex);
            }

            // proceed effects
            for (int j = 0; j < numEffects[i]; j++) {
                int varIndex = effectVar[i][j];
                int valFromIndex = effectVarPrec[i][j];
                int valToIndex = effectVarEff[i][j];
                if (valFromIndex > -1) { // the variable needs to have a certain value before the application
                    precList.add(firstIndex[varIndex] + valFromIndex);
                    delList.add(firstIndex[varIndex] + valFromIndex);
                } else { // the variable does NOT need to have a certain value before the application
                    int added = firstIndex[varIndex] + valToIndex;
                    for (int k = firstIndex[varIndex]; k <= lastIndex[varIndex]; k++) {
                        if (k != added) {
                            delList.add(k);
                        }
                    }
                }

                // anyway, the value is set
                addList.add(firstIndex[varIndex] + valToIndex);

                // todo implement conditional effects
                if (this.numOfEffectConditions[i][j] > 0) {
                    this.error = 1;
                    System.out.println("Error: (SAS+ parser) Found conditional effects - this feature is not (yet) supported.");
                }
            }

            numPrecs[i] = precList.size();

            // copy temporal structures to arrays
            this.precLists[i] = new int[precList.size()];
            this.addLists[i] = new int[addList.size()];
            this.delLists[i] = new int[delList.size()];
            for (int j = 0; j < precList.size(); j++) {
                this.precLists[i][j] = precList.get(j);
            }
            for (int j = 0; j < addList.size(); j++) {
                this.addLists[i][j] = addList.get(j);
            }
            for (int j = 0; j < delList.size(); j++) {
                this.delLists[i][j] = delList.get(j);
            }
        }

        calcInverseMappings();
        calcExtendedDelLists();

        s0List = new int[numOfVars];
        for (int i = 0; i < numOfVars; i++) {
            s0List[i] = firstIndex[i] + s0[i];
        }

        gList = new int[goal.length];
        for (int i = 0; i < goal.length; i++) {
            gList[i] = firstIndex[goal[i][0]] + goal[i][1];
        }

        factStrs = new String[numOfStateFeatures];
        int newI = 0;
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                factStrs[newI++] = varNames[i] + "=" + values[i][j];
            }
        }
    }

    public SasPlusProblem() {
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
        this.numOfEffectConditions = new int[this.numOfOperators][];
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
        numOfEffectConditions[i] = new int[this.numEffects[i]];
        effectVar[i] = new int[this.numEffects[i]];
        effectVarPrec[i] = new int[this.numEffects[i]]; // this may be -1, which means that there is no specific value needed
        effectVarEff[i] = new int[this.numEffects[i]];

        for (int j = 0; j < this.numEffects[i]; j++) {
            String[] eff = br.readLine().split(" ");
            int k = 0;
            this.numOfEffectConditions[i][j] = Integer.parseInt(eff[k++]);
            for (int l = 0; l < this.numOfEffectConditions[i][j]; l++) {
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
        while (!br.readLine().equals("end_mutex_group"))
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
                if (val.startsWith("Atom ")) {
                    values[i][j] = val.substring("Atom ".length(), val.length());
                } else if (val.startsWith("NegatedAtom ")) {
                    values[i][j] = negation + val.substring("NegatedAtom ".length(), val.length());
                } else {
                    values[i][j] = val;
                    this.error = 1;
                    System.out.println("Error: (SAS+ parser) Unexpected structure of variable element.");
                }
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
        String overall = "";
        for (int i = 0; i < this.numOfOperators; i++) {
            String opStr = opNames[i] + "\n\tp{";
            opStr += listToString(precLists[i]) + "}\n\ta{";
            opStr += listToString(addLists[i]) + "}\n\td{";
            opStr += listToString(delLists[i]) + "}";
            overall += opStr + "\n";
        }
        return overall;
    }

    private String listToString(int[] precList) {
        String l = "";
        for (int i = 0; i < precList.length; i++) {
            if (i > 0)
                l += ", ";
            l += factStrs[precList[i]];
        }
        return l;
    }

    public String orgSasToString() {
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
                if (effectVarPrec[i][j] != -1)
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

    public String[] getGroundedOperatorSignatures() {
        return opNames;
    }

    public Tuple2<Map<Integer, Task>, Map<Task, Integer>> restrictTo(Set<Integer> I, Map<Integer, Task> i2t) {
        assert this.correctModel();

        int actionCount = I.size();
        System.out.print("Reducing SAS+ domain ... ");
        int orgOpNum = this.numOfOperators;
        int orgStateNum = this.numOfStateFeatures;

        int[] actionNewToOld = new int[actionCount];
        Map<Task, Integer> taskToIndex = new HashMap<>();
        Map<Integer, Task> indexToTask = new HashMap<>();
        Set<Integer> usedFacts = new HashSet<>();

        // calculate new action indices
        Iterator<Integer> iter = I.iterator();
        int newIndex = 0;
        while (iter.hasNext()) {
            int oldIndex = iter.next();
            actionNewToOld[newIndex] = oldIndex;
            for (int f : precLists[oldIndex])
                usedFacts.add(f);
            for (int f : addLists[oldIndex])
                usedFacts.add(f);
            for (int f : delLists[oldIndex])
                usedFacts.add(f);
            indexToTask.put(newIndex, i2t.get(oldIndex));
            taskToIndex.put(i2t.get(oldIndex), newIndex);
            newIndex++;
        }

        // calculate new fact indices
        Iterator<Integer> iter2 = usedFacts.iterator();
        int[] usedFactsOrdered = new int[usedFacts.size()];
        for (int i = 0; i < usedFacts.size(); i++) {
            usedFactsOrdered[i] = iter2.next();
        }
        java.util.Arrays.sort(usedFactsOrdered);

        // shorten mutex groups
        int[] indexToMutexGroupNew = new int[usedFacts.size()];
        int[] factOldToNew = new int[numOfStateFeatures];
        int[] factNewToOld = new int[usedFacts.size()];

        for (int i = 0; i < usedFactsOrdered.length; i++) {
            factOldToNew[usedFactsOrdered[i]] = i;
            factNewToOld[i] = usedFactsOrdered[i];
            indexToMutexGroupNew[i] = indexToMutexGroup[usedFactsOrdered[i]];
        }

        // translate prec, add and del lists
        int[][] precListsNew = new int[actionCount][];
        int[][] addListsNew = new int[actionCount][];
        int[][] delListsNew = new int[actionCount][];
        int[] numprecsNew = new int[actionCount];
        int[] costsNew = new int[actionCount];
        for (int iA = 0; iA < actionNewToOld.length; iA++) {
            int[] precOld = precLists[actionNewToOld[iA]];
            int[] addOld = addLists[actionNewToOld[iA]];
            int[] delOld = delLists[actionNewToOld[iA]];

            int[] precNew = new int[precOld.length];
            int[] addNew = new int[addOld.length];
            int[] delNew = new int[delOld.length];

            for (int i = 0; i < precOld.length; i++) {
                precNew[i] = factOldToNew[precOld[i]];
            }
            for (int i = 0; i < addOld.length; i++) {
                addNew[i] = factOldToNew[addOld[i]];
            }
            for (int i = 0; i < delOld.length; i++) {
                delNew[i] = factOldToNew[delOld[i]];
            }
            numprecsNew[iA] = numPrecs[actionNewToOld[iA]];
            costsNew[iA] = costs[actionNewToOld[iA]];

            precListsNew[iA] = precNew;
            addListsNew[iA] = addNew;
            delListsNew[iA] = delNew;
        }
        numPrecs = numprecsNew;
        costs = costsNew;

        List<Integer> tempList = new ArrayList<>();
        for (int i = 0; i < s0List.length; i++)
            if (usedFacts.contains(s0List[i]))
                tempList.add(factOldToNew[s0List[i]]);

        int[] s0ListNew = new int[tempList.size()];
        for (int i = 0; i < s0ListNew.length; i++)
            s0ListNew[i] = tempList.get(i);

        tempList.clear();
        for (int i = 0; i < gList.length; i++)
            if (usedFacts.contains(gList[i]))
                tempList.add(factOldToNew[gList[i]]);

        int[] gListNew = new int[tempList.size()];
        for (int i = 0; i < gListNew.length; i++)
            gListNew[i] = tempList.get(i);

        numOfStateFeatures = usedFacts.size();
        numOfOperators = actionCount;
        indexToMutexGroup = indexToMutexGroupNew;
        calcMutexGroupIndices();
        calcRanges();

        precLists = precListsNew;
        addLists = addListsNew;
        delLists = delListsNew;

        calcInverseMappings();
        calcExtendedDelLists();

        String[] factStrsNew = new String[numOfStateFeatures];
        for (int i = 0; i < numOfStateFeatures; i++) {
            factStrsNew[i] = factStrs[factNewToOld[i]];
        }
        factStrs = factStrsNew;

        String[] opNamesNew = new String[numOfOperators];
        for (int i = 0; i < numOfOperators; i++) {
            opNamesNew[i] = opNames[actionNewToOld[i]];
        }
        opNames = opNamesNew;

        s0List = s0ListNew;
        gList = gListNew;
        s0Bitset = null;

        System.out.println("done.");
        System.out.println(" - Reduced number of operators from " + orgOpNum + " to " + I.size());
        System.out.println(" - Reduced number of state-bits from " + orgStateNum + " to " + usedFacts.size());

        assert this.correctModel();
        return new Tuple2<>(indexToTask, taskToIndex);
    }

    public void calcMutexGroupIndices() {
        List<Integer> firstI = new ArrayList<>();
        List<Integer> lastI = new ArrayList<>();
        int group = -1;
        int groupId = -1;
        for (int i = 0; i < indexToMutexGroup.length; i++) {
            if (indexToMutexGroup[i] != group) {
                firstI.add(i);
                if (group >= 0)
                    lastI.add(i - 1);
                group = indexToMutexGroup[i];
                groupId++;
            }
            indexToMutexGroup[i] = groupId;
        }
        lastI.add(indexToMutexGroup.length - 1);

        firstIndex = new int[firstI.size()];
        for (int i = 0; i < firstI.size(); i++)
            firstIndex[i] = firstI.get(i);

        lastIndex = new int[lastI.size()];
        for (int i = 0; i < lastI.size(); i++)
            lastIndex[i] = lastI.get(i);
        assert (firstIndex.length == lastIndex.length);
    }

    public void calcRanges() {
        ranges = new int[firstIndex.length];
        for (int i = 0; i < firstIndex.length; i++)
            ranges[i] = lastIndex[i] - firstIndex[i] + 1;
    }

    public void calcExtendedDelLists() {
        List<Integer>[] expandedDelListsNew = new List[numOfOperators];
        for (int i = 0; i < numOfOperators; i++) {
            List<Integer> aDelList = new ArrayList<>();
            expandedDelListsNew[i] = aDelList;
            for (int f : delLists[i])
                aDelList.add(f);
            for (int f : addLists[i]) {
                int mutexgroup = indexToMutexGroup[f];
                for (int j = firstIndex[mutexgroup]; j <= lastIndex[mutexgroup]; j++) {
                    if (j != f)
                        aDelList.add(j);
                }
            }
        }

        expandedDelLists = new int[numOfOperators][];
        for (int i = 0; i < numOfOperators; i++) {
            List<Integer> current = expandedDelListsNew[i];
            expandedDelLists[i] = new int[current.size()];
            for (int j = 0; j < current.size(); j++)
                expandedDelLists[i][j] = current.get(j);
        }
    }

    public void calcInverseMappings() {
        HashMap<Integer, Set<Integer>> p2t = new HashMap<>();
        HashMap<Integer, Set<Integer>> a2t = new HashMap<>();
        for (int i = 0; i < numOfStateFeatures; i++) {
            p2t.put(i, new HashSet<>());
            a2t.put(i, new HashSet<>());
        }

        for (int i = 0; i < numOfOperators; i++) {
            for (int f : precLists[i])
                p2t.get(f).add(i);
            for (int f : addLists[i])
                a2t.get(f).add(i);
        }

        this.precToTask = new int[numOfStateFeatures][];
        for (int i = 0; i < numOfStateFeatures; i++) {
            Set<Integer> set = p2t.get(i);
            this.precToTask[i] = new int[set.size()];
            Iterator<Integer> iter = set.iterator();
            for (int j = 0; j < set.size(); j++)
                this.precToTask[i][j] = iter.next();
        }

        this.addToTask = new int[numOfStateFeatures][];
        for (int i = 0; i < numOfStateFeatures; i++) {
            Set<Integer> set = a2t.get(i);
            this.addToTask[i] = new int[set.size()];
            Iterator<Integer> iter = set.iterator();
            for (int j = 0; j < set.size(); j++)
                this.addToTask[i][j] = iter.next();
        }
    }

    public boolean correctModel() {
        assert precLists.length == numOfOperators;
        assert addLists.length == numOfOperators;
        assert delLists.length == numOfOperators;
        assert expandedDelLists.length == numOfOperators;

        for (int i = 0; i < numOfOperators; i++) {
            for (int f : precLists[i]) {
                assert (f < numOfStateFeatures);
                assert (aContains(precToTask[f], i));
            }
            for (int f : addLists[i]) {
                assert (f < numOfStateFeatures);
                assert (aContains(addToTask[f], i));
            }
            for (int f : delLists[i]) {
                assert (f < numOfStateFeatures);
            }
            for (int f : expandedDelLists[i]) {
                assert (f < numOfStateFeatures);
            }
        }

        assert addToTask.length == numOfStateFeatures;
        assert precToTask.length == numOfStateFeatures;
        for (int f = 0; f < numOfStateFeatures; f++) {
            for (int t : precToTask[f]) {
                assert (t < numOfOperators);
                assert (aContains(precLists[t], f));
            }
            for (int t : addToTask[f]) {
                assert (t < numOfOperators);
                assert (aContains(addLists[t], f));
            }
        }

        for (int f : this.s0List)
            assert (f < numOfStateFeatures);

        for (int f : this.gList)
            assert (f < numOfStateFeatures);

        assert (indexToMutexGroup.length == numOfStateFeatures);
        assert (costs.length == numOfOperators);
        assert (opNames.length == numOfOperators);
        assert (factStrs.length == numOfStateFeatures);
        assert (indexToMutexGroup[numOfStateFeatures - 1] == (firstIndex.length - 1));
        assert (firstIndex.length == lastIndex.length);
        return true;
    }

    private boolean aContains(int[] array, int i) {
        for (int a : array)
            if (i == a) return true;
        return false;
    }

    public String ourRepToSaspString() {
        StringBuilder s = new StringBuilder();

        s.append("begin_version\n3\nend_version\n\n");

        s.append("begin_metric\n");
        if (this.actionCosts) s.append("1");
        else s.append("0");
        s.append("\nend_metric\n\n");

        s.append(this.firstIndex.length + "\n"); // number of variables aka mutex groups
        for (int i = 0; i < this.firstIndex.length; i++) {
            s.append("begin_variable\n");
            if (ranges[i] > 1) {
                s.append("var" + (i + 1) + "\n");
                s.append("-1\n"); // axiom layer of the var
                s.append(ranges[i] + "\n");
                for (int j = firstIndex[i]; j <= lastIndex[i]; j++) {
                    s.append("Atom " + factStrs[j].substring(factStrs[j].indexOf("=") + 1) + "\n");
                }
            } else {
                s.append(factStrs[firstIndex[i]].substring(factStrs[firstIndex[i]].indexOf("=") + 1).replaceAll(" ", "_") + "\n");

                s.append("-1\n"); // axiom layer of the var
                s.append("2\n");
                s.append("Atom TRUE()\n");
                s.append("Atom FALSE()\n");
            }
            s.append("end_variable\n\n");
        }

        s.append("0\n\n"); // number of mutex groups

        // initial state
        s.append("begin_state\n");
        BitSet tempS0 = this.getS0();
        for (int i = 0; i < firstIndex.length; i++) {
            if (ranges[i] > 1) {
                int value = tempS0.nextSetBit(firstIndex[i]) - firstIndex[i];
                s.append(value + "\n");
            } else {
                if (tempS0.get(firstIndex[i]))
                    s.append("0\n");
                else
                    s.append("1\n");
            }
        }
        s.append("end_state\n\n");

        // goal state
        s.append("begin_goal\n");
        s.append(this.gList.length + "\n");
        for (int g : this.gList) {
            int var = indexToMutexGroup[g];
            int val = g - firstIndex[var];
            s.append(var + " " + val + "\n");
        }
        s.append("end_goal\n\n");

        // operators
        s.append(numOfOperators + "\n\n");
        for (int i = 0; i < numOfOperators; i++) {
            s.append("begin_operator\n");
            s.append(this.opNames[i] + "\n");

            Set<Integer> affectedVars = new HashSet<>();
            Map<Integer, Integer> precs = new HashMap<>();
            Map<Integer, Integer> adds = new HashMap<>();

            for (int prec : precLists[i]) {
                int var = indexToMutexGroup[prec];
                int val = prec - firstIndex[var];
                precs.put(var, val);
                //System.out.println(varNames[var] + " " + factStrs[prec]);
            }

            for (int add : addLists[i]) {
                int var = indexToMutexGroup[add];
                int val = add - firstIndex[var];
                adds.put(var, val);
                //System.out.println(varNames[var] + " " + factStrs[add]);
            }

            affectedVars.addAll(precs.keySet());
            affectedVars.addAll(adds.keySet());

            List<String> prevail = new ArrayList<>();
            List<String> effects = new ArrayList<>();
            for (int var : affectedVars) {
                if (precs.containsKey(var) && !adds.containsKey(var)) {
                    prevail.add(var + " " + precs.get(var) + "\n");
                } else if (precs.containsKey(var) && adds.containsKey(var)) {
                    effects.add("0 " + var + " " + precs.get(var) + " " + adds.get(var) + "\n");
                } else if (!precs.containsKey(var) && adds.containsKey(var)) {
                    effects.add("0 " + var + " -1 " + adds.get(var) + "\n");
                }
            }
            s.append(prevail.size() + "\n");
            for (String line : prevail)
                s.append(line);
            s.append(effects.size() + "\n");
            for (String line : effects)
                s.append(line);
            s.append(costs[i] + "\n");
            s.append("end_operator\n\n");
        }


        return s.toString();
    }

    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append(" - model has " + this.numOfOperators + " operators\n");
        sb.append(" - model has " + this.numOfStateFeatures + " state features\n");
        sb.append(" - operators' mean count of (pre, add, del) = (" + meanCount(precLists) + ", " + meanCount(addLists) + ", " + meanCount(delLists) + ")\n");
        sb.append(" - effects' mean achiever count: " + meanCount(addLists) + "\n");
        sb.append(" - effects' mean consumer count: " + meanCount(precToTask) + "\n");
        return sb.toString();
    }

    DecimalFormat f = new DecimalFormat("#0.00");

    private String meanCount(int[][] lists) {
        double count = 0;
        for (int[] list : lists)
            count += list.length;
        return f.format(count / this.precLists.length);
    }
}