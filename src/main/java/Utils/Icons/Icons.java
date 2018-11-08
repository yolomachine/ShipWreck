package Utils.Icons;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Icons {

    private static Icons ourInstance = new Icons();

    public static Icons getInstance() {
        return ourInstance;
    }

    private Icons() { }

    public ImageView getShipIcon() {
        return new ImageView(new Image("file:res/Icons/ship.png"));
    }

    public ImageView getShipIcon(int size) {
        ImageView icon = getShipIcon();
        icon.setFitWidth(size);
        icon.setFitHeight(size);
        return icon;
    }

    public ImageView getRouteIcon() {
        return new ImageView(new Image("file:res/Icons/route.png"));
    }

    public ImageView getRouteIcon(int size) {
        ImageView icon = getRouteIcon();
        icon.setFitWidth(size);
        icon.setFitHeight(size);
        return icon;
    }

}
