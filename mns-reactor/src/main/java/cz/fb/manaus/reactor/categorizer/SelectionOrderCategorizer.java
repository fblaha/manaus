package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Runner;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Collections.singleton;

@Component
public class SelectionOrderCategorizer implements SettledBetCategorizer {

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        Market market = settledBet.getBetAction().getMarket();
        Runner runner = market.getRunners().stream()
                .filter(r -> r.getSelectionId() == settledBet.getSelectionId())
                .findFirst()
                .get();
        return singleton("selectionOrder_" + runner.getSortPriority());
    }
}
