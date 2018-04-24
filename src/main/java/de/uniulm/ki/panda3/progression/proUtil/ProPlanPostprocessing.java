package de.uniulm.ki.panda3.progression.proUtil;

import java.util.BitSet;
import java.util.List;

/**
 * Created by dh on 15.09.16.
 */
public class ProPlanPostprocessing {

    public static void inferCausalLinks(BitSet s0, List<Integer> solution) {
        /*List<Integer> sol = new LinkedList<>(); // copy array to keep changes local to the function
        sol.addAll(solution);
        sol.add(0, -1); // add dummy to simplify index-magic

        for (int consumerI = 1; consumerI < sol.size(); consumerI++) {
            int consumer = sol.get(consumerI);
            int fact = operators.prec[consumer].nextSetBit(0);
            boolean found = false;
            while (fact >= 0) {
                // Bit lookup tables
                BitSet relevantAdds = new BitSet(sol.size());
                if (s0.get(fact))
                    relevantAdds.set(0);
                BitSet relevantDels = new BitSet(sol.size());
                for (int ai = 1; ai < sol.size(); ai++) {
                    int a = sol.get(ai);
                    if (operators.add[a].get(fact))
                        relevantAdds.set(ai);
                    if (operators.del[a].get(fact))
                        relevantDels.set(ai);
                }
                int offset = 0;
                while (offset < consumerI) { // the equality is due to the first bit in the vectors that represents s0
                    int supporterI = relevantAdds.nextSetBit(offset);
                    boolean nothreat = ((supporterI == (consumerI - 1)) || (relevantDels.get(supporterI + 1, consumerI - 1).nextSetBit(0) == -1));
                    if (nothreat) {
                        System.out.println(supporterI + " -> " + consumerI + " " + proPrinter.literalToStr(operators.IndexToLiteral[fact]));
                        found = true;
                        if (supporterI > 0) { // only when the support is NOT s0
                            BitSet preds = relevantDels.get(0, supporterI - 1);
                            int predI = preds.nextSetBit(0);
                            while (predI >= 0) {
                                System.out.println(fact + " < " + supporterI);
                                predI = preds.nextSetBit(fact + 1);
                            }
                        }
                        BitSet succs = relevantDels.get(consumerI + 1, relevantDels.size() - 1);
                        int succI = succs.nextSetBit(0);
                        while (succI >= 0) {
                            System.out.println(consumerI + " < " + fact);
                            succI = succs.nextSetBit(succI + 1);
                        }
                        break;
                    } else
                        offset = relevantDels.nextSetBit(supporterI + 1) + 1;
                }
                fact = operators.prec[consumer].nextSetBit(fact + 1);
            }
            if (!found) {
                System.out.println("There is no supporter for a precondition");
            }
        }*/
    }

    private static boolean noThreat(List<Integer> solution, int producer, int consumer, int effI) {
        /*for (int i = producer + 1; i < consumer; i++) {
            int action = solution.get(i);
            if (operators.del[action].get(effI)) {
                return false;
            }
        }
        return true;*/
        return false;
    }
}
