package poedatatracker.core.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "poe_services_payments")
@NoArgsConstructor
@Getter
public class PoEServicePayment implements Serializable, Currency {

    @Id
    @GeneratedValue(generator = "SQLITE-POE-SERVICE-PAYMENT")
    @TableGenerator(name = "SQLITE-POE-SERVICE-PAYMENT", pkColumnName = "name", pkColumnValue = "poe_services_payments",
            table = "sqlite_sequence", valueColumnName = "seq")
    @Column(name = "payment_id")
    private long id;

    @Column(name = "currency_name")
    private String currencyName;

    @Column(name = "currency_amount")
    private int amount;

    @Setter
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private PoEService service;

    public PoEServicePayment(String currencyName, int amount) {
        this.currencyName = currencyName;
        this.amount = amount;
    }
}
