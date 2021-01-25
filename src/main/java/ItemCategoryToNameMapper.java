import java.util.function.BiFunction;
import java.util.function.Function;

public class ItemCategoryToNameMapper implements BiFunction<String, ItemCategory, String> {

    @Override
    public String apply(String s, ItemCategory category) {
        String name = s;
        switch (category) {
            case SKILL_GEM: {
                int index = s.indexOf("level");
                if (index != -1) {
                    name = s.substring(0, index).strip();
                }
                break;
            }

            case UNIQUE:
            case PROPHECY:
            case CRAFTING_SUPPLIES:
            case SCARAB:
            case BREACHSTONE:
            case LEGION_EMBLEM:
            case MAP_FRAGMENT:
            case MAP:
            case OTHER:
                break;

            default:
                name = "";
                break;
        }
        return name;
    }
}
