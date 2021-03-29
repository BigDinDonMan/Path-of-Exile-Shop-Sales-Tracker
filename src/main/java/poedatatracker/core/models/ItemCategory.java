package poedatatracker.core.models;

import javafx.scene.paint.Color;
import org.apache.maven.shared.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum ItemCategory {
    EQUIPMENT,
    JEWELLERY,
    UNIQUE,
    SKILL_GEM,
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
    DIVINATION_CARD,
    INCUBATOR,
    CRAFTING_SUPPLY,
    WATCHSTONE,
    CRAFTING_BASE,
    OTHER;

    private static Map<ItemCategory, Color> categoryColors;
    private static final Color DEFAULT_COLOR = Color.BLACK;

    static {
        categoryColors = new HashMap<>();
        var goldenColor = Color.rgb(255, 170, 0);
        var generalColor = Color.rgb(57, 57, 56);
        var purpleColor = Color.rgb(97, 30, 144);
        categoryColors.put(EQUIPMENT, goldenColor);
        categoryColors.put(JEWELLERY, goldenColor);
        categoryColors.put(UNIQUE, Color.rgb(125, 47, 17));
        categoryColors.put(SKILL_GEM, Color.rgb(25, 110, 75));
        categoryColors.put(CRAFTING_SUPPLY, Color.rgb(252, 96, 2));
        categoryColors.put(BEAST, goldenColor);
        categoryColors.put(PROPHECY, purpleColor);
        categoryColors.put(MAP, generalColor);
        categoryColors.put(BREACHSTONE, generalColor);
        categoryColors.put(JEWEL, goldenColor);
        categoryColors.put(ABYSSAL_JEWEL, Color.rgb(82, 163, 76));
        categoryColors.put(CLUSTER_JEWEL, purpleColor);
        categoryColors.put(FLASK, generalColor);
        categoryColors.put(SCARAB, purpleColor);
        categoryColors.put(MAP_FRAGMENT, purpleColor);
        categoryColors.put(LEGION_EMBLEM, generalColor);
        categoryColors.put(DIVINATION_CARD, Color.rgb(0,17,73));
//        categoryColors.put(ESSENCE, Color.rgb(16,195,249));
        categoryColors.put(OTHER, DEFAULT_COLOR);
    }

    public static Color getCategoryColor(ItemCategory ic) {
        return categoryColors.getOrDefault(ic, DEFAULT_COLOR);
    }

    public String prettifyName() {
        String[] parts = name().toLowerCase().split("_");
        return StringUtils.capitalise(String.join(" ", parts));
    }
}
