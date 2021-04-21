package poedatatracker.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import poedatatracker.core.converters.CategoryToStringConverter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SoldItem implements Serializable {

    private String name;

    private int amount;

    @Convert(converter = CategoryToStringConverter.class)
    private ItemCategory category;

    @Override
    public String toString() {
        return String.format("Item: %s\nCategory: %s\nAmount: %d", name, category.prettifyName(), amount);
    }
}
