package de.uniulm.ki.panda3.symbolic.compiler.prefix;


import de.uniulm.ki.panda3.symbolic.compiler.ToPlainFormulaRepresentation;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.ioInterface.FileHandler;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import scala.Tuple2;

public class PANDAaddPrefix {

    private static final boolean debugMode = true;

    private static String domain_targetPath = null;
    private static String problem_targetPath = null;

    private static String[][] prefix;
    private static String[][] process;

    public static void main(String[] args) {
        System.out.println("[START]\tRepair-Transformation");

        // DebugOnly-Step
        if (debugMode) {
            System.out.println("[INFO]\tDebug mode activated.");
        }

        // DebugOnly-Step
        if (debugMode) {
            String tempRunUUID = java.util.UUID.randomUUID().toString();
            System.out.println("[DEBUG]\tSet default call arguments.");

            args = new String[]{"domain",
                    "src/test/resources/de/uniulm/ki/panda3/symbolic/compiler/monroe.lisp",
                    "/home/dhoeller/Schreibtisch/temp/monroe-d" + tempRunUUID + ".lisp",
                    "problem",
                    "src/test/resources/de/uniulm/ki/panda3/symbolic/compiler/p-0001-clear-road-wreck.lisp",
                    "/home/dhoeller/Schreibtisch/temp/monroe-d" + tempRunUUID + ".lisp",
/*                    "process",
                    "-Connects",
                    "Stuttart_London_Air_Route Stuttgart London",*/
                    "prefix",
                    //"navegate-vehicle climb-in",
                    "navegate-vehicle navegate-vehicle climb-in",
                    "wcrew1 wtruck1 brighton-dump texaco1",
                    "wcrew1 wtruck1 brighton-dump texaco1",
                    "tcrew1 wtruck1 brighton-dump",
                    "end"};
        }

        prefix = null;
        process = null;

        try {
            Tuple2<Domain, Plan> domPlan = parseCallArguments(args);
            PrefixTransformer prefixTransformer;
            if (process == null)
                prefixTransformer = PrefixTransformer.getRecognitionTransformer(prefix);
            else
                prefixTransformer = PrefixTransformer.getRepairTransformer(prefix, process);

            domPlan = prefixTransformer.transform(domPlan._1(), domPlan._2(), null);

            // transformation introduces nasty structures that should be removed
            domPlan = ToPlainFormulaRepresentation.transform(domPlan);

            String toDomainFile = "/home/dhoeller/Schreibtisch/temp/dom.lisp";
            String toProblemFile = "/home/dhoeller/Schreibtisch/temp/prob.lisp";
            FileHandler.writeHPDDLToFiles(domPlan, toDomainFile, toProblemFile);

        } catch (addPrefixException error) {
            System.out.println("[ABORT]\tRepairing plan failed due to PlanRepairException: " + error.getMessage());
            error.printStackTrace();
            return;
        } catch (Exception error) {
            System.out.println("[ABORT]\tRepairing plan failed due to Exception: " + error.getMessage());
            error.printStackTrace();
            return;
        }

        System.out.println("[END]\tRepair-Transformation");
    }

    private static Tuple2<Domain, Plan> parseCallArguments(String[] lines) throws Exception {
        String domain_sourcePath = "";
        String problem_sourcePath = "";

        String[][] prefixPrimitiveTasks_Name_ArgumentValues = null;
        String[][] processLiterals_Name_ArgumentValues = null;

        boolean endLoop = false;

        for (int a = 0; a < lines.length && !endLoop; a++) {
            switch (lines[a]) {
                case "domain":
                    a++;
                    domain_sourcePath = lines[a];
                    a++;
                    domain_targetPath = lines[a];
                    break;
                case "problem":
                    a++;
                    problem_sourcePath = lines[a];
                    a++;
                    problem_targetPath = lines[a];
                    break;
                case "process":
                    a++;
                    String[] tempParts_1 = lines[a].split(" ");
                    processLiterals_Name_ArgumentValues = new String[tempParts_1.length][];
                    for (int b = 0; b < tempParts_1.length; b++) {
                        a++;
                        String[] tempSubParts_1 = lines[a].equals("") ? new String[0] : lines[a].split(" ");
                        processLiterals_Name_ArgumentValues[b] = new String[tempSubParts_1.length + 1];
                        processLiterals_Name_ArgumentValues[b][0] = tempParts_1[b];
                        for (int c = 0; c < tempSubParts_1.length; c++) {
                            processLiterals_Name_ArgumentValues[b][c + 1] = tempSubParts_1[c];
                        }
                    }
                    break;
                case "prefix":
                    a++;
                    String[] tempParts_2 = lines[a].split(" ");
                    prefixPrimitiveTasks_Name_ArgumentValues = new String[tempParts_2.length][];
                    for (int b = 0; b < tempParts_2.length; b++) {
                        a++;
                        String[] tempSubParts_2 = lines[a].equals("") ? new String[0] : lines[a].split(" ");
                        prefixPrimitiveTasks_Name_ArgumentValues[b] = new String[tempSubParts_2.length + 1];
                        prefixPrimitiveTasks_Name_ArgumentValues[b][0] = tempParts_2[b];
                        for (int c = 0; c < tempSubParts_2.length; c++) {
                            prefixPrimitiveTasks_Name_ArgumentValues[b][c + 1] = tempSubParts_2[c];
                        }
                    }
                    break;
                case "end":
                    endLoop = true;
                    break;
                default:
                    throw new addPrefixException("Invalid command '" + lines[a] + "'.");
            }
        }

        prefix = prefixPrimitiveTasks_Name_ArgumentValues;
        process = processLiterals_Name_ArgumentValues;

        if (!endLoop) {
            System.out.println("[Warning]\t'end' expected.");
        }

        return FileHandler.loadHDDLFromFile(domain_sourcePath, problem_sourcePath);
    }
}
