import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "poe_services")
@NoArgsConstructor
@Getter
public class PoEService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private long id;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "count_performed")
    private int countPerformed;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "service_type")
    private PoEServiceType serviceType;

    @OneToMany(mappedBy = "service", fetch = FetchType.EAGER)
    private List<PoeServicePayment> payments;
}
