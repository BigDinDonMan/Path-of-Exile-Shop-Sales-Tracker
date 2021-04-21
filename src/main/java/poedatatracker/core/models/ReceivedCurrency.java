package poedatatracker.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "received_currencies")
public class ReceivedCurrency implements Serializable, Currency {
    @Id
    @Column(name = "currency_id")
    @GeneratedValue(generator = "SQLITE-RECEIVED-CURRENCIES")
    @TableGenerator(name = "SQLITE-RECEIVED-CURRENCIES",pkColumnName = "name",
            pkColumnValue = "received_currencies", table = "sqlite_sequence",
            valueColumnName = "seq")
    private long id;

    @Column(name = "currency_name")
    private String currencyName;

    @Column(name = "currency_amount")
    private int amount;

    @ManyToOne
    @JoinColumn(name = "sale_id", nullable = false)
    @Setter
    private ShopSale sale;

    public ReceivedCurrency(String name, int quantity) {
        this.amount = quantity;
        this.currencyName = name;
    }

    @Override
    public String toString() {
        return String.format("%dx %s", amount, currencyName);
    }
}
