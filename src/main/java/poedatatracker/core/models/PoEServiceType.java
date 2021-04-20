package poedatatracker.core.models;

import org.apache.maven.shared.utils.StringUtils;

import java.util.Arrays;

public enum PoEServiceType implements DecoratedEnum {
    HARVEST_CRAFT, BENCH_CRAFT, BOSS_KILL, LAB_CARRY;

    @Override
    public String prettifyName() {
        String[] parts = name().toLowerCase().split("_");
        return StringUtils.capitalise(String.join(" ", parts));
    }
}
