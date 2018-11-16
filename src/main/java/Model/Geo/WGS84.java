package Model.Geo;

public class WGS84 {
    private static final double radius_equatorial = 6378137D;
    private static final double radius_polar = 6356852.3142D;
    private static final double flattening = 1 - radius_polar / radius_equatorial;

    public static double getEquatorialRadius() {
        return radius_equatorial;
    }

    public static double getPolarRadius() {
        return radius_polar;
    }

    public static double getFlattening() {
        return flattening;
    }
}
