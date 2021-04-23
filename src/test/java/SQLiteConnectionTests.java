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
    public void select_allSales() {
        var t = currentSession.beginTransaction();
        var cb = currentSession.getCriteriaBuilder();
        var query = cb.createQuery(ShopSale.class);
        var root = query.from(ShopSale.class);
        query.select(root).orderBy(cb.asc(root.get("saleDate")));
        var selected = currentSession.createQuery(query).getResultList();
        t.commit();
    }

    @Test
    public void insert_PoEServices_successful() {
        var t = currentSession.beginTransaction();
        var payments = Arrays.asList(
                new PoEServicePayment("Exalted Orb", 3),
                new PoEServicePayment("Chaos Orb", 30),
                new PoEServicePayment("Divine Orb", 10)
        );
        var testService = new PoEService(
                "Remove/Add Critical",
                2,
                PoEServiceType.HARVEST_CRAFT,
                LocalDate.of(2021, Month.MARCH, 20),
                payments
        );
        currentSession.save(testService);
        payments.forEach(currentSession::save);
        t.commit();
    }

    @Test
    public void insert_levelledGems_successful() {
        var t = currentSession.beginTransaction();
        var testGem = new LevelledSkillGem(
                "Pulverise Support",
                20,
                20,
                GemQualityType.SUPERIOR,
                GemType.SUPPORT,
                LocalDate.of(2021, 3, 22),
                false
        );
        currentSession.save(testGem);
        t.commit();
    }

    @Test
    public void insert_currencyExchanges_successful() {
        var t = currentSession.beginTransaction();

        var testExchange = new CurrencyExchange(
                new ExchangedCurrency("Chaos Orb", 400),
                new ExchangedCurrency("Exalted Orb" , 4),
                LocalDate.of(2021, 2, 26)
        );
        currentSession.save(testExchange);

        t.commit();
    }
}
