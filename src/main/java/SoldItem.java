import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SoldItem {
    private String name;
    private int amount;
    private ItemCategory category;


    @Override
    public String toString() {
        return String.format("Item: %s\nCategory: %s\nAmount: %d", name, category.prettifyName(), amount);
    }
}
