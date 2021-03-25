import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SaveDataToDatabaseTask extends Task<Boolean> {

    private List<ShopSale> sales;
    private List<LevelledSkillGem> gems;
    private List<PoEService> services;

    public SaveDataToDatabaseTask(Collection<? extends ShopSale> sales, Collection<? extends PoEService> services, Collection<? extends LevelledSkillGem> gems) {
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

    private void saveSales(Session s) {
        if (sales.isEmpty()) return;
        Transaction transaction = null;
        try {
            transaction = s.getTransaction();
            var currentWork = new AtomicInteger(0);
            final int totalWork = sales.size();
            sales.forEach(sale -> {
                var item = sale.getItem();
                item.setSale(sale);
                var currencies = sale.getCurrencies();
                s.save(sale);
                s.save(item);
                currencies.forEach(c -> {
                    c.setSale(sale);
                    s.save(c);
                });
                final int current = currentWork.incrementAndGet();
                updateProgress(current, totalWork);
                updateMessage(String.format("Sales saving progress: %d/%d", current, currentWork.get()));
            });
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    private void saveServices(Session s) {
        if (services.isEmpty()) return;
        Transaction transaction = null;
        try {
            transaction = s.getTransaction();
            var currentWork = new AtomicInteger(0);
            final int totalWork = services.size();
            services.forEach(service -> {
                var payments = service.getPayments();
                s.save(service);
                payments.forEach(p -> {
                    p.setService(service);
                    s.save(p);
                });
                final int current = currentWork.incrementAndGet();
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

    private void saveGems(Session s) {
        if (gems.isEmpty()) return;
        Transaction transaction = null;
        try {
            transaction = s.getTransaction();
            var currentWork = new AtomicInteger(0);
            final int totalWork = gems.size();
            gems.forEach(g -> {
                s.save(g);
                final int current = currentWork.incrementAndGet();
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
