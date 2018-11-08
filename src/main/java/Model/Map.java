package Model;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapPane;
import org.opengis.feature.simple.SimpleFeatureType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Map {
    private int width;
    private int height;
    private MapContent mapContent;
    private GTRenderer renderer;
    private JMapPane pane;
    private SimpleFeatureType featureType;

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

            featureType =
                    DataUtilities.createType(
                            "Location",
                            "the_geom:Point:srid=4326, name:String"
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    public void addLayer(Layer layer) {
        mapContent.addLayer(layer);
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

//    public Style createStyle(FeatureSource featureSource) {
//        SimpleFeatureType schema = (SimpleFeatureType) featureSource.getSchema();
//        Class geomType = schema.getGeometryDescriptor().getType().getBinding();
//
//        if (Polygon.class.isAssignableFrom(geomType)
//                || MultiPolygon.class.isAssignableFrom(geomType)) {
//            return createPolygonStyle();
//        } else if (LineString.class.isAssignableFrom(geomType)
//                || MultiLineString.class.isAssignableFrom(geomType)) {
//            return createLineStyle();
//        } else {
//            return createPointStyle();
//        }
//    }
}
