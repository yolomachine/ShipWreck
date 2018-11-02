package GUI.StageControllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.converter.DoubleStringConverter;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class RouteEditController implements Initializable {

    @FXML
    private GridPane routeEditViewGridPane;

    @FXML
    private TextField nameTextField;

    @FXML
    private Spinner<Double> startLatitudeSpinner;

    @FXML
    private Spinner<Double> startLongitudeSpinner;

    @FXML
    private Spinner<Double> destinationLatitudeSpinner;

    @FXML
    private Spinner<Double> destinationLongitudeSpinner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DecimalFormat format = new DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        UnaryOperator<TextFormatter.Change> filter = c -> {
            if (c.isContentChange()) {
                ParsePosition parsePosition = new ParsePosition(0);
                format.parse(c.getControlNewText(), parsePosition);
                if (parsePosition.getIndex() == 0 ||
                        parsePosition.getIndex() < c.getControlNewText().length()) {
                    return  null;
                }
            }
            return c;
        };
        for (Node node : routeEditViewGridPane.getChildren()) {
            TextFormatter<Double> spinnerFormatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, filter);
            if (node instanceof Spinner) {
                Spinner spinner = (Spinner) node;
                spinner.setEditable(true);
                spinner.getEditor().setTextFormatter(spinnerFormatter);
            }
        }
        nameTextField.setEditable(true);
        startLatitudeSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(-90, 90, 0, 1));
        startLongitudeSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(-180, 180, 0, 1));
        destinationLatitudeSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(-90, 90, 0, 1));
        destinationLongitudeSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(-180, 180, 0, 1));
    }
}
