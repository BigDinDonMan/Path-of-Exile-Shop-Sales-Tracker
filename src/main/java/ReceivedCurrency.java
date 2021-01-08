import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "received_currencies")
public class ReceivedCurrency implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "currency_id")
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
