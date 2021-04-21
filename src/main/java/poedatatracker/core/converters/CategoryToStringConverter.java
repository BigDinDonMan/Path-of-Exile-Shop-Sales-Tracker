package poedatatracker.core.converters;

import poedatatracker.core.models.ItemCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CategoryToStringConverter implements AttributeConverter<ItemCategory, String> {
    @Override
    public String convertToDatabaseColumn(ItemCategory itemCategory) {
        return itemCategory == null ? null : itemCategory.name();
    }

    @Override
    public ItemCategory convertToEntityAttribute(String s) {
        return s == null ? null : ItemCategory.valueOf(s);
    }
}
