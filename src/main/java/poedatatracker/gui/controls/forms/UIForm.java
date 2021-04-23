package poedatatracker.gui.controls.forms;

import javafx.scene.layout.AnchorPane;

import java.util.List;

public abstract class UIForm<T> extends AnchorPane {

    public UIForm() {
        super();
    }

    public abstract void clearInputs();
    public abstract T createFromFormData();
    public abstract List<T> getInputElements();
}
