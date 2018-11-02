package GUI.StageControllers;

import GUI.Controls.CustomTreeView;
import GUI.Controls.ShipsListView;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
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

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private ScrollPane mapScrollPane;

    @FXML
    private TitledPane shipsTitledPane;

    @FXML
    private TitledPane routesTitledPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shipsTitledPane.setContent(ShipsListView.getInstance());
        routesTitledPane.setContent(CustomTreeView.getInstance());

        final SwingNode swingNode = new SwingNode();
        createSwingContent(swingNode, createMap());

        mapScrollPane.setContent(swingNode);
    }

    private void createSwingContent(final SwingNode swingNode, final JComponent content) {
        SwingUtilities.invokeLater(() -> swingNode.setContent(content));
    }

    private JMapPane createMap() {
        JMapPane mapPane = new JMapPane();
        try {
            File file = new File("res/shapefiles/ne_50m_admin_0_sovereignty.shp");

            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            SimpleFeatureSource featureSource = store.getFeatureSource();

            MapContent map = new MapContent();
            map.setTitle("Map");

            Style style = SLD.createSimpleStyle(featureSource.getSchema());
            Layer layer = new FeatureLayer(featureSource, style);
            map.addLayer(layer);

            GTRenderer renderer = new StreamingRenderer();
            renderer.setMapContent(map);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int width = (int)screenSize.getWidth();
            int height = (int)screenSize.getHeight();

            Rectangle imageBounds;
            ReferencedEnvelope mapBounds;
            mapBounds = map.getMaxBounds();
            double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
            imageBounds = new Rectangle(0, 0, width, (int) (heightToWidth * width));

            BufferedImage image = new BufferedImage(
                    imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);

            Graphics2D gr = image.createGraphics();
            gr.setPaint(Color.WHITE);
            gr.fill(imageBounds);

            //renderer.paint(gr, imageBounds, mapBounds);
            //File fileToSave = new File("src/main/java/GUI/Map.jpg");
            //ImageIO.write(image, "jpeg", fileToSave);

            mapPane.setRenderer(renderer);
            mapPane.setMapContent(map);
            mapPane.setMinimumSize(new Dimension(width, height));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapPane;
    }

}
