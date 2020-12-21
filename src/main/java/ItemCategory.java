import org.apache.maven.shared.utils.StringUtils;

public enum ItemCategory {
    EQUIPMENT,
    JEWELLERY,
    UNIQUE,
    SKILL_GEM,
    FOSSIL,
    BEAST,
    PROPHECY,
    MAP,
    BREACHSTONE,
    JEWEL,
    ABYSSAL_JEWEL,
    CLUSTER_JEWEL,
    FLASK,
    SCARAB,
    MAP_FRAGMENT,
    LEGION_EMBLEM;

    public String prettifyName() {
        String[] parts = name().toLowerCase().split("_");
        return StringUtils.capitalise(String.join(" ", parts));
    }
}
