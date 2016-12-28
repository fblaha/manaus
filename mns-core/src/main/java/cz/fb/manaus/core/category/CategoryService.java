package cz.fb.manaus.core.category;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import cz.fb.manaus.core.category.categorizer.Categorizer;
import cz.fb.manaus.core.category.categorizer.NamespaceAware;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.copyOf;

@Service
final public class CategoryService {
    @Autowired(required = false)
    private List<Categorizer> categorizers = new LinkedList<>();
    @Autowired(required = false)
    private List<SettledBetCategorizer> settledBetCategorizers = new LinkedList<>();

    public Set<String> getMarketCategories(Market market, boolean simulationAwareOnly, Optional<String> namespace) {
        Set<String> result = new HashSet<>();
        for (Categorizer categorizer : filterCategorizers(categorizers, simulationAwareOnly, namespace)) {
            Set<String> categories = categorizer.getCategories(market);
            if (categories != null) result.addAll(categories);
        }
        return copyOf(result);
    }

    public Set<String> getSettledBetCategories(SettledBet settledBet, boolean simulationAwareOnly, Optional<String> namespace, BetCoverage coverage) {
        Set<String> result = new HashSet<>();
        for (SettledBetCategorizer categorizer : filterCategorizers(settledBetCategorizers, simulationAwareOnly, namespace)) {
            MarketPrices prices = settledBet.getBetAction().getMarketPrices();
            if (prices == null && categorizer.isMarketSnapshotRequired()) continue;
            Set<String> categories = categorizer.getCategories(settledBet, coverage);
            if (categories != null) result.addAll(categories);
        }
        return copyOf(result);
    }

    public List<SettledBet> filterBets(List<SettledBet> settledBets, String projection,
                                       Optional<String> namespace, BetCoverage coverage) {
        return settledBets.parallelStream().filter(input -> {
            Set<String> categories = getSettledBetCategories(input, false, namespace, coverage);
            return Iterables.any(categories, category -> category.contains(projection));
        }).collect(Collectors.toList());
    }

    private <T extends SimulationAware & NamespaceAware> Iterable<T> filterCategorizers(List<T> categorizers,
                                                                                        boolean simulationAwareOnly,
                                                                                        Optional<String> namespace) {
        checkNotNull(namespace);
        FluentIterable<T> filtered = FluentIterable.from(categorizers)
                .filter(c -> namespace.equals(c.getNamespace()) || c.isGlobal());
        if (simulationAwareOnly) {
            return filtered.filter(SimulationAware::isSimulationSupported);
        }
        return filtered;
    }

}
