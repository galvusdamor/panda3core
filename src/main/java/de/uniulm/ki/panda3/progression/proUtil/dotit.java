package de.uniulm.ki.panda3.progression.proUtil;

import scala.Tuple2;
import scala.Tuple3;

import java.util.List;

/**
 * Created by dhoeller on 27.07.16.
 */
public class dotit {
    public static String dotit2(List<List<dotNode>> cluster, List<Tuple2<dotNode, dotNode>> arcs) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n" +
                "  rankdir=LR;\n");

        for (int i = 0; i < cluster.size(); i++) {
            List<dotNode> c = cluster.get(i);
            sb.append("  subgraph cluster_" + i + " {\n");

            String layerName;
            if (i % 2 == 0)
                layerName = "Fact-Layer" + (i / 2);
            else
                layerName = "Operator-Layer" + (i / 2 + 1);

            sb.append("    label = \" " + layerName + " \";\n");

            for (dotNode n : c) {
                sb.append("      node[label=\"" + n.visibleName + "\"] nodeNo" + n.interalID + ";\n");
            }

            sb.append("  }\n"); // of cluster
        }

        for (Tuple2<dotNode, dotNode> a : arcs) {
            sb.append("  nodeNo" + a._1().interalID + " -> nodeNo" + a._2().interalID + ";\n");
        }

        sb.append("}");
        return sb.toString();
    }

    public static String dotit3(List<List<dotNode>> cluster, List<Tuple3<dotNode, dotNode, Integer>> arcs) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n" +
                "  rankdir=LR;\n");

        for (int i = 0; i < cluster.size(); i++) {
            List<dotNode> c = cluster.get(i);
            sb.append("  subgraph cluster_" + i + " {\n");

            String layerName;
            if (i % 2 == 0)
                layerName = "Fact-Layer";
            else
                layerName = "Operator-Layer";
            sb.append("    label = \" " + layerName + " " + i + "\";\n");

            for (dotNode n : c) {
                sb.append("      node[label=\"" + n.visibleName + "\"] nodeNo" + n.interalID + ";\n");
            }

            sb.append("  }\n"); // of cluster
        }

        for (Tuple3<dotNode, dotNode, Integer> a : arcs) {
            sb.append("  nodeNo" + a._1().interalID + " -> nodeNo" + a._2().interalID + "[label=\"" + a._3() + "\"];\n");
        }

        sb.append("}");
        return sb.toString();
    }
}
