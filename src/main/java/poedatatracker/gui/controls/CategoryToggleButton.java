package poedatatracker.gui.controls;

import javafx.scene.control.ToggleButton;
import lombok.Getter;
import poedatatracker.core.models.PoEServiceType;

@Getter
public class CategoryToggleButton extends ToggleButton {
    private PoEServiceType serviceCategory;

    public CategoryToggleButton(PoEServiceType type) {
        super();
        this.serviceCategory = type;
    }
}
