package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

@Component
public class LowestBackPriceFunction extends AbstractPriceReduceFunction {

    public LowestBackPriceFunction() {
        super(Side.BACK, Math::min);
    }

}
