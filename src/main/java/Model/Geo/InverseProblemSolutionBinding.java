package Model.Geo;

public class InverseProblemSolutionBinding {
    private double distance;
    private double bearingTo;
    private double bearingFrom;

    public InverseProblemSolutionBinding(double distance, double bearingTo, double bearingFrom) {
        this.distance = distance;
        this.bearingTo = bearingTo;
        this.bearingFrom = bearingFrom;
    }

    public double getDistance() {
        return distance;
    }

    public double getBearingTo() {
        return bearingTo;
    }

    public double getBearingFrom() {
        return bearingFrom;
    }
}
