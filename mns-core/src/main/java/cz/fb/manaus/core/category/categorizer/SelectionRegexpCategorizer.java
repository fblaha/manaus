package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toSet;

@Component
public class SelectionRegexpCategorizer extends AbstractRegexpResolver implements SettledBetCategorizer {

    public static final Map<String, Pattern> SELECTION_MAP = Map.of(
            "draw", compile("^The\\s+Draw$"),
            "yes", compile("^Yes$"),
            "no", compile("^No$"));

    public SelectionRegexpCategorizer() {
        super("selectionRegexp_");
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        String selectionName = settledBet.getSelectionName();
        return getCategories(selectionName);
    }

    Set<String> getCategories(String selectionName) {
        Set<String> selectionBased = getCategories(selectionName, SELECTION_MAP);
        return selectionBased.stream().map(this::addPrefix).collect(toSet());
    }

}
