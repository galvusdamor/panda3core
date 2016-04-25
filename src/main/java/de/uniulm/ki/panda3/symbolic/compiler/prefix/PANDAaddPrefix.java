package de.uniulm.ki.panda3.symbolic.compiler.prefix;


import de.uniulm.ki.panda3.symbolic.compiler.SHOPMethodCompiler;
import de.uniulm.ki.panda3.symbolic.compiler.ToPlainFormulaRepresentation;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.ioInterface.FileHandler;
import de.uniulm.ki.panda3.symbolic.plan.Plan;
import scala.Tuple2;

public class PANDAaddPrefix {

    private static final boolean debugMode = false;

    private static String domain_targetPath = null;
    private static String problem_targetPath = null;

    private static String[][] prefix = null;
    private static String[][] process = null;
    private static boolean verify = false;

    public static final String XML = "xml";
    public static final String RON = "ron";
    private static String outFormat = XML;

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
            args = getExampleCall();
        }

        try {
            if (args.length == 0) {
                for (String line : getExampleCall())
                    System.out.print(line + " ");
                return;
            }
            Tuple2<Domain, Plan> domPlan = parseCallArguments(args);

            if (prefix.length > 0) {
                PrefixTransformer prefixTransformer;
                if ((process == null) && (PANDAaddPrefix.verify))
                    prefixTransformer = PrefixTransformer.getVerifyTransformer(prefix);
                else if ((process == null) && (!PANDAaddPrefix.verify))
                    prefixTransformer = PrefixTransformer.getRecognitionTransformer(prefix);
                else
                    prefixTransformer = PrefixTransformer.getRepairTransformer(prefix, process);
                domPlan = prefixTransformer.transform(domPlan._1(), domPlan._2(), null);
            }
            // transformation introduces nasty structures that should be removed
            domPlan = ToPlainFormulaRepresentation.transform(domPlan);
            domPlan = (new forallAndExistsPrecCompiler()).transform(domPlan, null);

            // be sure that this is done AFTER the encoding (otherwise no decomposition will be possible before executing the prefix)
            domPlan = SHOPMethodCompiler.transform(domPlan);

            if (PANDAaddPrefix.outFormat.equals(XML))
                FileHandler.writeXMLToFiles(domPlan, domain_targetPath, problem_targetPath);
            else if (PANDAaddPrefix.outFormat.equals(RON))
                FileHandler.writeHPDDLToFiles(domPlan, domain_targetPath, problem_targetPath);
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

    private static String[] getExampleCall() {
        String base = "/home/dhoeller/Dokumente/repositories/private/evaluation-domains/monroe/problems/exp-ecai/test/";
        return new String[]{"-domain",
                base + "domain.lisp",
                base + "d-0001-clear-road-wreck-0.lisp",
                "-problem",
                base + "p-0001-clear-road-wreck.lisp",
                base + "p-0001-clear-road-wreck-0.lisp",
/*                    "-process",
                "-Connects",
                "Stuttart_London_Air_Route Stuttgart London",*/
                "-prefix",
                "(navegate-vehicle wcrew1 wtruck1 brighton-dump twelve-corners)",
/*                    "navegate-vehicle navegate-vehicle climb-in",
                "wcrew1 wtruck1 brighton-dump texaco1",
                "wcrew1 wtruck1 brighton-dump texaco1",
                "tcrew1 wtruck1 brighton-dump",*/
        };
    }

    private static Tuple2<Domain, Plan> parseCallArguments(String[] lines) throws Exception {
        String domain_sourcePath = "";
        String problem_sourcePath = "";

        for (int a = 0; a < lines.length; a++) {
            switch (lines[a]) {
                case "-domain":
                    a++;
                    domain_sourcePath = lines[a];
                    a++;
                    domain_targetPath = lines[a];
                    break;
                case "-problem":
                    a++;
                    problem_sourcePath = lines[a];
                    a++;
                    problem_targetPath = lines[a];
                    break;
                case "-process":
                    a++;
                    String[] tempParts_1 = lines[a].split(" ");
                    process = new String[tempParts_1.length][];
                    for (int b = 0; b < tempParts_1.length; b++) {
                        a++;
                        String[] tempSubParts_1 = lines[a].equals("") ? new String[0] : lines[a].split(" ");
                        process[b] = new String[tempSubParts_1.length + 1];
                        process[b][0] = tempParts_1[b];
                        for (int c = 0; c < tempSubParts_1.length; c++) {
                            process[b][c + 1] = tempSubParts_1[c];
                        }
                    }
                    break;
                case "-prefix":
                    readPrefix(lines[++a]);
                    break;
                case "-verify":
                    PANDAaddPrefix.verify = true;
                    readPrefix(lines[++a]);
                    break;
                case "-outFormat":
                    String format = lines[++a];
                    if (format.equals(RON))
                        PANDAaddPrefix.outFormat = RON;
                    break;
                default:
                    throw new addPrefixException("Invalid command '" + lines[a] + "'.");
            }
        }

        return FileHandler.loadHDDLFromFile(domain_sourcePath, problem_sourcePath);
    }

    private static void readPrefix(String prefixStr) {
        // want to match: "(climb-in a b)(navegate-vehicle b t g) (someother f t),(test z p), (other a w)"
        String[] prefixSplit = new String[0];
        if (prefixStr.length() > 0) {
            prefixSplit = prefixStr.substring(1, prefixStr.length() - 1).split("(\\)\\(|\\) \\(|\\)\\,\\(|\\)\\, \\()");
        }
        prefix = new String[prefixSplit.length][];
        for (int i = 0; i < prefixSplit.length; i++) {
            String[] action = prefixSplit[i].split(" ");
            prefix[i] = action;
        }
    }
}
