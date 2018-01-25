package de.uniulm.ki.panda3.progression.heuristics.htn.mergeAndShrink;

import de.uniulm.ki.panda3.progression.heuristics.htn.GroundedProgressionHeuristic;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.ClassicalMSGraph;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.ClassicalMergeAndShrink;
import de.uniulm.ki.panda3.progression.heuristics.sasp.mergeAndShrink.Utils;
import de.uniulm.ki.panda3.progression.htn.representation.ProMethod;
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionNetwork;
import de.uniulm.ki.panda3.progression.htn.search.ProgressionPlanStep;
import de.uniulm.ki.panda3.progression.sasp.mergeAndShrink.StratificationPlotter$;
import de.uniulm.ki.panda3.symbolic.domain.Domain;
import de.uniulm.ki.panda3.symbolic.domain.Task;
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep;
import de.uniulm.ki.util.DirectedGraph;
import de.uniulm.ki.util.Dot2PdfCompiler$;
import scala.collection.JavaConverters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        Utils.printMultiGraph(flatProblem, testGraph, "ClassicalMSGraph.pdf");


        Task[] allTasks = ProgressionNetwork.indexToTask;

        for (int i=0; i<allTasks.length;i++) {

            Task t = allTasks[i];

            System.out.println("\tTask: " + i + ": " + t.shortInfo());
        }


        //var i = 0
        DirectedGraph<?> layerGraph = domain.taskSchemaTransitionGraph().condensation();
        Dot2PdfCompiler$.MODULE$.writeDotToFile(layerGraph, "decomp_hierarchy.pdf");

        List<?> layer = JavaConverters.seqAsJavaList(layerGraph.topologicalOrdering().get().reverse());

        for (Object l : layer) {
            Set<Task> tasksInLayer = (Set<Task>) JavaConverters.setAsJavaSet((scala.collection.immutable.Set) l);
            //System.out.println("Layer: " + tasksInLayer);

            for (Task t : tasksInLayer) {
                int taskIndex = ProgressionNetwork.taskToIndex.get(t);
                //System.out.println("\tTask: " + taskIndex + ": " + t.shortInfo());
                //System.out.println("\tTask: " + t + " Index: " + taskIndex);
                List<ProMethod> methodsForTask = ProgressionNetwork.methods.get(t);
                if (t.isAbstract()) {
                    for (ProMethod pm : methodsForTask) {
                        //System.out.println("\t\tMethod: " + pm.m.name());
                        DirectedGraph<PlanStep> methodGraph = pm.m.subPlan().orderingConstraints().fullGraph();
                         //System.out.println("\t\t" + methodGraph);
                    }
                }
            }
        }

        StratificationPlotter$.MODULE$.plotStratification(domain);

        /*HashMap<Integer,HtnMsGraph> presentGraphs = new HashMap<>();

        presentGraphs = Merging.getHtnMsGraphForTaskIndex(flatProblem, methods, 0, presentGraphs);

        presentGraphs = Merging.getHtnMsGraphForTaskIndex(flatProblem, methods, 1, presentGraphs);

        presentGraphs = Merging.getHtnMsGraphForTaskIndex(flatProblem, methods, 2, presentGraphs);

        presentGraphs = Merging.getHtnMsGraphForTaskIndex(flatProblem, methods, 3, presentGraphs);

        presentGraphs = Merging.getHtnMsGraphForTaskIndex(flatProblem, methods, 6, presentGraphs);

        presentGraphs = Merging.getHtnMsGraphForTaskIndex(flatProblem, methods, 11, presentGraphs);

        presentGraphs = Merging.getHtnMsGraphForTaskIndex(flatProblem, methods, 10, presentGraphs);*/

        HashMap<Integer,HtnMsGraph> presentGraphs = getAllGraphs(flatProblem, methods, domain);

        Utils.printAllHtnGraphs(flatProblem, presentGraphs);


        System.exit(0);

    }


    public HashMap<Integer,HtnMsGraph> getAllGraphs(SasPlusProblem p, HashMap<Task, List<ProMethod>> methods, Domain domain){

        HashMap<Integer,HtnMsGraph> presentGraphs = new HashMap<>();

        Map<Task, Integer> taskToIndexMapping = ProgressionNetwork.taskToIndex;

        DirectedGraph<?> layerGraph = domain.taskSchemaTransitionGraph().condensation();

        List<?> layer = JavaConverters.seqAsJavaList(layerGraph.topologicalOrdering().get().reverse());

        for (Object l : layer) {
            Set<Task> tasksInLayer = (Set<Task>) JavaConverters.setAsJavaSet((scala.collection.immutable.Set) l);
            //System.out.println("Layer: " + tasksInLayer);

            for (Task t : tasksInLayer) {

                int taskIndex = taskToIndexMapping.get(t);

                System.out.println("Handle Task " + taskIndex);

                presentGraphs = Merging.getHtnMsGraphForTaskIndex(p, methods, taskIndex, presentGraphs);

                System.out.println("Tasks in present Tasks: " + presentGraphs.keySet());

            }
        }

        return presentGraphs;

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
