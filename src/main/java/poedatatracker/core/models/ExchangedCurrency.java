package poedatatracker.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@Getter
@AllArgsConstructor
public class ExchangedCurrency implements Currency, Serializable {
    private String currencyName;
    private int amount;

    @Override
    public String toString() {
        return String.format("%dx %s", amount, currencyName);
    }
}
