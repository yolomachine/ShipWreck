package GUI.Controls;

import javafx.scene.control.Tooltip;

public class InteractiveNode {

    public enum Type {
        Root,
        Ship,
        Route;
    }

    protected int id = -1;
    private Type type;
    private String text;

    public InteractiveNode(String text, Type type) {
        this.type = type;
        this.text = text;
    }

    public InteractiveNode(int id, String text, Type type) {
        this(text, type);
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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
