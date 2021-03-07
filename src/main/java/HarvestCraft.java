import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "harvest_crafts")
@NoArgsConstructor
public class HarvestCraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "craft_id")
    private long id;

    @Column(name = "craft_name")
    private String craftName;

    @Column(name = "craft_count")
    private int craftCount;

    @OneToMany(mappedBy = "craft", fetch = FetchType.EAGER)
    private List<HarvestCraftPayment> payments;
}
