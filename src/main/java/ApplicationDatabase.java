import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.Arrays;

public class ApplicationDatabase {

    @Getter
    private static Configuration dbConfig;

    private static SessionFactory sessionFactory;

    private ApplicationDatabase() {}

    public static void initialize() {
        dbConfig = new Configuration();
        dbConfig.configure("hibernate.cfg.xml");
        var annotatedClasses = Arrays.asList(
                ShopSale.class,
                ReceivedCurrency.class,
                SoldItem.class
        );
        annotatedClasses.forEach(dbConfig::addAnnotatedClass);
        var registry = new StandardServiceRegistryBuilder().applySettings(dbConfig.getProperties()).build();
        sessionFactory = dbConfig.buildSessionFactory(registry);
    }

    public static Session getNewSession() {
        return sessionFactory.openSession();
    }

    public static void shutdown() {
        if (sessionFactory.isOpen()) {
            sessionFactory.close();
        }
    }
}
