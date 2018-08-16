package cz.fb.manaus.core.model.factory;

import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.RunnerPrices;

import java.util.Collection;
import java.util.Date;

public class MarketPricesFactory {
    public static MarketPrices create(int winnerCount, Market market, Collection<RunnerPrices> runnerPrices, Date time) {
        var mp = new MarketPrices();
        mp.setWinnerCount(winnerCount);
        mp.setMarket(market);
        mp.setRunnerPrices(runnerPrices);
        mp.setTime(time);
        return mp;
    }
}
