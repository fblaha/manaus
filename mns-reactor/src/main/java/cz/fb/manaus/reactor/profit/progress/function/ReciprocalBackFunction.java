package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

@Component
public class ReciprocalBackFunction implements ProgressFunction {

    @Override
    public OptionalDouble apply(SettledBet bet) {
        return bet.getBetAction().getMarketPrices().getReciprocal(Side.BACK);
    }

}
