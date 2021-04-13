package poedatatracker.util;

import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Counter<T> {
    private Map<T, Integer> result;

    public Counter(Collection<? extends T> collection) {
        result = new HashMap<>();
        collection.forEach(e -> result.merge(e, 1, Integer::sum));
    }
}
