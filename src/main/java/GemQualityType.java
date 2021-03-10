import org.apache.maven.shared.utils.StringUtils;

public enum GemQualityType {
    SUPERIOR, ANOMALOUS, DIVERGENT, PHANTASMAL;

    public String prettyName() {
        return StringUtils.capitalise(name());
    }
}
