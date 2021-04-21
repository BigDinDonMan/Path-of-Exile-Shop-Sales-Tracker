import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import poedatatracker.core.models.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

public class SQLiteConnectionTests {

    private static Configuration cfg;
    private static SessionFactory sessionFactory;
    private Session currentSession;

    @BeforeAll
    public static void setup() {
        cfg = new Configuration();
        cfg.configure("hibernate-sqlite-test.cfg.xml");
        var annotatedClasses = Arrays.asList(
                ShopSale.class,
                ReceivedCurrency.class,
                SoldItem.class,
                PoEService.class,
                PoEServicePayment.class,
                LevelledSkillGem.class,
                CurrencyExchange.class,
                ExchangedCurrency.class
        );
        annotatedClasses.forEach(cfg::addAnnotatedClass);
        var registry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
        sessionFactory = cfg.buildSessionFactory(registry);
    }

    @BeforeEach
    public void setupConnection() {
        currentSession = sessionFactory.openSession();
    }

    @AfterEach
    public void disposeOfConnection() {
        if (currentSession != null) {
            currentSession.close();
        }
    }

    @AfterAll
    public static void destroy() {
        sessionFactory.close();
    }


    @Test
    public void insert_shopSales_into_db_successful() {
        var t = currentSession.beginTransaction();
        var testSale = new ShopSale(
                new SoldItem("Chayula's Pure Breachstone", 1, ItemCategory.BREACHSTONE),
                LocalDate.of(2021, Month.APRIL, 15),
                new ReceivedCurrency("Chaos Orb", 40),
                new ReceivedCurrency("Exalted Orb", 3)
        );
        var currencies = testSale.getCurrencies();
        currentSession.save(testSale);
        currencies.forEach(currentSession::save);
        t.commit();
    }


    //NOTES: sale date is of type INTEGER in the database, its stored as number of seconds since EPOCH
    @Test
    public void select_singleSale() {
        var t = currentSession.beginTransaction();
        var cb = currentSession.getCriteriaBuilder();
        var query = cb.createQuery(ShopSale.class);
        var root = query.from(ShopSale.class);
        query.select(root).orderBy(cb.asc(root.get("saleDate")));
        var selected = currentSession.createQuery(query).getResultList();
        t.commit();
    }
}
