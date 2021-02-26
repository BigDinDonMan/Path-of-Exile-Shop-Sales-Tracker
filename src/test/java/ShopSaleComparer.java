import java.util.ArrayList;
import java.util.Comparator;

public class ShopSaleComparer {

    public boolean salesEqual(ShopSale s1, ShopSale s2) {
        var item1 = s1.getItem();
        var item2 = s2.getItem();
        var itemsEqual = item1.getCategory().equals(item2.getCategory()) &&
                item1.getName().equals(item2.getName()) &&
                item1.getAmount() == item2.getAmount();
        if (!itemsEqual) {
            return false;
        }
        var sortedCurrencies1 = new ArrayList<>(s1.getCurrencies());
        sortedCurrencies1.sort(Comparator.comparing(ReceivedCurrency::getCurrencyName));
        var sortedCurrencies2 = new ArrayList<>(s2.getCurrencies());
        sortedCurrencies2.sort(Comparator.comparing(ReceivedCurrency::getCurrencyName));
        var sizesEqual = sortedCurrencies1.size() == sortedCurrencies2.size();
        if (!sizesEqual) {
            return false;
        }
        for (int i = 0; i < sortedCurrencies1.size(); ++i) {
            var c1 = sortedCurrencies1.get(i);
            var c2 = sortedCurrencies2.get(i);
            var equal = c1.getCurrencyName().equals(c2.getCurrencyName()) && c1.getAmount() == c2.getAmount();
            if (!equal) {
                return false;
            }
        }
        return s1.getSaleDate().equals(s2.getSaleDate());
    }
}
