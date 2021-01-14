import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

//todo: make application go into system tray after closing/minimizing the window
//todo: add undo to adding last shop sale
//todo: add progress bar/progress window when saving records to database
//todo: add item category icons in combobox
public class MainWindowController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private Label statusLabel;

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

    @FXML
    private ComboBox<LocalDate> dateFilterComboBox;

    @FXML
    private ComboBox<ItemCategory> categoryFilterComboBox;

    @FXML
    private Button clearFiltersButton;

    private ContextMenu itemNamesAutoCompleteMenu;
    private List<MenuItem> autoCompleteData;
    private BiFunction<String, ItemCategory, String> nameMapper;
    private SortedList<ShopSale> sortedSaleList;
    private FilteredList<ShopSale> filteredSaleList;

    private List<ShopSale> recentlyAddedSalesList;

    private BooleanProperty unsavedChangesPresent;

    private AtomicBoolean clearingFilters;

    public MainWindowController() {
        clearingFilters = new AtomicBoolean(false);
        unsavedChangesPresent = new SimpleBooleanProperty();
        recentlyAddedSalesList = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GlobalData.initialize();
        unsavedChangesPresent.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> statusLabel.setText(newValue ? "Unsaved changes!" : ""));
        });

        setUpListViewAndAutoCompleter();
        setUpNewSaleForm();
        setUpSaleFilters();
    }

    //<editor-fold desc="setup methods">
    private void setUpListViewAndAutoCompleter() {
        shopSalesListView.setCellFactory(c -> new ShopSaleListCell());
        List<ShopSale> sales = ApplicationDatabase.fetchAllSales();
        Platform.runLater(() -> {
            shopSalesListView.getItems().addAll(sales);
        });
        nameMapper = new ItemCategoryToNameMapper();
        itemNamesAutoCompleteMenu = new ContextMenu();
        itemNamesAutoCompleteMenu.setPrefWidth(itemNameTextField.getWidth());
        autoCompleteData = sales.stream().
                map(sale -> nameMapper.apply(sale.getItem().getName(), sale.getItem().getCategory())).
                filter(name -> !name.isBlank()).distinct().
                map(s -> {
                    MenuItem item = new MenuItem(s);
                    item.setOnAction(e -> {
                        itemNameTextField.setText(item.getText());
                        itemNameTextField.positionCaret(item.getText().length());
                    });
                    return item;
                }).collect(Collectors.toList());
    }

    private void setUpNewSaleForm() {
        itemNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            itemNamesAutoCompleteMenu.getItems().clear();
            String input = itemNameTextField.getText().toLowerCase();
            if (input.isBlank()) return;

            autoCompleteData.
                    stream().
                    filter(i -> i.getText().toLowerCase().startsWith(input)).
                    limit(10).
                    forEach(e -> itemNamesAutoCompleteMenu.getItems().add(e));

            if (!itemNamesAutoCompleteMenu.isShowing()) {
                itemNamesAutoCompleteMenu.show(itemNameTextField, Side.BOTTOM, 0, 0);
            }
        });

        itemNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                itemNamesAutoCompleteMenu.hide();
            }
        });

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

        currenciesListView.setCellFactory(c -> new ListCell<>() {
            @Override
            protected void updateItem(ReceivedCurrency item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(
                        !isEmpty() ?
                                new CurrencyDisplayCell(item, GlobalData.getCurrencyIcons().get(item.getCurrencyName()), 32d, 14) :
                                null
                );
                setText(null);
        }
        });
    }

    private void setUpSaleFilters() {
        dateFilterComboBox.getItems().addAll(
                ApplicationDatabase.fetchSaleDates(true)
        );

        dateFilterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!clearingFilters.get()) {
                filterSales();
            }
        });

        dateFilterComboBox.setButtonCell(new ListCell<>() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(isEmpty() ? dateFilterComboBox.getPromptText() : date.toString());
                setGraphic(null);
            }
        });

        categoryFilterComboBox.setButtonCell(new ListCell<>() {
            @Override
            public void updateItem(ItemCategory item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    Text categoryText = new Text(item.prettifyName());
                    categoryText.setFill(ItemCategory.getCategoryColor(item));
                    setGraphic(categoryText);
                } else {
                    setGraphic(new Text(categoryFilterComboBox.getPromptText()));
                }
                setText(null);
            }
        });

        categoryFilterComboBox.setCellFactory(c -> new ListCell<>(){
            @Override
            public void updateItem(ItemCategory item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    Text categoryText = new Text(item.prettifyName());
                    categoryText.setFill(ItemCategory.getCategoryColor(item));
                    setGraphic(categoryText);
                } else {
                    setGraphic(null);
                }
                setText(null);
            }
        });

        categoryFilterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!clearingFilters.get()) {
                filterSales();
            }
        });

        categoryFilterComboBox.getItems().addAll(ItemCategory.values());
    }
    //</editor-fold>

    private void filterSales() {
        LocalDate predicateDate = dateFilterComboBox.getSelectionModel().getSelectedItem();
        ItemCategory predicateCategory = categoryFilterComboBox.getSelectionModel().getSelectedItem();
        var matchedSales = ApplicationDatabase.fetchSalesMatching(predicateDate, predicateCategory);
        shopSalesListView.getItems().clear();
        shopSalesListView.getItems().addAll(matchedSales);
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
        String itemName = itemNameTextField.getText().strip();
        if (itemName == null || itemName.isBlank()) {
            new Alert( Alert.AlertType.ERROR, "Please input a valid item name.").showAndWait();
            return;
        }
        int itemAmount = 1;
        try {
            itemAmount = Integer.parseInt(itemAmountTextField.getText());
        } catch (NumberFormatException nfe) {
            new Alert(Alert.AlertType.ERROR, "Please input a numeric input UwU").showAndWait();
            return;
        }
        List<ReceivedCurrency> currencies = new ArrayList<>(currenciesListView.getItems());
        if (currencies.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "You cannot sell an item for free. Please select valid currencies.").showAndWait();
            return;
        }
        ItemCategory category = itemCategoryComboBox.getSelectionModel().getSelectedItem();
        if (category == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a valid item category").showAndWait();
            return;
        }
        LocalDate date = saleDatePicker.getValue();
        if (date == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a valid date from the date picker.").showAndWait();
            return;
        }
        ShopSale sale = new ShopSale(0L, date, currencies, new SoldItem(itemName, itemAmount, category));
        recentlyAddedSalesList.add(sale);
        shopSalesListView.getItems().add(sale);
        unsavedChangesPresent.setValue(true);
        onSaleAdded(sale);
        clearMandatoryInputs();
    }

    @FXML
    private void saveShopSales() {
        if (recentlyAddedSalesList.isEmpty()) {
            return;
        }
        Session session = ApplicationDatabase.getNewSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            recentlyAddedSalesList.forEach(sale -> {
                var item = sale.getItem();
                item.setSale(sale);
                var currencies = sale.getCurrencies();
                session.save(sale);
                session.save(item);
                currencies.forEach(c -> {
                    c.setSale(sale);
                    session.save(c);
                });
            });
            transaction.commit();
            recentlyAddedSalesList.clear();
            unsavedChangesPresent.setValue(false);
            new Alert(Alert.AlertType.INFORMATION, "Sales data saved successfully to database").showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage() + "\nRolling back...").showAndWait();
        } finally {
            session.close();
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

    private void onSaleAdded(ShopSale s) {
        final String name = nameMapper.apply(s.getItem().getName(), s.getItem().getCategory());
        if (autoCompleteData.stream().noneMatch(item -> item.getText().equals(name))) {
            final MenuItem mi = new MenuItem(name);
            mi.setOnAction(e -> {
                itemNameTextField.setText(mi.getText());
                itemNameTextField.positionCaret(mi.getText().length());
            });
            autoCompleteData.add(mi);
        }

        if (!dateFilterComboBox.getItems().contains(s.getSaleDate())) {
            dateFilterComboBox.getItems().add(s.getSaleDate());
            dateFilterComboBox.getItems().sort(Comparator.naturalOrder());
        }
    }

    @FXML
    private void clearFilters() {
        clearingFilters.set(true);
        dateFilterComboBox.getSelectionModel().clearSelection();
        categoryFilterComboBox.getSelectionModel().clearSelection();
        clearingFilters.set(false);
        filterSales();
    }

    @FXML
    private void clearCurrencies() {
        currenciesListView.getItems().clear();
    }

    public void shutdown() {
        if (!recentlyAddedSalesList.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "You still have unsaved changes! Save now?");
            Optional<ButtonType> result = a.showAndWait();
            result.ifPresent(e -> {
                if (e.equals(ButtonType.OK)) {
                    saveShopSales();
                }
            });
        }
    }

    @FXML
    private void loadLogFiles() {
        FileChooser fc = new FileChooser();
        var txtFilter = new FileChooser.ExtensionFilter("Txt log files (*.txt)", "*.txt");
        fc.getExtensionFilters().add(txtFilter);
        var files = fc.showOpenMultipleDialog(root.getScene().getWindow());
        if (files != null && !files.isEmpty()) {
            var fileLoader = new LogFileLoader(",", GlobalData.getCurrencies());
            var sales = new ArrayList<ShopSale>();
            files.forEach(f -> {
                try {
                    var loaded = fileLoader.load(f.getAbsolutePath());
                    sales.addAll(loaded);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            var dialog = new LoadedShopSaleViewDialog(sales);
            dialog.setTitle("Loaded sales display");
            Optional<ButtonType> decision = dialog.showAndWait();
            decision.ifPresent(type -> {
                if (!type.equals(ButtonType.OK)) return;
                //if all is good then add everything to list or database
                recentlyAddedSalesList.addAll(sales);
                saveShopSales();
            });
        }
    }

    @FXML
    private void undoRecentSale() {
        if (recentlyAddedSalesList.isEmpty()) {
            return;
        }
        var toDelete = recentlyAddedSalesList.get(recentlyAddedSalesList.size() - 1);
        recentlyAddedSalesList.remove(toDelete);
        shopSalesListView.getItems().remove(toDelete);
        if (recentlyAddedSalesList.isEmpty()) {
            unsavedChangesPresent.setValue(false);
        }
    }
}
