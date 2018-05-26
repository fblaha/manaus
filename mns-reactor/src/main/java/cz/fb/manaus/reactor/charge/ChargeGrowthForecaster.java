package cz.fb.manaus.reactor.charge;

import com.google.common.collect.LinkedListMultimap;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.reactor.betting.AmountAdviser;
import cz.fb.manaus.reactor.price.Fairness;
import cz.fb.manaus.reactor.price.ProbabilityCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.OptionalDouble;

@Component
public class ChargeGrowthForecaster {

    @Autowired
    private MarketChargeSimulator simulator;
    @Autowired
    private ProbabilityCalculator probabilityCalculator;
    @Autowired
    private AmountAdviser amountAdviser;
    @Autowired
    private ExchangeProvider exchangeProvider;


    private LinkedListMultimap<Long, Price> convertBetData(List<Bet> currentBets) {
        var bets = LinkedListMultimap.<Long, Price>create();
        for (var bet : currentBets) {
            var price = bet.getRequestedPrice().getPrice();
            var matchedAmount = bet.getMatchedAmount();
            var side = bet.getRequestedPrice().getSide();
            bets.put(bet.getSelectionId(), new Price(price, matchedAmount, side));
        }
        return bets;
    }

    public OptionalDouble getForecast(long selectionId, Side betSide,
                                      MarketSnapshot snapshot, Fairness fairness) {
        if (exchangeProvider.isPerMarketCharge()) {
            var fairnessSide = fairness.getMoreCredibleSide();
            if (fairnessSide.isPresent()) {
                var sideFairness = fairness.get(fairnessSide.get());
                var probabilities = probabilityCalculator.fromFairness(
                        sideFairness.getAsDouble(), fairnessSide.get(), snapshot.getMarketPrices());
                var marketPrices = snapshot.getMarketPrices();
                var bets = convertBetData(snapshot.getCurrentBets());
                var runnerPrices = marketPrices.getRunnerPrices(selectionId);
                var winnerCount = marketPrices.getWinnerCount();
                var oldCharge = simulator.getChargeMean(winnerCount, exchangeProvider.getChargeRate(), probabilities, bets);
                var bestPrice = runnerPrices.getHomogeneous(betSide.getOpposite()).getBestPrice();
                if (bestPrice.isPresent()) {
                    var price = bestPrice.get().getPrice();
                    var amount = amountAdviser.getAmount();
                    bets.put(selectionId, new Price(price, amount, betSide));
                    var newCharge = simulator.getChargeMean(winnerCount, exchangeProvider.getChargeRate(), probabilities, bets);
                    var result = newCharge / oldCharge;
                    return OptionalDouble.of(result);
                }
            }
        }
        return OptionalDouble.empty();
    }

}
