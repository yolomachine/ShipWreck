package GUI.Controls;

import javafx.scene.control.Button;

public class CustomButton extends Button {

    public CustomButton(String label, ActionDelegate actionDelegate) {
        setText(label);
        setOnAction(actionDelegate);
    }

    public void setOnAction(ActionDelegate action) {
        setOnAction(event -> action.invoke());
    }
}
