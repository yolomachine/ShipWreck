package Model;

import Model.Geo.Point;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapPane;
import org.geotools.util.factory.GeoTools;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.Intersects;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class Map {
    private int width;
    private int height;
    private MapContent mapContent;
    private GTRenderer renderer;
    private JMapPane pane;
    private SimpleFeatureType pointType;
    private SimpleFeatureType lineType;
    private String geomAttrName;
    private FilterFactory2 ff;
    private GeometryFactory gf;
    private FeatureSource<SimpleFeatureType, SimpleFeature> featureSource;

    private static Map ourInstance = new Map();

    public static Map getInstance() {
        return ourInstance;
    }

    private Map() {
        pane = new JMapPane();
        try {
            mapContent = new MapContent();
            mapContent.setTitle("Map");
            renderer = new StreamingRenderer();
            renderer.setMapContent(mapContent);
            pane.setRenderer(renderer);
            pane.setMapContent(mapContent);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            width = (int)screenSize.getWidth();
            height = (int)screenSize.getHeight();

            pane.setMinimumSize(new Dimension(width, height));

            pointType =
                    DataUtilities.createType(
                            "Location",
                            "the_geom:Point:srid=4326"
                    );
            lineType =
                    DataUtilities.createType(
                            "Locations",
                            "the_geom:LineString:srid=4326, name:String"
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }

        String mainLayerPathname = "res/Shapefiles/ne_50m_admin_0_sovereignty.shp";
        File mapShapeFile = new File(mainLayerPathname);
        ShapefileDataStore dataStore = null;
        try {
            dataStore = new ShapefileDataStore(mapShapeFile.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        featureSource = null;
        try {
            featureSource = dataStore.getFeatureSource();
        } catch (IOException e) {
            e.printStackTrace();
        }
        geomAttrName = featureSource.getSchema().getGeometryDescriptor ().getLocalName();

        ResourceInfo resourceInfo = featureSource.getInfo();
        CoordinateReferenceSystem crs = resourceInfo.getCRS();
        Hints hints = GeoTools.getDefaultHints();
        hints.put(Hints.JTS_SRID, 4326);
        hints.put(Hints.CRS, crs);

        ff = CommonFactoryFinder.getFilterFactory2(hints);
        gf = JTSFactoryFinder.getGeometryFactory(hints);

        addLayer(mainLayerPathname);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public JMapPane getPane() {
        return pane;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public SimpleFeatureType getPointType() {
        return pointType;
    }

    public SimpleFeatureType getLineType() {
        return lineType;
    }

    public boolean isWater(Point point) {
        Coordinate coord = new Coordinate(point.getLon(), point.getLat());

        Intersects filter = ff.intersects(ff.property(geomAttrName), ff.literal(gf.createPoint(coord)));
        FeatureCollection<SimpleFeatureType, SimpleFeature> features = null;
        try {
            features = featureSource.getFeatures(filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return features == null || features.isEmpty();
    }

    public boolean addLayer(Layer layer) {
        return mapContent.addLayer(layer);
    }

    public boolean removeLayer(Layer layer) {
        return mapContent.removeLayer(layer);
    }

    public void addLayer(String pathname) {
        try {
            File file = new File(pathname);

            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            SimpleFeatureSource featureSource = store.getFeatureSource();

            Style style = SLD.createSimpleStyle(featureSource.getSchema());
            Layer layer = new FeatureLayer(featureSource, style);
            mapContent.addLayer(layer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Map");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG files (*.jpg)", "*.jpg"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            Rectangle imageBounds;
            ReferencedEnvelope mapBounds;
            mapBounds = mapContent.getMaxBounds();
            double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
            imageBounds = new Rectangle(0, 0, width, (int) (heightToWidth * width));

            BufferedImage image = new BufferedImage(
                    imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);

            Graphics2D gr = image.createGraphics();
            gr.setPaint(Color.WHITE);
            gr.fill(imageBounds);

            renderer.paint(gr, imageBounds, mapBounds);
            try {
                ImageIO.write(image, "jpeg", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
