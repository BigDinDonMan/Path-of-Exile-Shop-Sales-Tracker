import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shop_sales")
public class ShopSale implements Serializable, Comparable<ShopSale> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private long id;

    @Column(name = "sale_date")
    private LocalDate saleDate;

    @OneToMany(mappedBy = "sale", fetch = FetchType.EAGER)
    private List<ReceivedCurrency> currencies;

    @OneToOne(mappedBy = "sale", fetch = FetchType.EAGER)
    private SoldItem item;

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

    @Override
    public int compareTo(ShopSale o) {
        return this.saleDate.compareTo(o.saleDate);
    }
}
