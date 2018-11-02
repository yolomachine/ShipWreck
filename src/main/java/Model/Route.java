package Model;

import GUI.Controls.InteractiveNode;
import Model.Geo.Point;
import Model.Geo.PointArray;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;

import java.nio.ByteBuffer;

public class Route extends InteractiveNode {

    private int id;
    private int shipId;
    private Point start;
    private Point destination;
    private PointArray points;

    public Route(String name, Point start, Point destination) {
        super(name, Type.Route);
        this.start = start;
        this.destination = destination;
    }

    public Route(String name, byte[] blob) {
        super(name, Type.Route);
        this.points = fromBlob(blob);
        this.start = points.get(0);
        this.destination = points.get(points.size() - 1);
    }

    public Route(int id, int shipId, String name, byte[] blob) {
        this(name, blob);
        this.id = id;
        this.shipId = shipId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setShipId(int shipId) {
        this.shipId = shipId;
    }

    public void setStartPoint(Point start) {
        this.start = start;
    }

    public void setDestinationPoint(Point destination) {
        this.destination = destination;
    }

    public int getId() {
        return id;
    }

    public int getShipId() {
        return shipId;
    }

    public Point getStartPoint() {
        return start;
    }

    public Point getDestinationPoint() {
        return destination;
    }

    public PointArray getPoints() {
        return points;
    }

    public PointArray calculate() {
        points = new PointArray(start, destination);
        // placeholder
        return points;
    }

    public void toShapefile() {

    }

    public byte[] toBlob() {
        ByteBuffer bb = ByteBuffer.allocate(Integer.BYTES + points.size() * 8 * 2);
        bb.putInt(points.size());
        for (Point point : points) {
            bb.putDouble(point.getLat());
            bb.putDouble(point.getLon());
        }
        return bb.array();
    }

    public PointArray fromBlob(byte[] blob) {
        ByteBuffer bb = ByteBuffer.wrap(blob);
        PointArray points = new PointArray();
        int size = bb.getInt();
        for (int i = 0; i < size; ++i) {
            double lat = bb.getDouble();
            double lon = bb.getDouble();
            points.add(new Point(lat, lon));
        }
        return points;
    }

    @Override
    public Tooltip getTooltip() {
        String tooltipText = String.format(
                "Id: %d; Start { Lat: %.2f, Lon: %.2f }; Destination { Lat: %.2f, Lon: %.2f }",
                id, start.getLat(), start.getLon(), destination.getLat(), destination.getLon()
        );
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setFont(new Font(24));
        return tooltip;
    }
}
