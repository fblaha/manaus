package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

@Component
public class BackPriceCountCategorizer extends AbstractPriceCountCategorizer {

    public BackPriceCountCategorizer() {
        super("priceCountBack_", 3, Side.BACK);
    }
}
