package poedatatracker.core.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class CurrencyExchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "")
    private long id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "currencyName", column = @Column(name = "")),
        @AttributeOverride(name = "amount", column = @Column(name = ""))
    })
    @Getter
    private ExchangedCurrency paidCurrency;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "currencyName", column = @Column(name = "")),
        @AttributeOverride(name = "amount", column = @Column(name = ""))
    })
    @Getter
    private ExchangedCurrency receivedCurrency;
}
