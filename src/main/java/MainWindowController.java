import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    private ListView<ShopSale> shopSalesListView;

    @FXML
    private ListView<ReceivedCurrency> currenciesListView;

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
        GlobalData.initialize();

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

        itemCategoryComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ItemCategory item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    setText(item.prettifyName());
                    setGraphic(null);
                }
            }
        });

        itemCategoryComboBox.getItems().addAll(ItemCategory.values());

        currencyComboBox.setCellFactory(callback -> new ListCell<>() {
            private ImageView iv = new ImageView();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    setText(item);
                    iv.setFitWidth(32);
                    iv.setFitHeight(32);
                    iv.setImage(GlobalData.getCurrencyIcons().get(item));
                    setGraphic(iv);
                }
            }
        });

        currencyComboBox.getItems().addAll(GlobalData.getCurrencies());

        itemAmountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*"))
                itemAmountTextField.setText(oldValue);
        });

        currencyAmountTextField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*"))
                currencyAmountTextField.setText(oldValue);
        }));
    }

    @FXML
    private void addNewCurrency() {
        if (!currencyAmountTextField.getText().isBlank() || currencyComboBox.getSelectionModel().getSelectedIndex() != -1) {
            int amount = Integer.parseInt(currencyAmountTextField.getText());
            String name = currencyComboBox.getSelectionModel().getSelectedItem();
            currenciesListView.getItems().add(new ReceivedCurrency(name, amount));
        }
    }

    @FXML
    private void addNewShopSale() {
        String itemName = itemNameTextField.getText();
        int itemAmount = Integer.parseInt(itemAmountTextField.getText());
        List<ReceivedCurrency> currencies = new ArrayList<>(currenciesListView.getItems());
        ItemCategory category = itemCategoryComboBox.getSelectionModel().getSelectedItem();
        LocalDate date = saleDatePicker.getValue();
        ShopSale sale = new ShopSale(new SoldItem(itemName, itemAmount, category), date, currencies);
        GlobalData.getSales().add(sale);
        shopSalesListView.getItems().add(sale);
        clearMandatoryInputs();
    }

    @FXML
    private void saveShopSales() {
        try {
            GlobalData.saveSalesData();
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Sale data saved successfully!");
            a.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearMandatoryInputs() {
        currenciesListView.getItems().clear();
        itemNameTextField.setText("");
        itemCategoryComboBox.getSelectionModel().clearSelection();
        currencyComboBox.getSelectionModel().clearSelection();
        itemAmountTextField.setText("");
        currencyAmountTextField.setText("");
    }
}
