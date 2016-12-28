package cz.fb.manaus.reactor.charge;

import com.google.common.collect.LinkedListMultimap;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.reactor.betting.AmountAdviser;
import cz.fb.manaus.reactor.price.Fairness;
import cz.fb.manaus.reactor.price.ProbabilityCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        LinkedListMultimap<Long, Price> bets = LinkedListMultimap.create();
        for (Bet bet : currentBets) {
            double price = bet.getRequestedPrice().getPrice();
            double matchedAmount = bet.getMatchedAmount();
            Side side = bet.getRequestedPrice().getSide();
            bets.put(bet.getSelectionId(), new Price(price, matchedAmount, side));
        }
        return bets;
    }

    public OptionalDouble getForecast(long selectionId, Side betSide,
                                      MarketSnapshot snapshot, Fairness fairness) {
        if (exchangeProvider.isPerMarketCharge()) {
            Optional<Side> fairnessSide = fairness.getMoreCredibleSide();
            if (fairnessSide.isPresent()) {
                OptionalDouble sideFairness = fairness.get(fairnessSide.get());
                Map<Long, Double> probabilities = probabilityCalculator.fromFairness(
                        sideFairness.getAsDouble(), fairnessSide.get(), snapshot.getMarketPrices());
                MarketPrices marketPrices = snapshot.getMarketPrices();
                LinkedListMultimap<Long, Price> bets = convertBetData(snapshot.getCurrentBets());
                RunnerPrices runnerPrices = marketPrices.getRunnerPrices(selectionId);
                int winnerCount = marketPrices.getWinnerCount();
                double oldCharge = simulator.getChargeMean(winnerCount, exchangeProvider.getChargeRate(), probabilities, bets);
                Optional<Price> bestPrice = runnerPrices.getHomogeneous(betSide.getOpposite()).getBestPrice();
                if (bestPrice.isPresent()) {
                    double price = bestPrice.get().getPrice();
                    double amount = amountAdviser.getAmount();
                    bets.put(selectionId, new Price(price, amount, betSide));
                    double newCharge = simulator.getChargeMean(winnerCount, exchangeProvider.getChargeRate(), probabilities, bets);
                    double result = newCharge / oldCharge;
                    return OptionalDouble.of(result);
                }
            }
        }
        return OptionalDouble.empty();
    }

}
