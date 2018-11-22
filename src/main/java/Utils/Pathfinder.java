package Utils;

import Model.Geo.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Pathfinder {
    private static Pathfinder ourInstance = new Pathfinder();

    public static Pathfinder getInstance() {
        return ourInstance;
    }

    private Pathfinder() { }

    public PointArray isochroneMethod(Point start, Point destination, double velocity) {
        Graph graph = new Graph();
        graph.addVertex(start);

        PointArray prev = new PointArray();
        PointArray next = new PointArray();
        PointArray orthodromesFronts = new PointArray();
        ArrayList<Double> orthodromesBearings = new ArrayList<>();

        int n = 10;
        double time = Geodesic.solveInverseProblem(start, destination).getDistance() / velocity / n;

        double initialBearing = Geodesic.solveInverseProblem(start, destination).getBearingTo();
        for (int i = 1; i < 11; ++i) {
            DirectProblemSolutionBinding dpsb = Geodesic.solveDirectProblem(start, initialBearing - i * 6.0, velocity * time);
            orthodromesFronts.add(dpsb.getPosition());
            orthodromesBearings.add(initialBearing - i * 6.0);
            if (Map.getInstance().isWater(dpsb.getPosition())) {
                graph.addVertex(dpsb.getPosition());
                graph.addEdge(start, dpsb.getPosition(), velocity * time);
            }
        }
        Point startAdvance = Geodesic.solveDirectProblem(start, initialBearing, velocity * time).getPosition();
        orthodromesFronts.add(startAdvance);
        orthodromesBearings.add(initialBearing);
        if (Map.getInstance().isWater(startAdvance)) {
            graph.addVertex(startAdvance);
            graph.addEdge(start, startAdvance, velocity * time);
        }
        for (int i = 10; i > 0; --i) {
            DirectProblemSolutionBinding dpsb = Geodesic.solveDirectProblem(start, initialBearing + i * 6.0, velocity * time);
            orthodromesFronts.add(dpsb.getPosition());
            orthodromesBearings.add(initialBearing + i * 6.0);
            if (Map.getInstance().isWater(dpsb.getPosition())) {
                graph.addVertex(dpsb.getPosition());
                graph.addEdge(start, dpsb.getPosition(), velocity * time);
            }
        }

        prev.add(orthodromesFronts);

        // TO DO: Apply speed loss function
        for (int j = 1; j < n - 1; ++j) {
            PointArray isochrone = new PointArray();

            for (int k = 0; k < orthodromesFronts.size(); ++k) {
                DirectProblemSolutionBinding dpsb = Geodesic.solveDirectProblem(orthodromesFronts.get(k), orthodromesBearings.get(k), velocity * time);

                orthodromesFronts.set(k, dpsb.getPosition());
                orthodromesBearings.set(k, dpsb.getBearing());
            }

            HashMap<String, PointArray> sectors = new HashMap<>();
            int sectorsCount = orthodromesFronts.size() - 1;
            for (int i = 0; i < sectorsCount; ++i) {
                sectors.put(String.format("%d", i), new PointArray());
            }

            for (Point point : prev) {
                next.clear();
                InverseProblemSolutionBinding ipsb = Geodesic.solveInverseProblem(point, destination);
                double bearing = ipsb.getBearingTo();

                for (int i = 1; i < 11; ++i) {
                    next.add(Geodesic.solveDirectProblem(point, bearing - i * 6.0, velocity * time).getPosition());
                }
                next.add(Geodesic.solveDirectProblem(point, bearing, velocity * time).getPosition());
                for (int i = 10; i > 0; --i) {
                    next.add(Geodesic.solveDirectProblem(point, bearing + i * 6.0, velocity * time).getPosition());
                }

                for (int i = 0; i < sectorsCount; ++i) {
                    String key = String.format("%d", i);
                    for (Point p : next) {
                        if (isInSector(p, start, velocity * time, orthodromesBearings.get(i), orthodromesBearings.get(i + 1), j)) {
                            sectors.get(key).add(p);
                        }
                    }
                }
            }
            for (int i = 0; i < sectorsCount; ++i) {
                PointArray sector = sectors.get(String.format("%d", i));
                if (sector.size() > 0) {
                    isochrone.add(getSectorMaximum(start, sector));
                }
            }
            for (Point p : prev) {
                if (Map.getInstance().isWater(p)) {
                    for (Point p1 : isochrone) {
                        if (Map.getInstance().isWater(p1)) {
                            graph.addVertex(p1);
                            graph.addEdge(p, p1);
                        }
                    }
                }
            }
            prev.clear();
            prev.add(isochrone);
        }
        graph.addVertex(destination);
        for (Point p : prev) {
            if (Map.getInstance().isWater(p)) {
                graph.addEdge(p, destination);
            }
        }
        PointArray path = new PointArray(start);
        path.add(graph.AStar.search(start, destination));
        path.add(destination);
        return path;
    }

    private boolean isInSector(Point tested, Point start, double distance, double bearingLeft, double bearingRight, int rank) {
        while (bearingLeft > bearingRight) {
            Point front = start;
            double frontBearing = bearingLeft;
            for (int i = 0; i < rank; ++i) {
                DirectProblemSolutionBinding dpsb = Geodesic.solveDirectProblem(front, frontBearing, distance);
                front = dpsb.getPosition();
                frontBearing = dpsb.getBearing();
            }
            double bearing = Geodesic.solveInverseProblem(front, tested).getBearingTo();
            if (bearing <= bearingLeft && bearing >= bearingRight) {
                return true;
            }
            bearingLeft -= 0.05;
        }
        return false;
    }

    private PointArray getSectorMaximum(Point pivot, PointArray sector) {
        double max = Double.MIN_VALUE;
        PointArray furthest = new PointArray();
        for (Point point : sector) {
            double distance = Geodesic.solveInverseProblem(pivot, point).getDistance();
            if (distance > max) {
                max = distance;
                furthest.clear();
            }
            if (Math.abs(distance - max) < 1e1) {
                furthest.add(point);
            }
        }
        return furthest;
    }

    public PointArray greedyBestFirstSearch(Point start, Point destination, int n, boolean isAnglePositive) {
        PointArray points = new PointArray(start);
        InverseProblemSolutionBinding ipsb = Geodesic.solveInverseProblem(start, destination);
        double bearing = ipsb.getBearingTo();
        double h = ipsb.getDistance() / n;
        for (int i = 0; i < n; ++i) {
            DirectProblemSolutionBinding dpsb = Geodesic.solveDirectProblem(points.get(points.size() - 1), bearing, h);
            if (Map.getInstance().isWater(dpsb.getPosition())) {
                bearing = dpsb.getBearing();
                points.add(dpsb.getPosition());
            } else {
                return turn(points, destination, n - 1, isAnglePositive);
            }
        }
        points.add(destination);
        return points;
    }

    private PointArray turn(PointArray points, Point destination, int n, boolean isAnglePositive) {
        PointArray newPath = null;
        while (points.size() > 0) {
            InverseProblemSolutionBinding ipsb = Geodesic.solveInverseProblem(points.get(points.size() - 1), destination);
            double bearing = ipsb.getBearingTo();
            double h = ipsb.getDistance() / n;
            for (int j = 1; j < 16; ++j) {
                Point newStart = Geodesic.solveDirectProblem(
                        points.get(points.size() - 1), isAnglePositive ? bearing + j * 10 : bearing - j * 10, h
                ).getPosition();
                if (Map.getInstance().isWater(newStart)) {
                    newPath = greedyBestFirstSearch(newStart, destination, n, isAnglePositive);
                    if (newPath != null && newPath.get(newPath.size() - 1).equals(destination)) {
                        points.add(newPath);
                        return points;
                    }
                }
            }
            points.remove(points.size() - 1);
        }
        return newPath;
    }
}
