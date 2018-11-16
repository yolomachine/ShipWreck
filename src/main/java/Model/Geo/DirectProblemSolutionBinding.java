package Model.Geo;

public class DirectProblemSolutionBinding {
    private Point position;
    private double bearing;
    private double reverseBearing;

    public DirectProblemSolutionBinding(Point position, double bearing, double reverseBearing) {
        this.position = position;
        this.bearing = bearing;
        this.reverseBearing = reverseBearing;
    }

    public Point getPosition() {
        return position;
    }

    public double getBearing() {
        return bearing;
    }

    public double getReverseBearing() {
        return reverseBearing;
    }
}
