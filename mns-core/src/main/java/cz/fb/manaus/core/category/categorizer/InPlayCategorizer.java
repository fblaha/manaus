package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
final public class InPlayCategorizer extends AbstractDelegatingCategorizer {

    public InPlayCategorizer() {
        super("inPlay_");
    }

    @Override
    public Set<String> getCategoryRaw(Market market) {
        return Set.of(Boolean.toString(market.isInPlay()));
    }
}
