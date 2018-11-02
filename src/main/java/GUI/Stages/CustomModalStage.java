package GUI.Stages;

import GUI.Controls.ActionDelegate;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;

public class CustomModalStage<T> extends CustomStage {

    protected T target;
    private Button confirmButton;
    private Button cancelButton;

    CustomModalStage(String rootLoc, String iconLoc, String title, int minWidth, int minHeight, ActionDelegate onCreate) {
        super(rootLoc, iconLoc, title, minWidth, minHeight, onCreate);
        initModality(Modality.APPLICATION_MODAL);
        Scene scene = getScene();
        confirmButton = (Button) scene.lookup("#confirmButton");
        cancelButton = (Button) scene.lookup("#cancelButton");
    }

    void setOnConfirm(ActionDelegate action) {
        confirmButton.setOnAction(e -> action.invoke());
    }

    void setOnCancel(ActionDelegate action) {
        cancelButton.setOnAction(e -> action.invoke());
    }

    public T getData() {
        return target;
    }

}
