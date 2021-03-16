import javafx.scene.control.ToggleButton;
import lombok.Getter;

@Getter
public class CategoryToggleButton extends ToggleButton {
    private PoEServiceType serviceCategory;

    public CategoryToggleButton(PoEServiceType type) {
        super();
        this.serviceCategory = type;
    }
}
