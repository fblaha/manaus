package cz.fb.manaus.core.category;

import cz.fb.manaus.core.category.categorizer.Categorizer;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.category.categorizer.SimulationAware;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableSet.copyOf;

@Service
final public class CategoryService {
    @Autowired(required = false)
    private List<Categorizer> categorizers = new LinkedList<>();
    @Autowired(required = false)
    private List<SettledBetCategorizer> settledBetCategorizers = new LinkedList<>();

    public Set<String> getMarketCategories(Market market, boolean simulationAwareOnly) {
        Set<String> result = new HashSet<>();
        for (Categorizer categorizer : filterCategorizers(categorizers, simulationAwareOnly)) {
            Set<String> categories = categorizer.getCategories(market);
            if (categories != null) result.addAll(categories);
        }
        return copyOf(result);
    }

    public Set<String> getSettledBetCategories(SettledBet settledBet, boolean simulationAwareOnly, BetCoverage coverage) {
        Set<String> result = new HashSet<>();
        for (SettledBetCategorizer categorizer : filterCategorizers(settledBetCategorizers, simulationAwareOnly)) {
            MarketPrices prices = settledBet.getBetAction().getMarketPrices();
            if (prices == null && categorizer.isMarketSnapshotRequired()) continue;
            Set<String> categories = categorizer.getCategories(settledBet, coverage);
            if (categories != null) result.addAll(categories);
        }
        return copyOf(result);
    }

    public List<SettledBet> filterBets(List<SettledBet> settledBets, String projection, BetCoverage coverage) {
        return settledBets.parallelStream().filter(input -> {
            Set<String> categories = getSettledBetCategories(input, false, coverage);
            return categories.stream().anyMatch(category -> category.contains(projection));
        }).collect(Collectors.toList());
    }

    private <T extends SimulationAware> List<T> filterCategorizers(
            List<T> categorizers, boolean simulationAwareOnly) {
        if (simulationAwareOnly) {
            return categorizers.stream().filter(SimulationAware::isSimulationSupported)
                    .collect(Collectors.toList());
        }
        return categorizers;
    }

}
