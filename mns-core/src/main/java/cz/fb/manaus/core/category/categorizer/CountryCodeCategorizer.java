package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
final public class CountryCodeCategorizer extends AbstractDelegatingCategorizer {

    public static final String PREFIX = "country_";

    public CountryCodeCategorizer() {
        super(PREFIX);
    }

    @Override
    protected Set<String> getCategoryRaw(Market market) {
        String countryCode = market.getEvent().getCountryCode();
        if (countryCode == null) {
            return Set.of("none");
        }
        return Set.of(countryCode.toLowerCase());
    }
}
