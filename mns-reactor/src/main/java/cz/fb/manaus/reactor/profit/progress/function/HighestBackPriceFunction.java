package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

@Component
public class HighestBackPriceFunction extends AbstractPriceReduceFunction {

    public HighestBackPriceFunction() {
        super(Side.BACK, Math::max);
    }

}
