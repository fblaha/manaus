package cz.fb.manaus.reactor.betting;

import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.charge.ChargeGrowthForecaster;
import cz.fb.manaus.reactor.price.Fairness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

@Component
public class BetContextFactory {

    @Autowired
    private ChargeGrowthForecaster forecaster;

    public BetContext create(Side side, long selectionId,
                             MarketSnapshot snapshot, Fairness fairness) {
        OptionalDouble forecast = forecaster.getForecast(selectionId, side, snapshot, fairness);
        return new BetContext(side, selectionId, forecast, snapshot, fairness);
    }

}
