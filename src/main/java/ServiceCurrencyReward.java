import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "service_rewards")
@NoArgsConstructor
public class ServiceCurrencyReward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id")
    private long id;
    private String currencyName;
    private int amount;

    @Setter
    private PoEService service;

    public ServiceCurrencyReward(String name, int amount) {
        this.currencyName = name;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.format("%dx %s", amount, currencyName);
    }
}
