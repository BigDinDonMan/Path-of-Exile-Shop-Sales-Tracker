import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import poedatatracker.core.ApplicationDatabase;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

//todo: add splash screen
public class TrackerApp extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        AnchorPane p = loader.load();
        MainWindowController controller = loader.getController();
        Scene s = new Scene(p);
        s.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setMinHeight(350);
        stage.setMinWidth(450);
        stage.setScene(s);
        stage.setTitle("Path of Exile Data Tracker");
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResource("images/app-icon.png").toExternalForm()));
//        setUpSystemTrayIfSupported(stage);
        stage.setOnCloseRequest(e -> {
            controller.shutdown();
            ApplicationDatabase.shutdown();
        });
        stage.sizeToScene();
        stage.show();
    }

    private void setUpSystemTrayIfSupported(Stage s) {
        if (!SystemTray.isSupported()) return;

        Platform.setImplicitExit(false);
        SystemTray tray = SystemTray.getSystemTray();
        java.awt.Image icon = null;

        try {
            icon = ImageIO.read(getClass().getResource("images/tray-icon.png").toURI().toURL());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            Platform.setImplicitExit(true);
            return;
        }

        s.setOnCloseRequest(e -> hideStage(s));
        ActionListener closeListener = e -> {
            s.close();
            Platform.exit();
            System.exit(0);
        };
        ActionListener showListener = e -> showStage(s);

        PopupMenu menu = new PopupMenu();
        MenuItem closeItem = new MenuItem("Close application");
        closeItem.addActionListener(closeListener);
        MenuItem showItem = new MenuItem("Show application");
        showItem.addActionListener(showListener);
        menu.add(showItem);
        menu.add(closeItem);

        TrayIcon tIcon = new TrayIcon(icon, s.getTitle(), menu);
        tIcon.addActionListener(showListener);

        try {
            tray.add(tIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void hideStage(Stage s) {
        Platform.runLater(s::hide);
    }

    private void showStage(Stage s) {
        Platform.runLater(s::show);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
