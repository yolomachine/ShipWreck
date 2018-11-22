package Model.Geo;

import java.util.ArrayList;
import java.util.Arrays;

public class PointArray extends ArrayList<Point> {

    public PointArray(Point... points) {
        super();
        this.addAll(Arrays.asList(points));
    }

    public PointArray(PointArray points) {
        super();
        this.addAll(points);
    }

    public void add(PointArray points) {
        this.addAll(points);
    }

}
