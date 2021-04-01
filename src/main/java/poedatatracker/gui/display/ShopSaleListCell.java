package poedatatracker.gui.display;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import poedatatracker.core.GlobalData;
import poedatatracker.core.models.ItemCategory;
import poedatatracker.core.models.ShopSale;
import poedatatracker.util.ItemCategoryToNameMapper;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

//todo implement this
public class ShopSaleListCell extends ListCell<ShopSale> {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Insets nodeInsets = new Insets(0, 0, 5, 0);

    private static final Tooltip clickTooltip = new Tooltip("Ctrl + click to open reference in browser");

    private static final String BASE_WIKI_URL = "https://pathofexile.gamepedia.com/";

    private static final List<ItemCategory> searchableCategories = Arrays.asList(
            ItemCategory.UNIQUE,
            ItemCategory.PROPHECY,
            ItemCategory.SKILL_GEM
    );

    private ShopSale sale;

    public ShopSaleListCell() {
        super();
        addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.isPrimaryButtonDown() && e.isControlDown()) {
                if (sale != null && searchableCategories.contains(sale.getItem().getCategory())) {
                    var mapper = new ItemCategoryToNameMapper();
                    String name = mapper.apply(sale.getItem().getName(), sale.getItem().getCategory());
                    String urlEncodedName = URLEncoder.encode(name.replace(' ', '_'), StandardCharsets.UTF_8);
                    try {
                        openInBrowser(urlEncodedName);
                    } catch (URISyntaxException | IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void openInBrowser(String encoded) throws URISyntaxException, IOException {
        String url = BASE_WIKI_URL + encoded;
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(url));
        }
    }

    @Override
    public void updateItem(ShopSale item, boolean empty) {
        super.updateItem(item, empty);
        this.sale = item;
        setGraphic(isEmpty() ? null : createDisplayControl(item));
        setText(null);
    }

    private Node createDisplayControl(ShopSale sale) {
        var root = new VBox();
        var dateLabel = new Text(sale.getSaleDate().format(dateFormatter));
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        VBox.setMargin(dateLabel, nodeInsets);

        var item = sale.getItem();
        var amount = item.getAmount();
        var itemNameText = new Text(amount > 1 ? String.format("%dx %s", amount, item.getName()) : item.getName());
        VBox.setMargin(itemNameText, nodeInsets);

        var categoryParent = new HBox();
        categoryParent.setAlignment(Pos.CENTER_LEFT);

        var categoryText = new Text("Category: ");
        var itemCategoryText = new Text(item.getCategory().prettifyName());
        categoryParent.getChildren().addAll(categoryText, itemCategoryText);
        itemCategoryText.setFill(ItemCategory.getCategoryColor(item.getCategory()));
        itemCategoryText.setStyle("-fx-font-weight: bold;");
        VBox.setMargin(categoryParent, nodeInsets);
        var currenciesText = new Text("Currencies: ");
        VBox.setMargin(currenciesText, nodeInsets);
        var currenciesRoot = new VBox();

        sale.getCurrencies().forEach(c -> {
            var displayCell = new CurrencyDisplayCell(
                    c,
                    GlobalData.getCurrencyIcons().get(c.getCurrencyName()),
                    24d,
                    12
            );
            currenciesRoot.getChildren().add(displayCell);
        });
        root.getChildren().addAll(dateLabel, itemNameText, categoryParent, currenciesText, currenciesRoot);
        return root;
    }
}
