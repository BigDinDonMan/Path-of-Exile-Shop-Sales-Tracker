package poedatatracker.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.hibernate.Session;
import org.hibernate.Transaction;
import poedatatracker.core.ApplicationDatabase;
import poedatatracker.core.models.ShopSale;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonToDb {
    public static void main(String[] args) throws Exception {
        ApplicationDatabase.initialize();
        Logger logger = Logger.getLogger(JsonToDb.class.getName());
        Gson g = new Gson();
        String path = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "sales.json";
        String salesJson = new String(Files.readAllBytes(Paths.get(path)));
        List<ShopSale> sales = g.fromJson(salesJson, new TypeToken<List<ShopSale>>(){}.getType());
        Session dbSession = ApplicationDatabase.getNewSession();
        AtomicBoolean errorPresent = new AtomicBoolean(false);
        Transaction t = null;
        try {
            t = dbSession.beginTransaction();
            final var transaction = t;
            sales.forEach(sale -> {
                var item = sale.getItem();
                item.setSale(sale);
                var currencies = sale.getCurrencies();
                dbSession.save(sale);
                dbSession.save(item);
                currencies.forEach(c -> {
                    c.setSale(sale);
                    dbSession.save(c);
                });
                logger.log(Level.INFO, "Inserted shop sale to database: \n" + sale.toString());
            });
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (t != null)
                t.rollback();
            errorPresent.set(true);
        }
        System.out.println(errorPresent.get() ? "Something went wrong :(" : "Saved successfully to db");
        dbSession.close();
        ApplicationDatabase.shutdown();
    }
}
