import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "harvest_craft_payments")
@NoArgsConstructor
@Getter
public class HarvestCraftPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private long id;

    @Column(name = "currency_name")
    private String currencyName;

    @Column(name = "currency_amount")
    private int amount;

    @Setter
    @ManyToOne
    @JoinColumn(name = "craft_id", nullable = false)
    private HarvestCraft craft;

    public HarvestCraftPayment(String currencyName, int amount) {
        this.currencyName = currencyName;
        this.amount = amount;
    }
}
