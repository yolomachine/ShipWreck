package GUI.Controls;

import Database.Database;
import Utils.Icons;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;

public class CustomTreeView extends TreeView<InteractiveNode> {

    private class CustomContextMenu {
        private final ContextMenu contextMenu;
        private final MenuItem insert;
        private final MenuItem edit;
        private final MenuItem delete;

        CustomContextMenu() {
            this.insert = new MenuItem("Add...");
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

    private class CustomTreeCell extends TextFieldTreeCell<InteractiveNode> {
        private final CustomContextMenu contextMenu;

        CustomTreeCell(CustomContextMenu contextMenu) {
            super(new StringConverter<InteractiveNode>() {
                @Override
                public String toString(InteractiveNode interactiveNode) {
                    return interactiveNode.toString();
                }

                @Override
                public InteractiveNode fromString(String s) {
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

        private void prepareContextMenu(TreeItem<InteractiveNode> item) {
            boolean root = item.getParent() == null;
            boolean route = !root && item.getParent().getValue().getType() == InteractiveNode.Type.Ship;
            MenuItem insert = contextMenu.getInsertMenuItem();
            MenuItem edit = contextMenu.getEditMenuItem();
            MenuItem delete = contextMenu.getDeleteMenuItem();
            switch (item.getValue().getType()) {
                case Root:
                    insert.setText("Add new ship");
                    break;
                case Ship:
                    insert.setText("Add new route");
            }
            insert.setDisable(route);
            edit.setDisable(root);
            delete.setDisable(root);
            if (!root) {
                edit.setOnAction(e -> {
                    InteractiveNode edited = InteractiveNodeHandler.getInstance().edit(item.getParent().getValue(), item.getValue());
                    if (edited != null) {
                        item.setValue(edited);
                    }
                    contextMenu.freeActionListeners();
                });
                delete.setOnAction(e -> {
                    InteractiveNodeHandler.getInstance().delete(item.getValue());
                    item.getParent().getChildren().remove(item);
                    contextMenu.freeActionListeners();
                });
            }
            if (!route) {
                insert.setOnAction(e -> {
                    InteractiveNode newItem = InteractiveNodeHandler.getInstance().create(item.getValue());
                    if (newItem != null) {
                        item.getChildren().add(new TreeItem<>(newItem));
                    }
                    contextMenu.freeActionListeners();
                });
            }
        }

        @Override
        public void updateItem(InteractiveNode item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContextMenu(contextMenu.getContextMenu());
                setEditable(true);
                setTooltip(item.getTooltip());
                switch (item.getType()) {
                    case Ship:
                        setGraphic(Icons.getInstance().getShipIcon(20));
                        break;
                    case Route:
                        setGraphic(Icons.getInstance().getRouteIcon(20));
                        break;
                }
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

        setRoot(new TreeItem<>(new InteractiveNode("Ships", InteractiveNode.Type.Root)));
        for (Ship ship : Database.getInstance().getShipsTable().selectAll()) {
            TreeItem<InteractiveNode> item = new TreeItem<>(ship);
            for (Route route : Database.getInstance().getRoutesTable().selectWhereShipId(ship.getId())) {
                item.getChildren().add(new TreeItem<>(route));
            }
            getRoot().getChildren().add(item);
        }
        expandTreeView(getRoot());
        setPrefSize(200, 200);
    }

    private void expandTreeView(TreeItem<?> item){
        if (item != null && !item.isLeaf()){
            item.setExpanded(true);
            for (TreeItem<?> child : item.getChildren()){
                expandTreeView(child);
            }
        }
    }
}
