import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    private ListView<ReceivedCurrency> currenciesListView;

    @FXML
    private Button addCurrencyButton;

    @FXML
    private TextField itemNameTextField;

    @FXML
    private TextField itemAmountTextField;

    @FXML
    private DatePicker saleDatePicker;

    @FXML
    private ComboBox<ItemCategory> itemCategoryComboBox;

    @FXML
    private ComboBox<String> currencyComboBox;

    @FXML
    private TextField currencyAmountTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GlobalResources.initialize();

        itemCategoryComboBox.setCellFactory(callback -> new ListCell<>() {
            @Override
            protected void updateItem(ItemCategory item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    setText(item.prettifyName());
                    setGraphic(null);
                }
            }
        });
    }
}
