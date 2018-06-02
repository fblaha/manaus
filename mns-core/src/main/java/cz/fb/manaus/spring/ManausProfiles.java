package cz.fb.manaus.spring;

import java.util.Set;

public interface ManausProfiles {
    String PRODUCTION_PROFILE = "production";
    String DB_PROFILE = "database";
    String TEST_PROFILE = "test";

    Set<String> PRODUCTION_REQUIRED = Set.of(PRODUCTION_PROFILE, DB_PROFILE);
}
