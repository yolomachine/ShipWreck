package GUI.Controls;

import javafx.scene.control.Tooltip;

public class InteractiveNode {

    public enum Type {
        Root,
        Ship,
        Route;
    }

    private Type type;
    private String text;

    public InteractiveNode(String text, Type type) {
        this.type = type;
        this.text = text;
    }

    public Type getType() {
        return type;
    }

    public Tooltip getTooltip() {
        return null;
    }

    @Override
    public String toString() {
        return text;
    }

}
