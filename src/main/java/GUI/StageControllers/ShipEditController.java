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

public class ShipEditController implements Initializable {

    @FXML
    private GridPane shipEditViewGridPane;

    @FXML
    public TextField nameTextField;

    @FXML
    public Spinner<Double> displacementSpinner;

    @FXML
    public Spinner<Double> serviceSpeedSpinner;

    @FXML
    public Spinner<Double> fuelAmountSpinner;

    @FXML
    public Spinner<Double> fuelConsumptionRateSpinner;

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
        for (Node node : shipEditViewGridPane.getChildren()) {
            TextFormatter<Double> spinnerFormatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, filter);
            if (node instanceof Spinner) {
                Spinner spinner = (Spinner) node;
                spinner.setEditable(true);
                spinner.getEditor().setTextFormatter(spinnerFormatter);
            }
        }
        nameTextField.setEditable(true);
        displacementSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 20000, 0, 500));
        serviceSpeedSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 500, 0, 10));
        fuelAmountSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1000, 0, 100));
        fuelConsumptionRateSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1, 0, 0.01));
    }
}
