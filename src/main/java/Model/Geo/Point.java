package Model.Geo;

public class Point {

    private double lat;
    private double lon;

    public Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String toString() {
        return String.format("%s %s", lon, lat);
    }

    public Point toRadians() {
        return new Point(Math.toRadians(getLat()), Math.toRadians(getLon()));
    }

    public Point toDegrees() {
        return new Point(Math.toDegrees(getLat()), Math.toDegrees(getLon()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof Point)) {
            return false;
        }
        Point otherPoint = (Point) other;
        return (Math.abs(this.getLon() - otherPoint.getLon()) < 1e-6) && (Math.abs(this.getLat() - otherPoint.getLat()) < 1e-6);
    }
}

