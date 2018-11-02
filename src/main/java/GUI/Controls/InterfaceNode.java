package GUI.Controls;

public class InterfaceNode {

    public enum Type {
        Root,
        Ship,
        Route;
    }

    private Type type;
    private String text;

    public InterfaceNode(String text, Type type) {
        this.type = type;
        this.text = text;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return text;
    }

}
