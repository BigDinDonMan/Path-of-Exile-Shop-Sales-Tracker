package poedatatracker.gui.display;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import poedatatracker.core.GlobalData;
import poedatatracker.core.models.PoEService;
import poedatatracker.core.models.PoEServiceType;

import java.time.format.DateTimeFormatter;

public class ServiceListCell extends ListCell<PoEService> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Insets nodeInsets = new Insets(0, 0, 5, 0);

    @Override
    protected void updateItem(PoEService item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(isEmpty() ? null : createDisplayControl(item));
        setText(null);
    }

    private Node createDisplayControl(PoEService item) {
        var root = new VBox();
        var dateLabel = new Text(item.getServiceDate().format(formatter));
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        VBox.setMargin(dateLabel, nodeInsets);

        var servicePerformedLabel = new Text("Service: " + item.getServiceName());

        var timesPerformed = item.getCountPerformed();
        var timesPerformedText = new Text(String.format("Times performed: %d time%s", timesPerformed, timesPerformed == 1 ? "" : "s"));

        var serviceTypeInfoParent = new HBox();
        var serviceLabel = new Text("Service type: ");
        var serviceTypeLabel = new Text(item.getServiceType().prettifyName());
        serviceTypeLabel.setStyle("-fx-font-weight: bold;");
        serviceTypeInfoParent.getChildren().addAll(serviceLabel, serviceTypeLabel);

        var paymentsText = new Text("Payments: ");
        var paymentsRoot = new VBox();
        VBox.setMargin(paymentsRoot, nodeInsets);
        VBox.setMargin(paymentsText, nodeInsets);

        item.getPayments().forEach(c -> {
            var displayCell = new CurrencyDisplayCell(c, GlobalData.getCurrencyIcons().get(c.getCurrencyName()), 24d, 12);
            paymentsRoot.getChildren().add(displayCell);
        });

        root.getChildren().addAll(
                dateLabel,
                servicePerformedLabel,
                timesPerformedText,
                serviceTypeInfoParent,
                paymentsText,
                paymentsRoot
        );
        return root;
    }
}
