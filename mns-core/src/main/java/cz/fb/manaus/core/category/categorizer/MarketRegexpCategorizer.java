package cz.fb.manaus.core.category.categorizer;

import com.google.common.collect.ImmutableMap;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.Category;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.collect.Collections2.transform;

@Component
public class MarketRegexpCategorizer extends AbstractRegexpResolver implements SettledBetCategorizer, Categorizer {
    public static final String PREFIX = "regexp_";

    public static final ImmutableMap<String, Pattern> MATCH_MAP = ImmutableMap.of(
            "overUnderGoals", compile("^Over/Under\\s+\\d+\\.5\\s+goals$"),
            "regularTimeMatchOdd", compile("^Regular\\s+Time\\s+Match\\s+Odds$"));

    public static final ImmutableMap<String, Pattern> EVENT_MAP = ImmutableMap.<String, Pattern>builder()
            .put("underAge", compile("^.*\\s+U[12]\\d\\s+.*\\s+U[12]\\d(?:\\s+.*)?$"))
            .put("underAge_{1}", compile("^.*\\s+U([12]\\d)\\s+.*\\s+U[12]\\d(?:\\s+.*)?$"))
            .put("women", compile("^.*\\s+\\(w\\)\\s+.*\\s+\\(w\\)(?:\\s+.*)?$"))
            .put("reserveTeam", compile("^.*\\s+\\(Res\\)\\s+.*\\s+\\(Res\\)(?:\\s+.*)?$"))
            .build();

    public MarketRegexpCategorizer() {
        super(Category.MARKET_PREFIX + PREFIX);
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        String marketName = settledBet.getBetAction().getMarket().getName();
        String eventName = settledBet.getBetAction().getMarket().getEvent().getName();
        return getCategories(marketName, eventName);
    }

    @Override
    public Set<String> getCategories(Market market) {
        return getCategories(market.getName(), market.getEvent().getName());
    }

    Set<String> getCategories(String marketName, String eventName) {
        Set<String> result = new HashSet<>();
        result.addAll(transform(getCategories(marketName, MATCH_MAP), this::addPrefix));
        result.addAll(transform(getCategories(eventName, EVENT_MAP), this::addPrefix));
        return result;
    }

}
