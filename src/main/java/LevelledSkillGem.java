import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "levelled_gems")
@NoArgsConstructor
public class LevelledSkillGem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gem_id")
    private long id;

    @Column(name = "gem_name")
    private String gemName;

    @Column(name = "max_level")
    private int maxLevel;

    @Column
    private int quality;

    @Column(name = "quality_type")
    @Enumerated(value = EnumType.STRING)
    private GemQualityType qualityType;

    @Column
    private boolean corrupted;

    @Override
    public boolean equals(Object o) {
        if (o instanceof LevelledSkillGem) {
            LevelledSkillGem g = (LevelledSkillGem)o;
            return this.corrupted == g.corrupted &&
                    this.gemName.equals(g.gemName) &&
                    this.maxLevel == g.maxLevel &&
                    this.quality == g.quality &&
                    this.qualityType.equals(g.qualityType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.gemName,
                this.maxLevel,
                this.quality,
                this.qualityType,
                this.corrupted
        );
    }

    @Override
    public String toString() {
        return "";
    }
}
