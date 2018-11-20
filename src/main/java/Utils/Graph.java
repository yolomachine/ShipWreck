package Utils;

import Model.Geo.*;
import com.healthmarketscience.common.util.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Graph {
    private class Edge extends Tuple2<Node, Double> {
        public Edge(Node node, Double weight) {
            super(node, weight);
        }

        public Node getNode() {
            return get0();
        }

        public Double getWeight() {
            return get1();
        }
    }

    private class Node {
        public Point target;
        public Node predecessor;
        public ArrayList<Edge> edges;

        public Node(Point target) {
            this.target = target;
            edges = new ArrayList<>();
        }

        public void addEdge(Node node) {
            edges.add(new Edge(node, Geodesic.solveInverseProblem(target, node.target).getDistance()));
        }

        public void addEdge(Node node, double weight) {
            edges.add(new Edge(node, weight));
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public String toString() {
            return target.toString();
        }
    }

    public class AStar {
        public AStar() { }

        private double tryFindLand(Point start, Point destination) {
            InverseProblemSolutionBinding ipsb = Geodesic.solveInverseProblem(start, destination);
            double bearing = ipsb.getBearingTo();
            double h = ipsb.getDistance() / 100;
            Point current = start;
            for (int i = 0; i < 100; ++i) {
                DirectProblemSolutionBinding dpsb = Geodesic.solveDirectProblem(current, bearing, h);
                bearing = dpsb.getBearing();
                current = dpsb.getPosition();
                if (!Map.getInstance().isWater(current)) {
                    return Double.MAX_VALUE;
                }
            }
            return 0.0;
        }

        private PointArray reconstructPath(HashMap<Integer, Integer> cameFrom, Node current) {
            PointArray path = new PointArray();
            while (cameFrom.keySet().contains(current.hashCode())) {
                path.add(current.target);
                current = data.get(cameFrom.get(current.hashCode()));
            }
            path.add(current.target);
            Collections.reverse(path);
            return path;
        }

        public PointArray search(Point start, Point destination) {
            Node startNode = data.get(start.hashCode());

            HashSet<Node> closed = new HashSet<>();
            HashSet<Node> open = new HashSet<>();
            open.add(startNode);

            HashMap<Integer, Integer> cameFrom = new HashMap<>();
            HashMap<Integer, Double> gScore = new HashMap<>();
            HashMap<Integer, Double> fScore = new HashMap<>();
            gScore.put(startNode.hashCode(), 0.0);
            fScore.put(startNode.hashCode(), Geodesic.solveInverseProblem(start, destination).getDistance());

            Node current = startNode;
            while (!open.isEmpty()) {
                double fScoreCurrent = Double.MAX_VALUE;
                for (Node n : open) {
                    double score = fScore.getOrDefault(n.hashCode(), Double.MAX_VALUE);
                    if (score < fScoreCurrent) {
                        current = n;
                        fScoreCurrent = score;
                    }
                }

                if (current.target == destination) {
                    return reconstructPath(cameFrom, current);
                }

                open.remove(current);
                closed.add(current);

                for (Edge edge : current.edges) {
                    Node neighbor = edge.getNode();
                    if (closed.contains(neighbor)) {
                        continue;
                    }

                    double gScoreTentative =
                            gScore.getOrDefault(current.hashCode(), Double.MAX_VALUE)
                            + edge.getWeight()
                            + ((neighbor.target == destination || current.target == destination) ? tryFindLand(current.target, neighbor.target) : 0.0);
                    if (!open.contains(neighbor)) {
                        open.add(neighbor);
                    } else if (gScoreTentative >= gScore.getOrDefault(neighbor.hashCode(), Double.MAX_VALUE)) {
                        continue;
                    }

                    cameFrom.put(neighbor.hashCode(), current.hashCode());
                    gScore.put(neighbor.hashCode(), gScoreTentative);
                    fScore.put(
                            neighbor.hashCode(),
                            gScore.getOrDefault(neighbor.hashCode(), Double.MAX_VALUE) +
                                    Geodesic.solveInverseProblem(neighbor.target, destination).getDistance()
                    );
                }
            }

            return new PointArray();
        }
    }

    private HashMap<Integer, Node> data;
    public AStar AStar = new AStar();

    public Graph() {
        data = new HashMap<>();
    }

    public Node addVertex(Point point) {
        Node node = new Node(point);
        data.put(node.hashCode(), node);
        return node;
    }

    public void addEdge(Node node, Node node1) {
        node.addEdge(node1);
        node1.addEdge(node);
    }

    public void addEdge(Node node, Node node1, double weight) {
        node.addEdge(node1, weight);
        node1.addEdge(node, weight);
    }

    public void addEdge(Point point, Point point1) {
        addEdge(data.get(point.hashCode()), data.get(point1.hashCode()));
    }

    public void addEdge(Point point, Point point1, double weight) {
        addEdge(data.get(point.hashCode()), data.get(point1.hashCode()), weight);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(" ");
        for (Integer hash : data.keySet()) {
            Node node = data.get(hash);
            stringBuilder.append(String.format("%s: ", node.toString()));
            for (Edge edge : node.edges) {
                stringBuilder.append(String.format("%s; ", edge.getNode().toString()));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

}
