package poedatatracker.core;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import poedatatracker.core.models.*;

import javax.persistence.criteria.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationDatabase {

    @Getter
    private static Configuration dbConfig;

    private static SessionFactory sessionFactory;

    private static boolean production = false;


    private ApplicationDatabase() {}

    public static void initialize() {
        dbConfig = new Configuration();
        dbConfig.configure(production ? "hibernate.cfg.xml" : "hibernate-dev.cfg.xml");
        var annotatedClasses = Arrays.asList(
                ShopSale.class,
                ReceivedCurrency.class,
                SoldItem.class,
                PoEService.class,
                PoEServicePayment.class,
                LevelledSkillGem.class
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

    public static List<LocalDate> fetchSaleDates(boolean distinct) {
        Session session = getNewSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<LocalDate> query = cb.createQuery(LocalDate.class);
        Root<ShopSale> root = query.from(ShopSale.class);
        query.select(root.get("saleDate")).distinct(distinct).orderBy(cb.asc(root.get("saleDate")));

        List<LocalDate> dates = session.createQuery(query).getResultList();

        transaction.commit();
        session.close();
        return dates;
    }

    public static List<ShopSale> fetchAllSales() {
        Session session = getNewSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ShopSale> query = cb.createQuery(ShopSale.class);
        Root<ShopSale> root = query.from(ShopSale.class);
        query.select(root).orderBy(cb.asc(root.get("saleDate")));
        List<ShopSale> sales = session.createQuery(query).getResultList();

        transaction.commit();
        session.close();
        return sales;
    }

    public static List<ShopSale> fetchSalesMatching(LocalDate predicateDate, ItemCategory predicateCategory) {
        Session session = getNewSession();
        Transaction transaction = session.beginTransaction();

        List<ShopSale> results = null;
        CriteriaBuilder builder = session.getCriteriaBuilder();
        if (predicateCategory == null && predicateDate == null) {
            results = session.createQuery("from ShopSale as s order by s.saleDate", ShopSale.class).getResultList();
        } else {
            CriteriaQuery<ShopSale> query = builder.createQuery(ShopSale.class);
            Root<ShopSale> root = query.from(ShopSale.class);
            List<Predicate> predicates = new ArrayList<>();
            if (predicateCategory == null && predicateDate != null) {
                predicates.add(builder.equal(root.get("saleDate"), predicateDate));
            } else if (predicateCategory != null && predicateDate == null) {
                predicates.add(builder.equal(root.get("item").get("category"), predicateCategory));
            } else {
                predicates.addAll(
                        Arrays.asList(
                                builder.equal(root.get("item").get("category"), predicateCategory),
                                builder.equal(root.get("saleDate"), predicateDate)
                        )
                );
            }
            query.select(root).where(predicates.toArray(Predicate[]::new)).orderBy(builder.asc(root.get("saleDate")));
            results = session.createQuery(query).getResultList();
        }

        transaction.commit();
        session.close();
        return results;
    }

    @SuppressWarnings("unchecked")
    public static List<String> fetchGemData() {
        try (var s = getNewSession()) {
            Transaction t = s.beginTransaction();
            String hqlQuery = "select g.gemName || ', ' || lower(g.gemType) || ', max level: ' || g.maxLevel from LevelledSkillGem g";
            Query<String> query = s.createQuery(hqlQuery);
            List<String> result = query.getResultList();
            t.commit();
            return result;
        }
    }

    public static boolean exportSalesToTxt(String filePath) {
        Session s = sessionFactory.openSession();
        var sales = s.createQuery("from ShopSale", ShopSale.class).getResultList();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            var dates = sales.stream().map(ShopSale::getSaleDate).distinct().collect(Collectors.toList());
            for (var date : dates) {
                var salesWithDateEqual = sales.stream().filter(_s -> _s.getSaleDate().equals(date)).collect(Collectors.toUnmodifiableList());
                writer.write(date.format(formatter));
                writer.write('\n');
                for (var sale : salesWithDateEqual) {
                    var item = sale.getItem();
                    var currencies = sale.getCurrencies();
                    var saleString = String.format(
                            "- %dx %s, %s, %s",
                            item.getAmount(),
                            item.getName(),
                            item.getCategory().name(),
                            currencies.stream().map(ReceivedCurrency::toString).collect(Collectors.joining(", ")));
                    writer.write(saleString);
                    writer.write('\n');
                }
                writer.write('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        s.close();
        return true;
    }
}
