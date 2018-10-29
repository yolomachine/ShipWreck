package GUI.Controls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ListViewRow<T> extends HBox {

    private T target;

    private Label label = new Label();

    ListViewRow() {
        add(label);
    }

    ListViewRow(String label) {
        setLabel(label);
        add(this.label);
    }

    ListViewRow(String label, Node... nodes) {
        this(label);
        addAll(nodes);
    }

    public void setLabel(String text) {
        label.setText(text);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);
    }

    public void setLabelAlignment(Pos pos) {
        label.setAlignment(pos);
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public T getTarget() {
        return target;
    }

    public void add(Node node) {
        getChildren().add(node);
        setMargin(node, new Insets(2, 2, 2, 2));
    }

    public void addAll(Node... nodes) {
        for (Node node : nodes) {
            add(node);
        }
    }
}
