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

    public SaveDataToDatabaseTask(Collection<? extends ShopSale> c) {
        super();
        this.sales = new ArrayList<>(c);
    }

    @Override
    protected Boolean call() throws Exception {
        Session session = ApplicationDatabase.getNewSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            AtomicInteger currentWork = new AtomicInteger(0);
            final int totalWork = sales.size();
            sales.forEach(sale -> {
                var item = sale.getItem();
                item.setSale(sale);
                var currencies = sale.getCurrencies();
                session.save(sale);
                session.save(item);
                currencies.forEach(c -> {
                    c.setSale(sale);
                    session.save(c);
                });
                final int current = currentWork.incrementAndGet();
                updateProgress(current, totalWork);
                updateMessage(String.format("Saving progress: %d/%d", current, currentWork.get()));
            });
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
        return true;
    }

    private void saveSales(Session s) {
        Transaction transaction = null;
        try {

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    private void saveServices(Session s) {
        Transaction transaction = null;
        try {

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    private void saveGems(Session s) {
        Transaction transaction = null;
        try {

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
