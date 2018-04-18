package cz.fb.manaus.core.maintanance;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigUpdate {
    public static final ConfigUpdate NO_OP = new ConfigUpdate(Collections.emptySet(), Collections.emptyMap(), "0m");
    private final Set<String> deletePrefixes;
    private final Map<String, String> setProperties;
    private final String ttl;

    private ConfigUpdate(Set<String> deletePrefixes, Map<String, String> setProperties, String ttl) {
        this.deletePrefixes = deletePrefixes;
        this.setProperties = setProperties;
        this.ttl = ttl;
    }

    public static ConfigUpdate empty(Duration ttl) {
        return new ConfigUpdate(new HashSet<>(), new HashMap<>(), ttl.toMinutes() + "m");
    }

    public Set<String> getDeletePrefixes() {
        return deletePrefixes;
    }

    public Map<String, String> getSetProperties() {
        return setProperties;
    }

    public String getTtl() {
        return ttl;
    }
}
