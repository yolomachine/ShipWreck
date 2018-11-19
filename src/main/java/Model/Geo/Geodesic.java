package Model.Geo;

import net.sf.geographiclib.*;

public class Geodesic {
    private static Geodesic ourInstance = new Geodesic();

    public static Geodesic getInstance() {
        return ourInstance;
    }

    private Geodesic() {}

    public static DirectProblemSolutionBinding solveDirectProblem(Point position, double bearing, double distance) {
        GeodesicLine line = net.sf.geographiclib.Geodesic.WGS84.DirectLine(position.getLat(), position.getLon(), bearing, distance);
        GeodesicData data = line.Position(distance, GeodesicMask.ALL);

        return new DirectProblemSolutionBinding(
                new Point(data.lat2, data.lon2),
                data.azi2,
                data.azi1
        );
    }

    public static InverseProblemSolutionBinding solveInverseProblem(Point start, Point destination) {
        GeodesicLine line = net.sf.geographiclib.Geodesic.WGS84.InverseLine(start.getLat(), start.getLon(), destination.getLat(), destination.getLon());

        return new InverseProblemSolutionBinding(
                line.Distance(),
                line.Azimuth(),
                Math.abs(360.0 - line.Azimuth())
        );
    }
}
