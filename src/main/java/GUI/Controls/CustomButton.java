package GUI.Controls;

import javafx.scene.control.Button;

interface ActionDelegate {
    void invoke();
}

public class CustomButton extends Button {

    public CustomButton(String label) {
        setText(label);
    }

    public CustomButton(String label, ActionDelegate actionDelegate) {
        setText(label);
        setOnAction(actionDelegate);
    }

    public void setOnAction(ActionDelegate action) {
        setOnAction(event -> action.invoke());
    }
}
