import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShopSale {
    private SoldItem item;
    private LocalDate saleDate;
    private List<ReceivedCurrency> currencies;

    public ShopSale(SoldItem item, LocalDate date, ReceivedCurrency... currencies) {
        this.item = item;
        this.saleDate = date;
        this.currencies = new ArrayList<>(Arrays.asList(currencies));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(saleDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append('\n');
        sb.append(item.toString()).append('\n');
        sb.append("Received currencies:\n");
        currencies.forEach(c -> sb.append("\t- ").append(c.toString()).append('\n'));
        return sb.toString();
    }
}
