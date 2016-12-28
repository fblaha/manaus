package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

import static java.util.Collections.singleton;

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
            return Collections.singleton("none");
        }
        return singleton(countryCode.toLowerCase());
    }
}
