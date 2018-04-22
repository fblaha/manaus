package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
final public class BspMarketCategorizer extends AbstractDelegatingCategorizer {

    public BspMarketCategorizer() {
        super("bsp_");
    }

    @Override
    public Set<String> getCategoryRaw(Market market) {
        return Set.of(Boolean.toString(market.isBspMarket()));
    }
}
