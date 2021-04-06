package poedatatracker.gui.display;

import javafx.scene.control.ListCell;
import poedatatracker.core.models.DecoratedEnum;

public class SimpleEnumListCell<T extends DecoratedEnum> extends ListCell<T> {

    protected void updateItem(T value, boolean empty) {
        super.updateItem(value, empty);
        setGraphic(null);
        setText(isEmpty() ? null : value.prettifyName());
    }
}
