package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

@Component
public class FairDiffBackFunction extends AbstractFairDiffFunction {

    protected FairDiffBackFunction() {
        super(Side.BACK);
    }

}
