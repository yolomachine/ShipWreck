package Utils;

import Model.Geo.Point;
import com.healthmarketscience.common.util.Tuple2;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
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
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
import org.geotools.swing.tool.ZoomInTool;
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
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

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
    private java.awt.Point panePos;
    private boolean isDragging;

    private static final String mainLayerPathname       = "res/Shapefiles/Marine/ne_10m_geography_marine_polys.shp";
    private static final String landLayerPathname       = "res/Shapefiles/Admin/ne_50m_admin_0_sovereignty.shp";
    private static final String bathymetry10000Pathname = "res/Shapefiles/Bathymetry/ne_10m_bathymetry_A_10000.shp";
    private static final String bathymetry9000Pathname  = "res/Shapefiles/Bathymetry/ne_10m_bathymetry_B_9000.shp";
    private static final String bathymetry8000Pathname  = "res/Shapefiles/Bathymetry/ne_10m_bathymetry_C_8000.shp";
    private static final String bathymetry7000Pathname  = "res/Shapefiles/Bathymetry/ne_10m_bathymetry_D_7000.shp";
    private static final String bathymetry6000Pathname  = "res/Shapefiles/Bathymetry/ne_10m_bathymetry_E_6000.shp";
    private static final String bathymetry5000Pathname  = "res/Shapefiles/Bathymetry/ne_10m_bathymetry_F_5000.shp";
    private static final String bathymetry4000Pathname  = "res/Shapefiles/Bathymetry/ne_10m_bathymetry_G_4000.shp";
    private static final String bathymetry3000Pathname  = "res/Shapefiles/Bathymetry/ne_10m_bathymetry_H_3000.shp";
    private static final String bathymetry2000Pathname  = "res/Shapefiles/Bathymetry/ne_10m_bathymetry_I_2000.shp";
    private static final String bathymetry1000Pathname  = "res/Shapefiles/Bathymetry/ne_10m_bathymetry_J_1000.shp";
    private static final String bathymetry200Pathname   = "res/Shapefiles/Bathymetry/ne_10m_bathymetry_K_200.shp";
    private static final String bathymetry0Pathname     = "res/Shapefiles/Bathymetry/ne_10m_bathymetry_L_0.shp";
    private static final String graticulesPathname      = "res/Shapefiles/Graticules/ne_10m_graticules_5.shp";

    private static final ArrayList<Tuple2<String, Style>> styles;
    static
    {
        styles = new ArrayList<>();
        styles.add(new Tuple2<>(mainLayerPathname,       SLD.createPolygonStyle(hex2Rgb("#FFFFFF"), hex2Rgb("#FFFFFF"), 1.0f)));
        styles.add(new Tuple2<>(landLayerPathname,       SLD.createPolygonStyle(hex2Rgb("#F5D760"), hex2Rgb("#F5D76E"), 1.0f)));
        styles.add(new Tuple2<>(bathymetry0Pathname,     SLD.createPolygonStyle(hex2Rgb("#E3ECFC"), hex2Rgb("#E3ECFC"), 1.0f)));
        styles.add(new Tuple2<>(bathymetry200Pathname,   SLD.createPolygonStyle(hex2Rgb("#E0E0E0"), hex2Rgb("#D5E5F7"), 1.0f)));
        styles.add(new Tuple2<>(bathymetry1000Pathname,  SLD.createPolygonStyle(hex2Rgb("#C7D8F8"), hex2Rgb("#C7D8F8"), 1.0f)));
        styles.add(new Tuple2<>(bathymetry2000Pathname,  SLD.createPolygonStyle(hex2Rgb("#AAC5F5"), hex2Rgb("#AAC5F5"), 1.0f)));
        styles.add(new Tuple2<>(bathymetry3000Pathname,  SLD.createPolygonStyle(hex2Rgb("#8EB2F2"), hex2Rgb("#8EB2F2"), 1.0f)));
        styles.add(new Tuple2<>(bathymetry4000Pathname,  SLD.createPolygonStyle(hex2Rgb("#729FEF"), hex2Rgb("#729FEF"), 1.0f)));
        styles.add(new Tuple2<>(bathymetry5000Pathname,  SLD.createPolygonStyle(hex2Rgb("#5B87E4"), hex2Rgb("#5B87E4"), 1.0f)));
        styles.add(new Tuple2<>(bathymetry6000Pathname,  SLD.createPolygonStyle(hex2Rgb("#496CD2"), hex2Rgb("#496CD2"), 1.0f)));
        styles.add(new Tuple2<>(bathymetry7000Pathname,  SLD.createPolygonStyle(hex2Rgb("#3751C0"), hex2Rgb("#3751C0"), 1.0f)));
        styles.add(new Tuple2<>(bathymetry8000Pathname,  SLD.createPolygonStyle(hex2Rgb("#2436AF"), hex2Rgb("#2436AF"), 1.0f)));
        styles.add(new Tuple2<>(bathymetry9000Pathname,  SLD.createPolygonStyle(hex2Rgb("#121B9D"), hex2Rgb("#121B9D"), 1.0f)));
        styles.add(new Tuple2<>(bathymetry10000Pathname, SLD.createPolygonStyle(hex2Rgb("#00008B"), hex2Rgb("#00008B"), 1.0f)));
        styles.add(new Tuple2<>(graticulesPathname, SLD.createLineStyle(Color.BLACK, 1)));
    }

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

        for (Tuple2<String, Style> style : styles) {
            while(!addLayer(style.get0(), style.get1())) {}
        }

        pane.setDoubleBuffered(true);
        pane.addMouseListener(new MapMouseListener() {
            @Override
            public void onMouseClicked(MapMouseEvent e) {
                Rectangle paneArea = pane.getVisibleRect();
                DirectPosition2D mapPos = e.getWorldPos();
                DirectPosition2D corner = new DirectPosition2D(mapPos.getX() + paneArea.getWidth(), mapPos.getY() + paneArea.getHeight());
                double defaultScale = 4.333334857941979;
                double scale = pane.getWorldToScreenTransform().getScaleX();
                double newScale = scale;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    newScale = scale * ZoomInTool.DEFAULT_ZOOM_FACTOR;
                    corner = new DirectPosition2D(mapPos.getX() - 0.5D * paneArea.getWidth() / newScale, mapPos.getY() + 0.5D * paneArea.getHeight() / newScale);
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    newScale = scale / ZoomInTool.DEFAULT_ZOOM_FACTOR;
                    if (newScale < defaultScale) {
                        return;
                    } else {
                        corner = new DirectPosition2D(mapPos.getX() - 0.5D * paneArea.getWidth() / newScale, mapPos.getY() - 0.5D * paneArea.getHeight() / newScale);
                    }
                }
                Envelope2D newMapArea = new Envelope2D();
                newMapArea.setFrameFromCenter(mapPos, corner);
                pane.setDisplayArea(newMapArea);
            }

            @Override
            public void onMouseDragged(MapMouseEvent mapMouseEvent) {
                if (isDragging) {
                    java.awt.Point pos = mapMouseEvent.getPoint();
                    if (!pos.equals(panePos)) {
                        pane.moveImage(pos.x - panePos.x, pos.y - panePos.y);
                        panePos = pos;
                    }
                }
            }

            @Override
            public void onMouseEntered(MapMouseEvent mapMouseEvent) {

            }

            @Override
            public void onMouseExited(MapMouseEvent mapMouseEvent) {

            }

            @Override
            public void onMouseMoved(MapMouseEvent mapMouseEvent) {

            }

            @Override
            public void onMousePressed(MapMouseEvent mapMouseEvent) {
                panePos = mapMouseEvent.getPoint();
                isDragging = true;
            }

            @Override
            public void onMouseReleased(MapMouseEvent mapMouseEvent) {
                isDragging = false;
            }

            @Override
            public void onMouseWheelMoved(MapMouseEvent mapMouseEvent) {

            }
        });
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
        return !features.isEmpty();
    }

    public boolean addLayer(Layer layer) {
        return mapContent.addLayer(layer);
    }

    public boolean addLayer(String pathname) {
        Layer layer = null;
        try {
            File file = new File(pathname);

            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            SimpleFeatureSource featureSource = store.getFeatureSource();

            Style style = SLD.createSimpleStyle(featureSource.getSchema());
            layer = new FeatureLayer(featureSource, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapContent.addLayer(layer);

    }

    public boolean addLayer(String pathname, Style style) {
        Layer layer = null;
        try {
            File file = new File(pathname);

            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            SimpleFeatureSource featureSource = store.getFeatureSource();

            layer = new FeatureLayer(featureSource, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapContent.addLayer(layer);
    }

    public boolean removeLayer(Layer layer) {
        return mapContent.removeLayer(layer);
    }

    public void save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Map");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG files (*.jpg)", "*.jpg"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            Rectangle imageBounds;
            ReferencedEnvelope mapBounds;
            mapBounds = mapContent.getViewport().getBounds();
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

    /**
     *
     * @param colorStr e.g. "#FFFFFF"
     * @return java.awt.Color
     */
    public static Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16)
        );
    }
}
