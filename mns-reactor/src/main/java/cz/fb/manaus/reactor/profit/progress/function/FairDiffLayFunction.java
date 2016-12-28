package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

@Component
public class FairDiffLayFunction extends AbstractFairDiffFunction {

    protected FairDiffLayFunction() {
        super(Side.LAY);
    }
}
