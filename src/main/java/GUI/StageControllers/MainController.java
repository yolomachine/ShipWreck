package GUI.StageControllers;

import GUI.Controls.CustomTreeView;
import GUI.Controls.ShipsListView;
import Model.Map;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;

import javax.swing.*;
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

        createMap();
    }

    @FXML
    private void saveMap() {
        Map.getInstance().save();
    }

    private void createMap() {
        Map.getInstance().addLayer("res/Shapefiles/ne_50m_admin_0_sovereignty.shp");

        final SwingNode swingNode = new SwingNode();
        createSwingContent(swingNode, Map.getInstance().getPane());

        mapScrollPane.setContent(swingNode);
    }

    private void createSwingContent(final SwingNode swingNode, final JComponent content) {
        SwingUtilities.invokeLater(() -> swingNode.setContent(content));
    }

}
