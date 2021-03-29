package poedatatracker.gui;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import poedatatracker.core.models.ReceivedCurrency;

import java.util.Arrays;
import java.util.List;

public class CurrencyDisplayCell extends HBox {

    private ReceivedCurrency displayCurrency;
    private Image icon;
    private double iconSize = 24d;
    private int fontSize = 14;

    public CurrencyDisplayCell() {
        super();
    }

    public CurrencyDisplayCell(ReceivedCurrency currency, Image icon, double iconSize, int fontSize) {
        this.displayCurrency = currency;
        this.iconSize = iconSize;
        this.icon = icon;
        this.fontSize = fontSize;
        setUpView();
    }

    private void setUpView() {
        setAlignment(Pos.CENTER_LEFT);
        var text = new Text(String.format("- %dx ", displayCurrency.getAmount()));
        text.setStyle("-fx-font-weight: bold; -fx-font-size: " + String.format("%dpx;", this.fontSize));
        var iconView = new ImageView();
        iconView.setFitHeight(iconSize);
        iconView.setFitWidth(iconSize);
        iconView.setImage(icon);
        getChildren().addAll(text, iconView);
    }
}
