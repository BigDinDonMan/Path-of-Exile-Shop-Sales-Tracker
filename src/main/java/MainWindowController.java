import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.controlsfx.dialog.ProgressDialog;
import poedatatracker.core.ApplicationDatabase;
import poedatatracker.core.GlobalData;
import poedatatracker.core.commands.AddToListCommand;
import poedatatracker.core.models.*;
import poedatatracker.gui.controls.CategoryToggleButton;
import poedatatracker.gui.display.*;
import poedatatracker.gui.dialogs.LoadedShopSaleViewDialog;
import poedatatracker.util.ItemCategoryToNameMapper;
import poedatatracker.util.loaders.SaleLogFileLoader;
import poedatatracker.util.IntegerTextValidator;
import poedatatracker.util.SaveDataToDatabaseTask;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

//todo: add item category icons in combobox
//todo: add replacing the words in parentheses when opening url in browser (e.g. (red) in "combat focus (red)" or "very large ring" in thread of hope (very large ring)
//todo: start thinking about stats display
//todo: add currency exchange display and form
//fixme: create separate user controls for each of the forms (sales, services, gems and exchanges)
//todo: add more currencies to resources and more currency images
//fixme: disable hibernate logs
public class MainWindowController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private HBox serviceCategoryTogglesParent;

    @FXML
    private Label statusLabel;

    @FXML
    private ListView<ShopSale> shopSalesListView;

    @FXML
    private ListView<PoEService> servicesListView;

    @FXML
    private ListView<LevelledSkillGem> gemsListView;

    @FXML
    private ListView<ReceivedCurrency> currenciesListView;

    @FXML
    private ListView<PoEServicePayment> paymentsListView;

    @FXML
    private TextField itemNameTextField;

    @FXML
    private TextField itemAmountTextField;

    @FXML
    private TextField serviceNameTextField;

    @FXML
    private TextField timesPerformedTextField;

    @FXML
    private DatePicker saleDatePicker;

    @FXML
    private DatePicker serviceDatePicker;

    @FXML
    private ComboBox<ItemCategory> itemCategoryComboBox;

    @FXML
    private ComboBox<String> currencyComboBox;

    @FXML
    private ComboBox<String> paymentsComboBox;

    @FXML
    private TextField paymentAmountTextField;

    @FXML
    private TextField currencyAmountTextField;

    @FXML
    private ComboBox<LocalDate> dateFilterComboBox;

    @FXML
    private ComboBox<ItemCategory> categoryFilterComboBox;

    //<editor-fold desc="Gem data fields">
    @FXML
    private ComboBox<GemQualityType> gemQualityTypeComboBox;

    @FXML
    private ComboBox<GemType> gemTypeComboBox;

    @FXML
    private TextField gemNameTextField;

    @FXML
    private TextField gemMaxLevelTextField;

    @FXML
    private TextField gemQualityTextField;

    @FXML
    private CheckBox isGemCorruptedCheckBox;

    @FXML
    private DatePicker levellingDatePicker;

    @FXML
    private ListView<LevelledSkillGem> currentlyAddedGemsListView;
    //</editor-fold>

    private ToggleGroup serviceTypeToggleGroup;

    //<editor-fold desc="Autocompleters data">
    private ContextMenu gemNamesAutoCompleteMenu;
    private List<MenuItem> autoCompleteGemData;

    private ContextMenu itemNamesAutoCompleteMenu;
    private List<MenuItem> autoCompleteSalesData;
    //</editor-fold>

    private BiFunction<String, ItemCategory, String> nameMapper;

    private List<ShopSale> recentlyAddedSalesList;
    private List<PoEService> recentlyAddedServicesList;
    private List<LevelledSkillGem> recentlyAddedGemsList;

    private Stack<AddToListCommand<? extends Object>> undoCommands;
    private Stack<AddToListCommand<? extends Object>> redoCommands;

    private BooleanProperty unsavedChangesPresent;

    private AtomicBoolean clearingFilters;

    private Function<String, EventHandler<ActionEvent>> gemAutoCompleteHandler;

    public MainWindowController() {
        clearingFilters = new AtomicBoolean(false);
        unsavedChangesPresent = new SimpleBooleanProperty();
        recentlyAddedSalesList = new ArrayList<>();
        recentlyAddedGemsList = new ArrayList<>();
        recentlyAddedServicesList = new ArrayList<>();
        undoCommands = new Stack<>();
        redoCommands = new Stack<>();
        gemAutoCompleteHandler = s -> e -> {
            var parts = s.split(",");
            gemNameTextField.setText(parts[0]);
            gemNameTextField.positionCaret(gemNameTextField.getText().length());
            gemMaxLevelTextField.setText(parts[2].substring(parts[2].indexOf("max level:") + "max level:".length()).strip());
            gemTypeComboBox.getSelectionModel().select(GemType.valueOf(parts[1].strip().toUpperCase()));
        };
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
        setUpCategoryToggleButtons();

        setUpNewServiceForm();
        setUpNewGemForm();
        setUpGemAutoCompleter();
    }

    //<editor-fold desc="setup methods">

    private void setUpGemAutoCompleter() {
        gemNamesAutoCompleteMenu = new ContextMenu();
        gemNamesAutoCompleteMenu.setPrefWidth(gemNameTextField.getWidth());
        autoCompleteGemData = new ArrayList<>();
        var gemData = ApplicationDatabase.fetchGemData();
        autoCompleteGemData =
                gemData.stream().map(s -> {
                    var item = new MenuItem(s);
                    item.setOnAction(gemAutoCompleteHandler.apply(s));
                    return item;
                }).collect(Collectors.toList());
        gemNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            gemNamesAutoCompleteMenu.getItems().clear();
            String input = gemNameTextField.getText().toLowerCase();
            if (input.isBlank()) return;

            autoCompleteGemData.
                    stream().
                    filter(i -> i.getText().toLowerCase().startsWith(input)).
                    limit(10).
                    forEach(e -> gemNamesAutoCompleteMenu.getItems().add(e));

            if (!gemNamesAutoCompleteMenu.isShowing()) {
                gemNamesAutoCompleteMenu.show(gemNameTextField, Side.BOTTOM, 0, 0);
            }
        });

        gemNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                gemNamesAutoCompleteMenu.hide();
            }
        });
    }

    private void setUpListViewAndAutoCompleter() {
        shopSalesListView.setCellFactory(c -> new ShopSaleListCell());
        List<ShopSale> sales = ApplicationDatabase.fetchAllSales();
        Platform.runLater(() -> {
            shopSalesListView.getItems().addAll(sales);
        });
        nameMapper = new ItemCategoryToNameMapper();
        itemNamesAutoCompleteMenu = new ContextMenu();
        itemNamesAutoCompleteMenu.setPrefWidth(itemNameTextField.getWidth());
        autoCompleteSalesData = sales.stream().
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

            autoCompleteSalesData.
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

        itemCategoryComboBox.setCellFactory(callback -> new SimpleEnumListCell<ItemCategory>());

        itemCategoryComboBox.setButtonCell(new SimpleEnumListCell<ItemCategory>());

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

        itemAmountTextField.textProperty().addListener(new IntegerTextValidator(itemAmountTextField, false));

        currencyAmountTextField.textProperty().addListener(new IntegerTextValidator(currencyAmountTextField, false));

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

    private void setUpNewServiceForm() {
        paymentsComboBox.setCellFactory(callback -> new ListCell<>() {
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
        paymentsComboBox.getItems().addAll(GlobalData.getCurrencies());

        paymentAmountTextField.textProperty().addListener(new IntegerTextValidator(paymentAmountTextField, false));

        timesPerformedTextField.textProperty().addListener(new IntegerTextValidator(timesPerformedTextField, false));

        servicesListView.setCellFactory(callback -> new ServiceListCell());
        paymentsListView.setCellFactory(c -> new ListCell<>() {
            @Override
            protected void updateItem(PoEServicePayment item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(
                        !isEmpty() ?
                                new CurrencyDisplayCell(item, GlobalData.getCurrencyIcons().get(item.getCurrencyName()), 32d, 14) :
                                null
                );
                setText(null);
            }
        });

        servicesListView.getItems().addAll(ApplicationDatabase.fetchAllServices());
    }

    private void setUpNewGemForm() {
        gemsListView.setCellFactory(callback -> new GemDisplayListCell());
        gemMaxLevelTextField.textProperty().addListener(new IntegerTextValidator(gemMaxLevelTextField, false));
        gemQualityTextField.textProperty().addListener(new IntegerTextValidator(gemQualityTextField, false));
        gemQualityTypeComboBox.getItems().addAll(GemQualityType.values());
        gemQualityTypeComboBox.setCellFactory(callback -> new SimpleEnumListCell<GemQualityType>());
        gemQualityTypeComboBox.setButtonCell(new SimpleEnumListCell<GemQualityType>());
        gemTypeComboBox.getItems().addAll(GemType.values());
        gemTypeComboBox.setCellFactory(callback -> new SimpleEnumListCell<GemType>());
        gemTypeComboBox.setButtonCell(new SimpleEnumListCell<GemType>());
        gemsListView.getItems().addAll(ApplicationDatabase.fetchAllGems());
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
        if (!currencyAmountTextField.getText().isBlank() && currencyComboBox.getSelectionModel().getSelectedIndex() != -1) {
            int amount = Integer.parseInt(currencyAmountTextField.getText());
            String name = currencyComboBox.getSelectionModel().getSelectedItem();
            currenciesListView.getItems().add(new ReceivedCurrency(name, amount));
        }
    }

    @FXML
    private void addNewPayment() {
        if (!paymentAmountTextField.getText().isBlank() && paymentsComboBox.getSelectionModel().getSelectedIndex() != -1) {
            int amount = Integer.parseInt(paymentAmountTextField.getText());
            String name = paymentsComboBox.getSelectionModel().getSelectedItem();
            paymentsListView.getItems().add(new PoEServicePayment(name, amount));
        }
    }

    @FXML
    private void addNewService() {
        var selectedToggle = (CategoryToggleButton)serviceTypeToggleGroup.getSelectedToggle();
        if (selectedToggle == null) {
            showErrorDialog("Please select a service category");
            return;
        }
        var serviceType = selectedToggle.getServiceCategory();

        var serviceName = serviceNameTextField.getText().strip();
        if (serviceName.isBlank()) {
            showErrorDialog("Please input a valid service name");
            return;
        }

        var date = serviceDatePicker.getValue();
        if (date == null) {
            showErrorDialog("Please select a valid date");
            return;
        }

        var amount = 1;

        try {
            amount = Integer.parseInt(timesPerformedTextField.getText());
        } catch (NumberFormatException nfe) {
            showErrorDialog("Please input a valid numeric value");
            return;
        }

        var payments = new ArrayList<>(paymentsListView.getItems());

        PoEService service = new PoEService(serviceName, amount, serviceType, date, payments);
        recentlyAddedServicesList.add(service);
        servicesListView.getItems().add(service);
        unsavedChangesPresent.setValue(true);
        clearMandatoryServiceInputs();
        onServiceAdded(service);
    }

    @FXML
    private void addNewGem() {
        var gemName = gemNameTextField.getText().strip();
        if (gemName == null || gemName.isBlank()) {
            showErrorDialog("Please input a valid gem name");
            return;
        }

        var maxLevelStr = gemMaxLevelTextField.getText();
        var qualityStr = gemQualityTextField.getText();
        int maxLevel = maxLevelStr.isBlank() ? 20 : Integer.parseInt(maxLevelStr);
        int quality = qualityStr.isBlank() ? 0 : Integer.parseInt(qualityStr);
        boolean corrupted = isGemCorruptedCheckBox.isSelected();
        if (!corrupted && quality > 20) {
            showErrorDialog("Quality can be over 20 only if the gem is corrupted!");
            return;
        }
        var gemType = gemTypeComboBox.getSelectionModel().getSelectedItem();
        var gemQualityType = gemQualityTypeComboBox.getSelectionModel().getSelectedItem();
        if (gemType == null || gemQualityType == null) {
            showErrorDialog("Please select a valid quality type and/or gem type");
            return;
        }
        var date = levellingDatePicker.getValue();
        if (date == null) {
            showErrorDialog("Please select a valid date");
            return;
        }
        var gem = new LevelledSkillGem(
                gemName,
                maxLevel,
                quality,
                gemQualityType,
                gemType,
                date,
                corrupted
        );
        gemsListView.getItems().add(gem);
        recentlyAddedGemsList.add(gem);
        unsavedChangesPresent.setValue(true);
        onGemAdded(gem);
        clearMandatoryGemInputs();
    }

    @FXML
    private void addNewShopSale() {
        var itemName = itemNameTextField.getText().strip();
        if (itemName == null || itemName.isBlank()) {
            showErrorDialog("Please input a valid item name.");
            return;
        }
        var itemAmount = 1;
        try {
            itemAmount = Integer.parseInt(itemAmountTextField.getText());
        } catch (NumberFormatException nfe) {
            showErrorDialog("Please input a numeric value.");
            return;
        }
        var currencies = new ArrayList<>(currenciesListView.getItems());
        if (currencies.isEmpty()) {
            showErrorDialog("You cannot sell an item for free. Please select valid currencies.");
            return;
        }
        var category = itemCategoryComboBox.getSelectionModel().getSelectedItem();
        if (category == null) {
            showErrorDialog("Please select a valid item category");
            return;
        }
        var date = saleDatePicker.getValue();
        if (date == null) {
            showErrorDialog("Please select a valid date from the date picker.");
            return;
        }
        var sale = new ShopSale(
                0L,
                date,
                currencies,
                new SoldItem(itemName, itemAmount, category)
        );
        recentlyAddedSalesList.add(sale);
        shopSalesListView.getItems().add(sale);
        unsavedChangesPresent.setValue(true);
        onSaleAdded(sale);
        clearMandatorySaleInputs();
    }

    private void showErrorDialog(String message) {
        var errorDialog = new Alert(Alert.AlertType.ERROR, message);
        errorDialog.showAndWait();
    }

    @FXML
    private void saveDbData() {
        if (recentlyAddedSalesList.isEmpty() && recentlyAddedServicesList.isEmpty() && recentlyAddedGemsList.isEmpty()) {
            return;
        }
        Service<Boolean> saveService = new ScheduledService<>() {
            @Override
            protected Task<Boolean> createTask() {
                return new SaveDataToDatabaseTask(recentlyAddedSalesList, recentlyAddedServicesList, recentlyAddedGemsList);
            }
        };
        ProgressDialog progressDialog = new ProgressDialog(saveService);
        saveService.setOnSucceeded(e -> {
            saveService.cancel();
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Successfully saved sales data to your database");
            a.showAndWait();
            progressDialog.close();
            unsavedChangesPresent.setValue(false);
            recentlyAddedSalesList.clear();
            recentlyAddedGemsList.clear();
            recentlyAddedServicesList.clear();
            undoCommands.clear();
            redoCommands.clear();
            currentlyAddedGemsListView.getItems().clear();
        });

        saveService.setOnFailed(e -> {
            saveService.cancel();
            showErrorDialog("Error while saving to database: \n" + e.getSource().getMessage());
            progressDialog.close();
        });

        progressDialog.setTitle("Saving to database...");
        progressDialog.setHeaderText("Saving to database...");
        progressDialog.setOnCloseRequest(e -> {
            saveService.cancel();
            progressDialog.close();
        });

        saveService.start();
        progressDialog.showAndWait();
    }

    private void clearMandatorySaleInputs() {
        currenciesListView.getItems().clear();
        itemNameTextField.setText("");
        itemCategoryComboBox.getSelectionModel().clearSelection();
        currencyComboBox.getSelectionModel().clearSelection();
        itemAmountTextField.setText("");
        currencyAmountTextField.setText("");
    }

    private void clearMandatoryGemInputs() {
        gemNameTextField.setText("");
        gemQualityTextField.setText("");
        isGemCorruptedCheckBox.setSelected(false);
        gemMaxLevelTextField.setText("");
    }

    private void clearMandatoryServiceInputs() {
        serviceNameTextField.setText("");
        serviceTypeToggleGroup.selectToggle(null);
        paymentAmountTextField.setText("");
        timesPerformedTextField.setText("");
    }

    private void onSaleAdded(ShopSale s) {
        final String name = nameMapper.apply(s.getItem().getName(), s.getItem().getCategory());
        if (autoCompleteSalesData.stream().noneMatch(item -> item.getText().equals(name))) {
            final MenuItem mi = new MenuItem(name);
            mi.setOnAction(e -> {
                itemNameTextField.setText(mi.getText());
                itemNameTextField.positionCaret(mi.getText().length());
            });
            autoCompleteSalesData.add(mi);
        }

        if (!dateFilterComboBox.getItems().contains(s.getSaleDate())) {
            dateFilterComboBox.getItems().add(s.getSaleDate());
            dateFilterComboBox.getItems().sort(Comparator.naturalOrder());
        }

        var command = new AddToListCommand<>(recentlyAddedSalesList, s);
        command.addUndoCallback(() -> {
            Platform.runLater(() -> {
                shopSalesListView.getItems().remove(s);
            });
            unsavedChangesPresent.setValue(addedRecordsPresent());
        });
        command.addRedoCallback(() -> {
            Platform.runLater(() -> {
                shopSalesListView.getItems().add(s);
            });
            unsavedChangesPresent.setValue(true);
        });
        undoCommands.add(command);
    }

    private void onGemAdded(LevelledSkillGem lsg) {
        currentlyAddedGemsListView.getItems().add(lsg);
        if (autoCompleteGemData.stream().noneMatch(item -> item.getText().contains(lsg.getGemName()))) {
            final var item = new MenuItem(
                    String.format("%s, %s, max level: %d", lsg.getGemName(), lsg.getGemType().name().toLowerCase(), lsg.getMaxLevel())
            );
            item.setOnAction(gemAutoCompleteHandler.apply(item.getText()));
            autoCompleteGemData.add(item);
        }

        final var command = new AddToListCommand<>(recentlyAddedGemsList, lsg);
        command.addRedoCallback(() -> {
            Platform.runLater(() -> {
                gemsListView.getItems().add(lsg);
                currentlyAddedGemsListView.getItems().add(lsg);
            });
            unsavedChangesPresent.setValue(true);
        });
        command.addUndoCallback(() -> {
            Platform.runLater(() -> {
                gemsListView.getItems().remove(lsg);
                currentlyAddedGemsListView.getItems().remove(lsg);
            });
            unsavedChangesPresent.setValue(addedRecordsPresent());
        });
        undoCommands.add(command);
    }

    private void onServiceAdded(PoEService service) {


        var command = new AddToListCommand<>(recentlyAddedServicesList, service);
        command.addUndoCallback(() -> {
            Platform.runLater(() -> {
                servicesListView.getItems().remove(service);
            });
            unsavedChangesPresent.setValue(addedRecordsPresent());
        });
        command.addRedoCallback(() -> {
            Platform.runLater(() -> {
                servicesListView.getItems().add(service);
            });
            unsavedChangesPresent.setValue(true);
        });
        undoCommands.add(command);
    }

    private boolean addedRecordsPresent() {
        return !recentlyAddedGemsList.isEmpty() || !recentlyAddedServicesList.isEmpty() || !recentlyAddedSalesList.isEmpty();
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

    @FXML
    private void clearPayments() {
        paymentsListView.getItems().clear();
    }

    public void shutdown() {
        if (!recentlyAddedSalesList.isEmpty() || !recentlyAddedGemsList.isEmpty() || !recentlyAddedServicesList.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "You still have unsaved changes! Save now?");
            Optional<ButtonType> result = a.showAndWait();
            result.ifPresent(e -> {
                if (e.equals(ButtonType.OK)) {
                    saveDbData();
                }
            });
        }
    }

    @FXML
    private void loadSaleLogFiles() {
        FileChooser fc = new FileChooser();
        var txtFilter = new FileChooser.ExtensionFilter("Txt log files (*.txt)", "*.txt");
        fc.getExtensionFilters().add(txtFilter);
        var files = fc.showOpenMultipleDialog(root.getScene().getWindow());
        if (files != null && !files.isEmpty()) {
            var fileLoader = new SaleLogFileLoader(",", GlobalData.getCurrencies());
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
                saveDbData();
            });
        }
    }

    @FXML
    private void undo() {
        if (undoCommands.isEmpty()) return;
        var command = undoCommands.pop();
        redoCommands.push(command);
        command.undo();
    }

    @FXML
    private void redo() {
        if (redoCommands.isEmpty()) return;
        var command = redoCommands.pop();
        undoCommands.push(command);
        command.redo();
    }

    private void setUpCategoryToggleButtons() {
        serviceTypeToggleGroup = new ToggleGroup();
        for (var category : PoEServiceType.values()) {
            var button = new CategoryToggleButton(category);
//            button.setGraphic();//todo: add category image here
            button.setText(category.name());
            button.setMaxHeight(Double.MAX_VALUE);
            button.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(button, Priority.ALWAYS);
            serviceTypeToggleGroup.getToggles().add(button);
            serviceCategoryTogglesParent.getChildren().add(button);
        }
    }
}
