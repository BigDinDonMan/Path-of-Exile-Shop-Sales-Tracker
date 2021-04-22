package poedatatracker.core.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.maven.shared.utils.StringUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "levelled_gems")
@NoArgsConstructor
@Getter
public class LevelledSkillGem {
    @Id
    @GeneratedValue(generator = "SQLITE-LEVELLED-GEMS")
    @TableGenerator(name = "SQLITE-LEVELLED-GEMS", pkColumnValue = "levelled_gems", pkColumnName = "name",
            valueColumnName = "seq", table = "sqlite_sequence")
    @Column(name = "gem_id")
    private long id;

    @Column(name = "gem_name")
    private String gemName;

    @Column(name = "max_level")
    private int maxLevel;

    @Column(name = "quality")
    private int quality;

    @Column(name = "quality_type")
    @Enumerated(value = EnumType.STRING)
    private GemQualityType qualityType;

    @Column(name = "gem_type")
    @Enumerated(value = EnumType.STRING)
    private GemType gemType;

    @Column(name = "levelling_date")
    private LocalDate levellingDate;

    @Column
    private boolean corrupted;

    public LevelledSkillGem(String name, int maxLevel, int quality, GemQualityType qualityType, GemType type, LocalDate date, boolean isCorrupted) {
        this.gemName = name;
        this.maxLevel = maxLevel;
        this.quality = quality;
        this.qualityType = qualityType;
        this.gemType = type;
        this.levellingDate = date;
        this.corrupted = isCorrupted;
    }

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
            sb.append(this.qualityType.prettifyName()).append(' ');
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
