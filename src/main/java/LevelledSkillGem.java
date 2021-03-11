import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.maven.shared.utils.StringUtils;

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

    @Column(name = "gem_type")
    @Enumerated(value = EnumType.STRING)
    private GemType gemType;

    @Column
    private boolean corrupted;

    @Override
    public boolean equals(Object o) {
        if (o instanceof LevelledSkillGem) {
            var g = (LevelledSkillGem)o;
            return this.corrupted == g.corrupted &&
                    this.gemName.equals(g.gemName) &&
                    this.maxLevel == g.maxLevel &&
                    this.quality == g.quality &&
                    this.qualityType.equals(g.qualityType) &&
                    this.gemType.equals(g.gemType);
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
                this.corrupted,
                this.gemType
        );
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if (this.quality > 0 && !this.qualityType.equals(GemQualityType.SUPERIOR)) {
            sb.append(this.qualityType.prettyName()).append(' ');
        }
        sb.append(this.gemName).append('\n');
        sb.append(StringUtils.capitalise(this.gemType.name().toLowerCase())).append('\n');
        sb.append("Level: ").append(this.maxLevel).append('\n');
        sb.append("Quality: ").append(this.quality).append('%').append('\n');
        if (this.corrupted) {
            sb.append("Corrupted").append('\n');
        }
        return sb.toString();
    }
}
