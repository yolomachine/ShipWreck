package GUI;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ScrollPane mapScrollPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        final SwingNode swingNode = new SwingNode();
        createSwingContent(swingNode);

        mapScrollPane.setContent(swingNode);
    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JMapPane mapPane = new JMapPane();

                try {
                    File file = new File("C:/Users/ьтт/IdeaProjects/ShipWreck/res/shapefiles/ne_50m_admin_0_sovereignty.shp");

                    FileDataStore store = FileDataStoreFinder.getDataStore(file);
                    SimpleFeatureSource featureSource = store.getFeatureSource();

                    MapContent map = new MapContent();
                    map.setTitle("Quickstart");

                    Style style = SLD.createSimpleStyle(featureSource.getSchema());
                    Layer layer = new FeatureLayer(featureSource, style);
                    map.addLayer(layer);

                    GTRenderer renderer = new StreamingRenderer();
                    renderer.setMapContent(map);

                    Rectangle imageBounds = null;
                    ReferencedEnvelope mapBounds = null;
                    mapBounds = map.getMaxBounds();
                    double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
                    imageBounds = new Rectangle(
                            0, 0, 1024, (int) Math.round(1024 * heightToWidth));

                    BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);

                    Graphics2D gr = image.createGraphics();
                    gr.setPaint(Color.WHITE);
                    gr.fill(imageBounds);

                    renderer.paint(gr, imageBounds, mapBounds);
                    File fileToSave = new File("src/main/java/GUI/Map.jpg");
                    ImageIO.write(image, "jpeg", fileToSave);

                    mapPane.setRenderer(renderer);
                    mapPane.setMapContent(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                swingNode.setContent(mapPane);
            }
        });
    }

}
