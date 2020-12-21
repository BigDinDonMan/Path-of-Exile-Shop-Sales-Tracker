import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

public class TrackerApp extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent p = loader.load();
        Scene s = new Scene(p);
        s.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setMinHeight(350);
        stage.setMinWidth(450);
        stage.setScene(s);
        stage.setTitle("Path of Exile Shop Sales Tracker");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
