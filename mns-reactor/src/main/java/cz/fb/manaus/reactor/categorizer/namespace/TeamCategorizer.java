package cz.fb.manaus.reactor.categorizer.namespace;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Runner;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TeamCategorizer implements SettledBetCategorizer {

    public static final String NAMESPACE = "team";

    @Override
    public Optional<String> getNamespace() {
        return Optional.of(NAMESPACE);
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        Market market = settledBet.getBetAction().getMarketPrices().getMarket();
        String eventName = market.getEvent().getName();
        return market.getRunners().stream().map(Runner::getName)
                .filter(name -> eventName.contains(name))
                .map(String::toLowerCase)
                .map(CharMatcher.WHITESPACE.or(CharMatcher.JAVA_LETTER_OR_DIGIT)::retainFrom)
                .map(this::getCategory)
                .collect(Collectors.toSet());
    }

    private String getCategory(String name) {
        name = CharMatcher.WHITESPACE.replaceFrom(name, '_');
        name = name.substring(0, Math.min(name.length(), 30));
        return Joiner.on('_').join(NAMESPACE, name);
    }
}
