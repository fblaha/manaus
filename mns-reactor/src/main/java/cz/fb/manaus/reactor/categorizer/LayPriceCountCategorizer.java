package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

@Component
public class LayPriceCountCategorizer extends AbstractPriceCountCategorizer {

    public LayPriceCountCategorizer() {
        super("priceCountLay_", 3, Side.LAY);
    }
}
