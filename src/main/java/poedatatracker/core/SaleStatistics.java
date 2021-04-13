package poedatatracker.core;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import poedatatracker.core.models.ItemCategory;
import poedatatracker.core.models.ReceivedCurrency;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SaleStatistics {

    private Map<ItemCategory, Long> itemsSoldByCategory;
    private List<ReceivedCurrency> totalIncomes;
    private long totalItemsSold;

    public static SaleStatistics calculate() {
        try (var s = ApplicationDatabase.getNewSession()) {
            var categoryCounts = getItemsSoldByCategory(s);
            var totalIncomes = getTotalIncomes(s);
            var totalItemsSold = categoryCounts.values().stream().mapToLong(l -> l).sum();
            return new SaleStatistics(categoryCounts, totalIncomes, totalItemsSold);
        }
    }



    @SuppressWarnings("unchecked")
    private static Map<ItemCategory, Long> getItemsSoldByCategory(Session s) {
        Query<HashMap<String, Object>> categoryCountsQuery = s.createQuery(
                "select new map(si.category as Category, sum(si.amount) as Amount) from SoldItem as si group by si.category");
        var categoryCountsResult = categoryCountsQuery.getResultList();
        return categoryCountsResult.
                stream().
                collect(Collectors.toMap(e -> (ItemCategory)e.get("Category"), e -> (long)e.get("Amount")));
    }

    @SuppressWarnings("unchecked")
    private static List<ReceivedCurrency> getTotalIncomes(Session s) {
        return s.createQuery(
                "select new ReceivedCurrency(rc.currencyName, cast(sum(rc.amount) as integer)) " +
                        "from ReceivedCurrency as rc group by rc.currencyName").
                getResultList();
    }
}
