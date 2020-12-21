import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SoldItem {
    private String name;
    private int amount;
    private ItemCategory category;
}
