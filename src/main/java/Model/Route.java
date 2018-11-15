package Model;

import GUI.Controls.InteractiveNode;
import Model.Geo.Point;
import Model.Geo.PointArray;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.SLD;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Route extends InteractiveNode {
    private int shipId;
    private Point start;
    private Point destination;
    private PointArray points;
    private Layer pointsLayer;
    private Layer linesLayer;

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

    public void setPointsLayer(Layer pointsLayer) {
        this.pointsLayer = pointsLayer;
    }

    public void setLinesLayer(Layer linesLayer) {
        this.linesLayer = linesLayer;
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

    public Layer getPointsLayer() {
        return pointsLayer;
    }

    public Layer getLinesLayer() {
        return linesLayer;
    }

    public void calculate() {
        points = new PointArray(start, destination);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(name);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Shape files (*.shp)", "*.shp"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file == null) {
            toShapefile();
        } else {
            toShapefile(file.getAbsolutePath());
        }
    }

    @Override
    public void invalidate() {
        while (pointsLayer != null && Map.getInstance().removeLayer(pointsLayer)) { }
        while (linesLayer != null && Map.getInstance().removeLayer(linesLayer)) { }
        File dir = new File(System.getProperty("user.dir") + "/routes/" + name);
        try {
            deleteDirectoryStream(dir.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toShapefile(String pathname) {
        pointsLayer = createPointsLayer(pathname);
        linesLayer = createLinesLayer(pathname);
        while(!Map.getInstance().addLayer(linesLayer)) { }
        while(!Map.getInstance().addLayer(pointsLayer)) { }
    }

    public void toShapefile() {
        File dir = new File(System.getProperty("user.dir") + "/routes/" + name + "/");
        dir.mkdirs();
        File file = new File(dir,name + ".shp");
        toShapefile(file.getAbsolutePath());
    }

    private Layer createPointsLayer(String pathname) {
        SimpleFeatureType featureType = Map.getInstance().getPointType();
        List<SimpleFeature> features = new ArrayList<>();
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);

        for (Point point : points) {
            featureBuilder.add(geometryFactory.createPoint(new Coordinate(point.getLon(), point.getLat())));
            features.add(featureBuilder.buildFeature(null));
        }

        FeatureCollection featureCollection = createShapefile(
                String.format("%s%s.shp", pathname.substring(0, pathname.length() - 4), "_points"),
                features,
                featureType
        );

        Layer layer = null;
        try {
            layer = new FeatureLayer(
                    featureCollection,
                    SLD.createPointStyle(
                            "circle",
                            java.awt.Color.ORANGE,
                            java.awt.Color.BLACK,
                            1.0f,
                            5.0f
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return layer;
    }

    public Layer createLinesLayer(String pathname) {
        SimpleFeatureType featureType = Map.getInstance().getLineType();

        List<SimpleFeature> features = new ArrayList<>();
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        WKTReader reader = new WKTReader( geometryFactory );
        StringBuilder lineStringBuilder = new StringBuilder("LINESTRING(");

        for (Point point : points) {
            lineStringBuilder.append(point.toString());
            if (point == points.get(points.size() - 1)) {
                lineStringBuilder.append(")");
            } else {
                lineStringBuilder.append(", ");
            }
        }
        LineString line = null;
        try {
            line = (LineString) reader.read(lineStringBuilder.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        featureBuilder.add(line);
        featureBuilder.add(name);
        features.add(featureBuilder.buildFeature(null));

        FeatureCollection featureCollection = createShapefile(
                String.format("%s%s.shp", pathname.substring(0, pathname.length() - 4), "_lines"),
                features,
                featureType
        );

        Layer layer = null;
        try {
            layer = new FeatureLayer(
                    featureCollection,
                    SLD.createLineStyle(java.awt.Color.ORANGE, 1.0f)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return layer;
    }

    private FeatureCollection createShapefile(String pathname, List<SimpleFeature> features, SimpleFeatureType featureType) {
        File shapefile = new File(pathname);
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        HashMap<String, Serializable> params = new HashMap<>();
        try {
            params.put("url", shapefile.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        params.put("create spatial index", Boolean.TRUE);

        ShapefileDataStore newDataStore = null;
        try {
            newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            newDataStore.createSchema(featureType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Transaction transaction = new DefaultTransaction("create");

        String typeName = null;
        try {
            typeName = newDataStore.getTypeNames()[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        SimpleFeatureSource featureSource = null;
        try {
            featureSource = newDataStore.getFeatureSource(typeName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FeatureCollection featureCollection = null;
        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            SimpleFeatureCollection collection = new ListFeatureCollection(featureType, features);
            featureStore.setTransaction(transaction);
            try {
                featureStore.addFeatures(collection);
                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    transaction.rollback();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } finally {
                try {
                    transaction.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            featureCollection = collection;
        } else {
            System.out.println(typeName + " does not support read/write access");
        }

        return featureCollection;
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
                "Start { Lat: %.2f, Lon: %.2f }; Destination { Lat: %.2f, Lon: %.2f }",
                start.getLat(), start.getLon(), destination.getLat(), destination.getLon()
        );
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setFont(new Font(24));
        return tooltip;
    }

    void deleteDirectoryStream(Path path) throws IOException {
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
