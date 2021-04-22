package poedatatracker.core.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "poe_services")
@NoArgsConstructor
@Getter
public class PoEService {

    @Id
    @GeneratedValue(generator = "SQLITE-POE-SERVICE")
    @TableGenerator(name = "SQLITE-POE-SERVICE", pkColumnName = "name", pkColumnValue = "poe_services",
            valueColumnName = "seq", table = "sqlite_sequence")
    @Column(name = "service_id")
    private long id;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "count_performed")
    private int countPerformed;

    @Column(name = "service_date")
    private LocalDate serviceDate;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "service_type")
    private PoEServiceType serviceType;

    @OneToMany(mappedBy = "service", fetch = FetchType.EAGER)
    private List<PoEServicePayment> payments;

    public PoEService(String name, int count, PoEServiceType type, LocalDate date) {
        this.serviceName = name;
        this.countPerformed = count;
        this.serviceType = type;
        this.serviceDate = date;
        this.payments = Collections.emptyList();
    }

    public PoEService(String name, int count, PoEServiceType type, LocalDate date, Collection<? extends PoEServicePayment> payments) {
        this(name, count, type, date);
        this.payments = new ArrayList<>(payments);
    }
}
