import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.format.DateTimeFormatter;

//todo implement this
public class ShopSaleListCell extends ListCell<ShopSale> {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Insets nodeInsets = new Insets(0, 0, 5, 0);

    @Override
    public void updateItem(ShopSale item, boolean empty) {
        super.updateItem(item, empty);
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

        for (var currency : sale.getCurrencies()) {
            var displayCell = new CurrencyDisplayCell(
                    currency,
                    GlobalData.getCurrencyIcons().get(currency.getCurrencyName()),
                    24d,
                    12
            );
            currenciesRoot.getChildren().add(displayCell);
        }
        root.getChildren().addAll(dateLabel, itemNameText, categoryParent, currenciesText, currenciesRoot);
        return root;
    }
}
