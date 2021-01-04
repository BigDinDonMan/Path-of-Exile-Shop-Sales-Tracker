import javafx.scene.paint.Color;
import org.apache.maven.shared.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

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
    LEGION_EMBLEM,
    OTHER;

    private static Map<ItemCategory, Color> categoryColors;
    private static final Color DEFAULT_COLOR = Color.BLACK;

    static {
        categoryColors = new HashMap<>();
        categoryColors.put(EQUIPMENT, Color.rgb(255, 170, 0));
        categoryColors.put(JEWELLERY, Color.rgb(255, 170, 0));
        categoryColors.put(UNIQUE, Color.rgb(125, 47, 17));
        categoryColors.put(SKILL_GEM, Color.rgb(25, 110, 75));
        categoryColors.put(FOSSIL, Color.rgb(252, 96, 2));
        categoryColors.put(BEAST,  Color.rgb(255, 170, 0));
        categoryColors.put(PROPHECY, Color.rgb(97, 30, 144));
        categoryColors.put(MAP, Color.rgb(57, 57, 56));
        categoryColors.put(BREACHSTONE, Color.rgb(57, 57, 56));
        categoryColors.put(JEWEL,  Color.rgb(255, 170, 0));
        categoryColors.put(ABYSSAL_JEWEL, Color.rgb(82, 163, 76));
        categoryColors.put(CLUSTER_JEWEL, Color.rgb(97, 30, 144));
        categoryColors.put(FLASK, Color.rgb(57, 57, 56));
        categoryColors.put(SCARAB, Color.rgb(97, 30, 144));
        categoryColors.put(MAP_FRAGMENT, Color.rgb(97, 30, 144));
        categoryColors.put(LEGION_EMBLEM, Color.rgb(57, 57, 56));
        categoryColors.put(OTHER, DEFAULT_COLOR);
    }

    public static Color getCategoryColor(ItemCategory ic) {
        return categoryColors.get(ic);
    }

    public String prettifyName() {
        String[] parts = name().toLowerCase().split("_");
        return StringUtils.capitalise(String.join(" ", parts));
    }
}
