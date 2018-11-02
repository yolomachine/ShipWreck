package GUI.Controls;

import Model.Ship;
import Model.ShipDB;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;

public class CustomTreeView extends TreeView<InterfaceNode> {

    private class CustomContextMenu {
        private final ContextMenu contextMenu;
        private final MenuItem insert;
        private final MenuItem edit;
        private final MenuItem delete;

        CustomContextMenu() {
            this.insert = new MenuItem("Insert...");
            this.edit = new MenuItem("Edit");
            this.delete = new MenuItem("Delete");
            this.contextMenu = new ContextMenu(insert, edit, delete);
        }

        ContextMenu getContextMenu() {
            return contextMenu;
        }

        MenuItem getInsertMenuItem() {
            return insert;
        }

        MenuItem getEditMenuItem() {
            return edit;
        }

        MenuItem getDeleteMenuItem() {
            return delete;
        }

        void freeActionListeners() {
            this.insert.setOnAction(null);
            this.edit.setOnAction(null);
            this.delete.setOnAction(null);
        }

    }

    private class CustomTreeCell extends TextFieldTreeCell<InterfaceNode> {
        private final CustomContextMenu contextMenu;

        CustomTreeCell(CustomContextMenu contextMenu) {
            super(new StringConverter<InterfaceNode>() {
                @Override
                public String toString(InterfaceNode interfaceNode) {
                    return interfaceNode.toString();
                }

                @Override
                public InterfaceNode fromString(String s) {
                    return null;
                }
            });
            if (contextMenu == null) {
                throw new NullPointerException();
            }
            this.contextMenu = contextMenu;
            this.setOnContextMenuRequested(evt -> {
                prepareContextMenu(getTreeItem());
                evt.consume();
            });
        }

        private void prepareContextMenu(TreeItem<InterfaceNode> item) {
            boolean root = item.getParent() == null;
            MenuItem insert = contextMenu.getInsertMenuItem();
            MenuItem edit = contextMenu.getEditMenuItem();
            MenuItem delete = contextMenu.getDeleteMenuItem();
            delete.setDisable(root);
            if (!root) {
                edit.setOnAction(e -> {
                    switch (item.getValue().getType()) {
                        case Ship:
                            Ship edited = ShipHandler.getInstance().editShip((Ship) item.getValue());
                            if (edited != null) {
                                item.setValue(edited);
                            }
                            break;
                    }
                    contextMenu.freeActionListeners();
                });
                delete.setOnAction(e -> {
                    switch (item.getValue().getType()) {
                        case Ship:
                            ShipHandler.getInstance().deleteShip((Ship) item.getValue());
                            break;
                    }
                    item.getParent().getChildren().remove(item);
                    contextMenu.freeActionListeners();
                });
            }
            insert.setOnAction(e -> {
                switch (item.getValue().getType()) {
                    case Root:
                        Ship ship = ShipHandler.getInstance().createShip();
                        if (ship != null) {
                            item.getChildren().add(new TreeItem<>(ship));
                        }
                        break;
                }
                contextMenu.freeActionListeners();
            });
        }

        @Override
        public void updateItem(InterfaceNode item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContextMenu(contextMenu.getContextMenu());
                setEditable(true);
                if (item.getType() == InterfaceNode.Type.Root) {
                    return;
                }
                final Tooltip tooltip = new Tooltip();
                switch(item.getType()) {
                    case Ship:
                        Ship ship = (Ship) item;
                        tooltip.setText(
                                String.format(
                                        "Id: %d, Tonnage: %e, MaxVelocity: %e, FuelAmount: %e, FuelConsumptionRate: %e",
                                        ship.getId(), ship.getTonnage(), ship.getMaxVelocity(), ship.getFuelAmount(), ship.getFuelConsumptionRate()
                                )
                        );
                        break;
                }
                setTooltip(tooltip);
            }
        }
    }

    private static final CustomTreeView ourInstance = new CustomTreeView();

    public static CustomTreeView getInstance() {
        return ourInstance;
    }

    private CustomTreeView() {
        super();

        setCellFactory(p -> new CustomTreeCell(new CustomContextMenu()));

        setRoot(new TreeItem<>(new InterfaceNode("Ships", InterfaceNode.Type.Root)));
        for (Ship ship : ShipDB.getInstance().getShips()) {
            getRoot().getChildren().add(new TreeItem<>(ship));
        }
        setPrefSize(200, 200);
    }
}
