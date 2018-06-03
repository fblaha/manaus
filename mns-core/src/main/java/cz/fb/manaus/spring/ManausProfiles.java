package cz.fb.manaus.spring;

public interface ManausProfiles {
    String PRODUCTION = "production";
    String DB = "database";
    String TEST = "test";

    String PRODUCTION_REQUIRED[] = {PRODUCTION, DB};
}
