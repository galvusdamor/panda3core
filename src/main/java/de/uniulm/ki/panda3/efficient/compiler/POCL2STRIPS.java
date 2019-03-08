package de.uniulm.ki.panda3.efficient.compiler;

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain;
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan;
import scala.Tuple2;

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
public class POCL2STRIPS {

    public static Tuple2<EfficientDomain, EfficientPlan> compile(EfficientDomain d, EfficientPlan p) {

        EfficientDomain newD = new EfficientDomain(
                d.subSortsForSort(),
                d.sortsOfConstant(),
                d.predicates(),
                d.tasks(),
                d.decompositionMethods(),
                d.sasPlusProblem(),
                d.taskIndexToSASPlus(),
                d.predicateIndexToSASPlus());


        return new Tuple2<EfficientDomain, EfficientPlan>(newD, p);
    }

}