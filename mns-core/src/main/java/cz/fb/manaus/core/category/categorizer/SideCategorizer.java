package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SideCategorizer implements SettledBetCategorizer {

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        var side = settledBet.getPrice().getSide().name().toLowerCase();
        return Set.of("side_" + side);
    }

}
