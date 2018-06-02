package cz.fb.manaus.spring;

import java.util.Set;

public interface ManausProfiles {
    String PRODUCTION = "production";
    String DB = "database";
    String TEST = "test";

    Set<String> PRODUCTION_REQUIRED = Set.of(PRODUCTION, DB);
}
