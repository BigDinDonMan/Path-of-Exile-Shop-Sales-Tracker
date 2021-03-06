package poedatatracker.core;

import com.google.gson.Gson;
import javafx.scene.image.Image;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GlobalData {

    private GlobalData() {}

    //mutable data
//    @Getter
//    private static ObservableList<ShopSale> sales;

    //resources
    @Getter
    private static List<String> currencies;
    @Getter
    private static Map<String, Image> currencyIcons;

    private static boolean initialized = false;


    public static void initialize() {
        if (!initialized) {
            try {
                loadResources();
                initialized = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadResources() throws IOException {
        currencies = loadResourceFile(
                System.getProperty("user.dir") + File.separator + "resources" + File.separator + "currencies.json",
                String[].class
        );
        currencyIcons = new HashMap<>();

        currencies.forEach(name -> {
            String filename = String.join("-", name.toLowerCase().split(" "));
            Image i = new Image(GlobalData.class.getResource(String.format("/images/%s.png", filename)).toExternalForm());
            currencyIcons.put(name, i);
        });

//        sales = FXCollections.observableArrayList(loadResourceFile(
//                System.getProperty("user.dir") + File.separator + "resources" + File.separator + "sales.json",
//                ShopSale[].class
//        ));
    }

    private static <T> List<T> loadResourceFile(String path, Class<T[]> klass) throws IOException {
        Gson g = new Gson();
        byte[] fileBytes = Files.readAllBytes(Paths.get(path));
        String json = new String(fileBytes);
        return new ArrayList<>(Arrays.asList(g.fromJson(json, klass)));
    }
//
//    public static void saveSalesData() throws IOException {
//        GsonBuilder builder = new GsonBuilder();
//        Gson g = builder.setPrettyPrinting().create();
//        String salesJson = g.toJson(sales);
//        Files.writeString(
//                Paths.get(System.getProperty("user.dir") + File.separator + "resources" + File.separator + "sales.json"),
//                salesJson
//        );
//    }
}
