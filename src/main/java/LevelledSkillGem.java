import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "")
@NoArgsConstructor
public class LevelledSkillGem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "")
    private long id;

    @Column(name = "")
    private String gemName;

    @Column(name = "")
    private int maxLevel;

    @Column(name = "")
    private int quality;

    @Column(name = "")
    @Enumerated(value = EnumType.STRING)
    private GemQualityType qualityType;

    @Column(name = "")
    private boolean corrupted;
}
