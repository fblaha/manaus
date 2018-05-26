package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
final public class CountryCodeCategorizer extends AbstractDelegatingCategorizer {

    public static final String PREFIX = "country_";

    public CountryCodeCategorizer() {
        super(PREFIX);
    }

    @Override
    protected Set<String> getCategoryRaw(Market market) {
        var countryCode = market.getEvent().getCountryCode();
        return Set.of(Optional.ofNullable(countryCode)
                .map(String::toLowerCase)
                .orElse("none"));
    }
}
