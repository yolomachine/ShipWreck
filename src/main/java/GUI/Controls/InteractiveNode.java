package GUI.Controls;

import javafx.scene.control.Tooltip;

public class InteractiveNode {

    public enum Type {
        Root,
        Ship,
        Route;
    }

    protected int id = -1;
    protected Type type;
    protected String name;

    public InteractiveNode(String name, Type type) {
        this.type = type;
        this.name  = name;
    }

    public InteractiveNode(int id, String name, Type type) {
        this(name, type);
        this.id = id;
    }

    public void invalidate() { }

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
        return name;
    }

}
