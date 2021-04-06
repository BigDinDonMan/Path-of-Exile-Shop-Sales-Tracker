package poedatatracker.core.models;

import org.apache.maven.shared.utils.StringUtils;

public enum GemType implements DecoratedEnum {
    ATTACK, SUPPORT, AURA, SPELL, WARCRY;

    @Override
    public String prettifyName() {
        return StringUtils.capitalise(name().toLowerCase());
    }
}
