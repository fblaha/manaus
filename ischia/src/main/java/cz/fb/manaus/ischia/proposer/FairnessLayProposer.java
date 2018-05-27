package cz.fb.manaus.ischia.proposer;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.ischia.BackLoserBet;
import cz.fb.manaus.ischia.LayLoserBet;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.proposer.common.AbstractFairnessProposer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@BackLoserBet
@LayLoserBet
public class FairnessLayProposer extends AbstractFairnessProposer {

    @Autowired
    public FairnessLayProposer(Function<BetContext, Double> downgradeStrategy) {
        super(Side.BACK, downgradeStrategy);
    }
}
