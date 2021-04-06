package poedatatracker.core.models;

import org.apache.maven.shared.utils.StringUtils;

public enum GemQualityType implements DecoratedEnum {
    SUPERIOR, ANOMALOUS, DIVERGENT, PHANTASMAL;

    @Override
    public String prettifyName() {
        return StringUtils.capitalise(name().toLowerCase());
    }
}
