package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

@Component
public class LowestLayPriceFunction extends AbstractPriceReduceFunction {

    public LowestLayPriceFunction() {
        super(Side.LAY, Math::min);
    }

}
