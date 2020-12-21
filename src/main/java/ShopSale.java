import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public class ShopSale {
    private SoldItem item;
    private LocalDate saleDate;
    private List<ReceivedCurrency> currencies;

    public ShopSale(SoldItem item, LocalDate date, ReceivedCurrency... currencies) {
        this.item = item;
        this.saleDate = date;
        this.currencies = new ArrayList<>(Arrays.asList(currencies));
    }
}
