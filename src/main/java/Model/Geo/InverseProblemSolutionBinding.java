package Model.Geo;

public class InverseProblemSolutionBinding {
    private double distance;
    private double bearingTo;
    private double bearingFrom;
    private double reverseBearingFrom;

    public InverseProblemSolutionBinding(double distance, double bearingTo, double bearingFrom, double reverseBearingFrom) {
        this.distance = distance;
        this.bearingTo = bearingTo;
        this.bearingFrom = bearingFrom;
        this.reverseBearingFrom = reverseBearingFrom;
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

    public double getReverseBearingFrom() {
        return reverseBearingFrom;
    }
}
