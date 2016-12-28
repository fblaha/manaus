package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Runner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
final public class RunnerCountCategorizer extends AbstractDelegatingCategorizer {

    public static final String PREFIX = "runnerCount_";

    public RunnerCountCategorizer() {
        super(PREFIX);
    }

    @Override
    public Set<String> getCategoryRaw(Market market) {
        long size = market.getRunners().stream().mapToLong(Runner::getSelectionId).distinct().count();
        return Collections.singleton(Long.toString(size));
    }
}
