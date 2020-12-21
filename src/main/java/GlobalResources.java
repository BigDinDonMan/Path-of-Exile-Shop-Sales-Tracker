import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.image.Image;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalResources {

    private GlobalResources() {}

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
        currencies = loadResourceFile(System.getProperty("user.dir") + File.separator + "resources" + File.separator + "currencies.json");
        currencyIcons = new HashMap<>();

        currencies.forEach(name -> {
            String filename = String.join("-", name.toLowerCase().split(" "));
            Image i = new Image(GlobalResources.class.getResource(String.format("images/%s.png", filename)).toExternalForm());
            currencyIcons.put(name, i);
        });
    }

    private static <T> T loadResourceFile(String path) throws IOException {
        Gson g = new Gson();
        Type t = new TypeToken<T>(){}.getType();
        String json = new String(Files.readAllBytes(Paths.get(path)));
        return g.fromJson(json, t);
    }
}
