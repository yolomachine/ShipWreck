package Model;

import GUI.Controls.InterfaceNode;
import Model.Geo.Point;
import Model.Geo.PointArray;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class Route extends InterfaceNode {

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
        for (int i = 0; i < bb.getInt(); ++i) {
            double lat = bb.getDouble();
            double lon = bb.getDouble();
            points.add(new Point(lat, lon));
        }
        return points;
    }
}
