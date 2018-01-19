package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.htn.GroundedProgressionHeuristic;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.CascadingTables;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.ClassicalMSGraph;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.ClassicalMergeAndShrink;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.Utils;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.NodeValue;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.StratificationPlotter;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.StratificationPlotter$;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.util.DirectedGraph;
import de.uniulm.ki.util.Dot2PdfCompiler;
import de.uniulm.ki.util.Dot2PdfCompiler$;
import de.uniulm.ki.util.EdgeLabelledGraph;
import scala.collection.JavaConverters;
import scala.collection.Map;
import scala.concurrent.JavaConversions;
import scala.runtime.AbstractFunction1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
public class HierarchicalMergeAndShrink extends GroundedProgressionHeuristic {

    HashMap<Task, List<ProMethod>> methods;
    List<ProgressionPlanStep> initialTasks;


    public HierarchicalMergeAndShrink(SasPlusProblem flatProblem, HashMap<Task, List<ProMethod>> methods, List<ProgressionPlanStep> initialTasks, Domain domain) {
        super();

        this.methods = methods;
        this.initialTasks = initialTasks;


        ClassicalMergeAndShrink classicalMergeAndShrink = new ClassicalMergeAndShrink(flatProblem);
        ClassicalMSGraph testGraph = classicalMergeAndShrink.mergeAndShrinkProcess(flatProblem, 5000);

        Utils.printMultiGraph(flatProblem, testGraph, "htnGraph.pdf");


        Task[] allTasks = ProgressionNetwork.indexToTask;


        //var i = 0
        DirectedGraph<?> layerGraph = domain.taskSchemaTransitionGraph().condensation();
        Dot2PdfCompiler$.MODULE$.writeDotToFile(layerGraph, "decomp_hierarchy.pdf");

        List<?> layer = JavaConverters.seqAsJavaList(layerGraph.topologicalOrdering().get().reverse());

        for (Object l : layer) {
            Set<Task> tasksInLayer = (Set<Task>) JavaConverters.setAsJavaSet((scala.collection.immutable.Set) l);
            System.out.println("Layer: " + tasksInLayer);

            for (Task t : tasksInLayer) {
                int taskIndex = ProgressionNetwork.taskToIndex.get(t);
                System.out.println("\tTask: " + t + " Index: " + taskIndex);
                List<ProMethod> methodsForTask = ProgressionNetwork.methods.get(t);
                if (t.isAbstract()) {
                    for (ProMethod pm : methodsForTask) {
                        System.out.println("\t\tMethod: " + pm.m.name());
                        DirectedGraph<PlanStep> methodGraph = pm.m.subPlan().orderingConstraints().fullGraph();
                        System.out.println("\t\t" + methodGraph.);
                    }
                }
            }
        }

        StratificationPlotter$.MODULE$.plotStratification(domain);

        System.exit(0);

    }

    @Override
    public String getName() {
        return "M&S";
    }

    @Override
    public void build(ProgressionNetwork tn) {

    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps, ProMethod m) {
        return null;
    }

    @Override
    public GroundedProgressionHeuristic update(ProgressionNetwork newTN, ProgressionPlanStep ps) {
        return null;
    }

    @Override
    public int getHeuristic() {
        return 0;
    }

    @Override
    public boolean goalRelaxedReachable() {
        return false;
    }
}
