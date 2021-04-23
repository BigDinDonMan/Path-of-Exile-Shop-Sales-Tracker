package poedatatracker.util;

import javafx.concurrent.Task;
import org.hibernate.Session;
import org.hibernate.Transaction;
import poedatatracker.core.ApplicationDatabase;
import poedatatracker.core.models.LevelledSkillGem;
import poedatatracker.core.models.PoEService;
import poedatatracker.core.models.ShopSale;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SaveDataToDatabaseTask extends Task<Boolean> {

    private List<ShopSale> sales;
    private List<PoEService> services;
    private List<LevelledSkillGem> gems;

    public SaveDataToDatabaseTask(Collection<ShopSale> sales,Collection<PoEService> services, Collection<LevelledSkillGem> gems) {
        super();
        this.sales = new ArrayList<>(sales);
        this.services = new ArrayList<>(services);
        this.gems = new ArrayList<>(gems);
    }

    @Override
    protected Boolean call() throws Exception {
        try (Session session = ApplicationDatabase.getNewSession()) {
            saveSales(session);
            saveServices(session);
            saveGems(session);
        }
        return true;
    }

    private void saveServices(Session session) {
        if (services.isEmpty()) return;
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            var currentWork = new AtomicInteger(0);
            final int totalWork = services.size();
            services.forEach(service -> {
                var payments = service.getPayments();
                session.save(service);
                payments.forEach(p -> {
                    p.setService(service);
                    session.save(p);
                });
                var current = currentWork.getAndIncrement();
                updateProgress(current, totalWork);
                updateMessage(String.format("Services saving progress: %d/%d", current, totalWork));
            });
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    private void saveSales(Session session) {
        if (sales.isEmpty()) return;
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            var currentWork = new AtomicInteger(0);
            final int totalWork = sales.size();
            sales.forEach(sale -> {
                var currencies = sale.getCurrencies();
                session.save(sale);
                currencies.forEach(c -> {
                    c.setSale(sale);
                    session.save(c);
                });
                var current = currentWork.incrementAndGet();
                updateProgress(current, totalWork);
                updateMessage(String.format("Sales saving progress: %d/%d", current, totalWork));
            });
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    private void saveGems(Session session) {
        if (gems.isEmpty()) return;
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            var currentWork = new AtomicInteger(0);
            final int totalWork = gems.size();
            gems.forEach(g -> {
                session.save(g);
                var current = currentWork.incrementAndGet();
                updateProgress(current, totalWork);
                updateMessage(String.format("Gems saving progress: %d/%d", current, totalWork));
            });
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
