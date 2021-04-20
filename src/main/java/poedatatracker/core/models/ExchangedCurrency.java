package poedatatracker.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangedCurrency implements Currency, Serializable {
    private String currencyName;
    private int amount;

    @Override
    public String toString() {
        return String.format("%dx %s", amount, currencyName);
    }
}
