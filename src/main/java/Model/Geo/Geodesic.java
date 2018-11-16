package Model.Geo;

public class Geodesic {
    private static Geodesic ourInstance = new Geodesic();

    public static Geodesic getInstance() {
        return ourInstance;
    }

    private Geodesic() {}

    public static DirectProblemSolutionBinding solveDirectProblem(Point position, double bearing, double distance) {
        bearing = Math.toRadians(bearing);
        position = position.toRadians();

        double U1 = Math.atan((1 - WGS84.getFlattening()) * Math.tan(position.getLat()));
        double sigma1 = Math.atan2(Math.tan(U1), Math.cos(bearing));
        double sin_alpha = Math.cos(U1) * Math.sin(bearing);
        double sqr_cos_alpha = 1.0 - sin_alpha * sin_alpha;

        double sqr_u = sqr_cos_alpha * (
                (WGS84.getEquatorialRadius() * WGS84.getEquatorialRadius()) /
                (WGS84.getPolarRadius() * WGS84.getPolarRadius()) - 1);

        double A = 1.0 + (sqr_u / 16384) * (4096 + sqr_u * (-768 + sqr_u * (320 - 175 * sqr_u)));
        double B = (sqr_u / 1024) * (256 + sqr_u * (-128 + sqr_u * (74 - 47 * sqr_u)));
        double sigma = (distance / (WGS84.getPolarRadius() * A));
        double last_sigma = 100000;
        double two_sigma_m = 0;

        while (Math.abs(last_sigma / sigma - 1) > 1e-12) {
            two_sigma_m = 2 * sigma1 + sigma;
            double delta_sigma = B * Math.sin(sigma) * (Math.cos(two_sigma_m) + (B / 4)
                    * (Math.cos(sigma)
                    * (-1 + 2 * Math.pow(Math.cos(two_sigma_m), 2)
                    - (B / 6) * Math.cos(two_sigma_m)
                    * (-3 + 4 * Math.pow(Math.sin(sigma), 2))
                    * (-3 + 4 * Math.pow(Math.cos(two_sigma_m), 2)))));

            last_sigma = sigma;
            sigma = (distance / (WGS84.getPolarRadius() * A)) + delta_sigma;
        }

        Point new_position = new Point(0, 0);
        new_position.setLat(
                Math.atan2(
                        (Math.sin(U1) * Math.cos(sigma)) + Math.cos(U1) * Math.sin(sigma) * Math.cos(bearing),
                        ((1 - WGS84.getFlattening())) * Math.sqrt(Math.pow(sin_alpha, 2)
                            + Math.pow(Math.sin(U1)
                            * Math.sin(sigma)
                            - Math.cos(U1)
                            * Math.cos(sigma)
                            * Math.cos(bearing), 2))
                        )
                );

        double lon = Math.atan2(
                (Math.sin(sigma) * Math.sin(bearing)),
                (Math.cos(U1)
                        * Math.cos(sigma)
                        - Math.sin(U1)
                        * Math.sin(sigma)
                        * Math.cos(bearing))
        );

        double c = (WGS84.getFlattening() / 16) * sqr_cos_alpha * (4 + WGS84.getFlattening() * (4 - 3 * sqr_cos_alpha));

        double delta = lon - (1 - c) * WGS84.getFlattening()
                * sin_alpha
                * (sigma + c * Math.sin(sigma)
                * (Math.cos(two_sigma_m)
                + c * Math.cos(sigma)
                * (-1 + 2 * Math.pow(Math.cos(two_sigma_m), 2))));

        new_position.setLon(position.getLon() + delta);
        double new_reverse_bearing = Math.atan2(
                sin_alpha,
                (-Math.sin(U1)
                        * Math.sin(sigma)
                        + Math.cos(U1)
                        * Math.cos(sigma)
                        * Math.cos(bearing)
                )
        );

        new_reverse_bearing += Math.PI;
        if (new_reverse_bearing < 0.0D) {
            new_reverse_bearing += Math.PI * 2;
        }
        if (new_reverse_bearing > Math.PI * 2) {
            new_reverse_bearing -= Math.PI * 2;
        }

        return new DirectProblemSolutionBinding(
                new Point(Math.toDegrees(new_position.getLat()),Math.toDegrees(new_position.getLon())),
                Math.abs(180 - Math.toDegrees(new_reverse_bearing)),
                Math.toDegrees(new_reverse_bearing)
        );
    }

    public static InverseProblemSolutionBinding solveInverseProblem(Point start, Point destination) {
        if ((Math.abs(start.getLat() - destination.getLat()) < 1e-8) && (Math.abs(start.getLon() - destination.getLon()) < 1e-8)) {
            new InverseProblemSolutionBinding(0.0D, 0.0D, 0.0D, 0.0D);
        }

        Point point1 = start.toRadians();
        Point point2 = destination.toRadians();

        double U1 = Math.atan((1 - WGS84.getFlattening()) * Math.tan(point1.getLat()));
        double U2 = Math.atan((1 - WGS84.getFlattening()) * Math.tan(point2.getLat()));

        double alpha = 0.0d;
        double sigma = 0.0d;
        double sin_sigma = 0.0d;
        double cos_sigma = 0.0d;
        double cos2sigma_m = 0.0d;
        double delta = point2.getLon() - point1.getLon();
        double last = -1000.0d;
        double L = delta;

        while (Math.abs(delta) < 1e-6 && Math.abs(last / delta - 1) > 1e-12) {
            sin_sigma = Math.sqrt(Math.pow(Math.cos(U2) * Math.sin(delta), 2)
                    + Math.pow((Math.cos(U1) * Math.sin(U2)
                    - Math.sin(U1) * Math.cos(U2) * Math.cos(delta)), 2));

            cos_sigma = Math.sin(U1) * Math.sin(U2) + Math.cos(U1) * Math.cos(U2) * Math.cos(delta);
            sigma = Math.atan2(sin_sigma, cos_sigma);
            alpha = Math.asin(Math.cos(U1) * Math.cos(U2) * Math.sin(delta) / Math.sin(sigma));
            cos2sigma_m = Math.cos(sigma) - (2 * Math.sin(U1) * Math.sin(U2) / Math.pow(Math.cos(alpha), 2));

            double c = (WGS84.getFlattening() / 16) * Math.pow(Math.cos(alpha), 2) *
                    (4 + WGS84.getFlattening() * (4 - 3 * Math.pow(Math.cos(alpha), 2)));
            last = delta;
            delta = L + (1 - c) * WGS84.getFlattening() * Math.sin(alpha) *
                    (sigma + c
                            * Math.sin(sigma)
                            * (cos2sigma_m + c * Math.cos(sigma) * (2 * Math.pow(cos2sigma_m, 2) - 1))
                    );
        }

        double sqr_u = Math.pow(Math.cos(alpha), 2)
                * ((WGS84.getEquatorialRadius() * WGS84.getEquatorialRadius())
                / (WGS84.getPolarRadius() * WGS84.getPolarRadius()) - 1);

        double A = 1 + (sqr_u / 16384) * (4096 + sqr_u * (-768 + sqr_u * (320 - 175 * sqr_u)));
        double B = (sqr_u / 1024) * (256 + sqr_u * (-128 + sqr_u * (74 - 47 * sqr_u)));
        double delta_sigma = B * sin_sigma * (cos2sigma_m + (B / 4) * (cos_sigma
                * (-1 + 2 * Math.pow(cos2sigma_m, 2))
                - (B / 6) * cos2sigma_m
                * (-3 + 4 * sin_sigma * sin_sigma)
                * (-3 + 4 * Math.pow(cos2sigma_m, 2))));

        double s = WGS84.getPolarRadius() * A * (sigma - delta_sigma);

        double azimuth_to = Math.atan2((Math.cos(U2) * Math.sin(delta)),
                (Math.cos(U1) * Math.sin(U2) -
                        Math.sin(U1) * Math.cos(U2) * Math.cos(delta)));

        double azimuth_from = Math.atan2((Math.cos(U1) * Math.sin(delta)),
                (-Math.sin(U1) * Math.cos(U2) +
                        Math.cos(U1) * Math.sin(U2) * Math.cos(delta)));

        if (azimuth_to < 0.0) {
            azimuth_to += Math.PI * 2;
        }
        if (azimuth_to > Math.PI * 2) {
            azimuth_to -= Math.PI * 2;
        }

        azimuth_from += Math.PI;
        if (azimuth_from < 0.0) {
            azimuth_from += Math.PI * 2;
        }
        if (azimuth_from > Math.PI * 2) {
            azimuth_from -= Math.PI * 2;
        }

        return new InverseProblemSolutionBinding(
                s,
                Math.toDegrees(azimuth_to),
                Math.abs(180.0 - Math.toDegrees(azimuth_from)),
                Math.toDegrees(azimuth_from)
        );
    }
}
