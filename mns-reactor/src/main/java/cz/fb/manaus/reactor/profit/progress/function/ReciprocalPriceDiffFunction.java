package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.price.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.OptionalDouble;

@Component
public class ReciprocalPriceDiffFunction implements ProgressFunction {

    @Autowired
    private PriceService priceService;

    @Override
    public OptionalDouble function(SettledBet bet) {
        MarketPrices marketPrices = bet.getBetAction().getMarketPrices();
        OptionalDouble reciprocalBack = marketPrices.getReciprocal(Side.BACK);
        OptionalDouble reciprocalLay = marketPrices.getReciprocal(Side.LAY);
        if (reciprocalBack.isPresent() && reciprocalLay.isPresent()) {
            RunnerPrices runnerPrices = marketPrices.getRunnerPrices(bet.getSelectionId());
            Optional<Price> layBest = runnerPrices.getHomogeneous(Side.LAY).getBestPrice();
            Optional<Price> backBest = runnerPrices.getHomogeneous(Side.BACK).getBestPrice();
            double layPrice = layBest.get().getPrice();
            double backPrice = backBest.get().getPrice();
            double reciprocalLayFairPrice = priceService.getReciprocalFairPrice(layPrice, reciprocalLay.getAsDouble());
            double reciprocalBackFairPrice = priceService.getReciprocalFairPrice(backPrice, reciprocalBack.getAsDouble());
            return OptionalDouble.of(Math.abs(reciprocalBackFairPrice - reciprocalLayFairPrice));
        } else {
            return OptionalDouble.empty();
        }
    }

}
