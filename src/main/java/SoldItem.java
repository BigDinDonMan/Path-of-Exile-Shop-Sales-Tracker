import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sold_items")
public class SoldItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private long id;

    @Column(name = "item_name")
    private String name;

    @Column(name = "item_amount")
    private int amount;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "item_category")
    private ItemCategory category;

    @OneToOne
    @JoinColumn(name = "sale_id")
    @Setter
    private ShopSale sale;

    public SoldItem(String name, int amount, ItemCategory category) {
        this.name = name;
        this.amount = amount;
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("Item: %s\nCategory: %s\nAmount: %d", name, category.prettifyName(), amount);
    }
}
