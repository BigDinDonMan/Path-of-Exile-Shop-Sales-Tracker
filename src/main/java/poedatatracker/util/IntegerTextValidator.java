package poedatatracker.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IntegerTextValidator implements ChangeListener<String> {

    private TextField target;
    private boolean negativesAllowed;

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
        if (target != null) {
            String pattern = negativesAllowed ? "-?\\d*" : "\\d*";
            if (!newValue.matches(pattern)) {
                target.setText(oldValue);
            }
        }
    }
}
