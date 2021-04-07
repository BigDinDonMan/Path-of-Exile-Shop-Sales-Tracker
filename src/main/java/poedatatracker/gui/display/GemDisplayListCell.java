package poedatatracker.gui.display;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import poedatatracker.core.models.LevelledSkillGem;

import java.time.format.DateTimeFormatter;

public class GemDisplayListCell extends ListCell<LevelledSkillGem> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Insets nodeInsets = new Insets(0, 0, 5, 0);

    @Override
    protected void updateItem(LevelledSkillGem item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(isEmpty() ? null : createDisplayControl(item));
    }

    private Node createDisplayControl(LevelledSkillGem item) {
        var root = new VBox();
        var dateText = new Text(item.getLevellingDate().format(formatter));
        dateText.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        VBox.setMargin(dateText, nodeInsets);

        StringBuilder sb = new StringBuilder();
        sb.append(item.getGemName()).append(" level ").append(item.getMaxLevel());
        int quality = item.getQuality();
        if (quality > 0) {
            sb.append(' ').append(quality).append("% quality");
        }
        if (item.isCorrupted()) {
            sb.append(" (corrupted)");
        }
        sb.append("\n");
        sb.append("Gem type: ").append(item.getGemType().prettifyName()).append('\n');
        sb.append("Quality type: ").append(item.getQualityType().prettifyName()).append('\n');
        var gemDescriptionText = new Text(sb.toString());

        root.getChildren().addAll(dateText, gemDescriptionText);
        return root;
    }
}
