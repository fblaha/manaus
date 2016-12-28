package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
public class TagCategorizer implements SettledBetCategorizer {

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        return settledBet.getBetAction().getTags().stream().map(tag -> "tag_" + tag).collect(toSet());
    }
}
