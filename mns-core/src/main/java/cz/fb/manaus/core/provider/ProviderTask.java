package cz.fb.manaus.core.provider;

import java.time.Duration;

public interface ProviderTask {

    default String getLogPrefix() {
        return String.format("task '%s' : ", getName());
    }

    String getName();

    Duration getPauseDuration();

    void execute();

}
