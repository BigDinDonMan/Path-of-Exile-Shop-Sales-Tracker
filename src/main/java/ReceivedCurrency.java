import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReceivedCurrency {
    private String currencyName;
    private int amount;
}
