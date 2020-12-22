import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReceivedCurrency {
    private String currencyName;
    private int amount;

    @Override
    public String toString() {
        return String.format("%dx %s", amount, currencyName);
    }
}
