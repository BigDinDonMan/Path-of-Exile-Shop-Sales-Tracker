package poedatatracker.core.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "currency_exchanges")
@NoArgsConstructor
@Getter
public class CurrencyExchange {
    @Id
    @GeneratedValue(generator = "SQLITE-CURRENCY-EXCHANGES")
    @TableGenerator(name = "SQLITE-CURRENCY-EXCHANGES", pkColumnName = "name", pkColumnValue = "currency_exchanges",
            table = "sqlite_sequence", valueColumnName = "seq")
    @Column(name = "exchange_id")
    private long id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "currencyName", column = @Column(name = "paid_currency_name")),
        @AttributeOverride(name = "amount", column = @Column(name = "paid_currency_amount"))
    })
    private ExchangedCurrency paidCurrency;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "currencyName", column = @Column(name = "received_currency_name")),
        @AttributeOverride(name = "amount", column = @Column(name = "received_currency_amount"))
    })
    private ExchangedCurrency receivedCurrency;

    @Column(name = "exchange_date")
    private LocalDate exchangeDate;

    public CurrencyExchange(ExchangedCurrency paid, ExchangedCurrency recvd, LocalDate d) {
        this.exchangeDate = d;
        this.paidCurrency = paid;
        this.receivedCurrency = recvd;
    }
}
