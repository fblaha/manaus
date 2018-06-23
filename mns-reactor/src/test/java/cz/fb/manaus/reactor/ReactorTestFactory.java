package cz.fb.manaus.reactor;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Event;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketPricesTest;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.BetContextFactory;
import cz.fb.manaus.reactor.price.Fairness;
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator;
import cz.fb.manaus.reactor.price.PriceService;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.time.DateUtils.addHours;


@Component
public class ReactorTestFactory {

    public static final String BET_ID = "111156454";

    @Autowired
    private RoundingService roundingService;
    @Autowired
    private FairnessPolynomialCalculator calculator;
    @Autowired
    private PriceService priceService;
    @Autowired
    private BetContextFactory contextFactory;
    @Autowired
    private ExchangeProvider provider;


    public static Bet newBet(Price oldOne) {
        return new Bet(BET_ID, "1", CoreTestFactory.HOME, oldOne, new Date(), 5);
    }

    public BetContext newUpdateBetContext(MarketPrices marketPrices, RunnerPrices runnerPrices, Side side) {
        var oldBet = newBet(new Price(5d, 5d, side));
        return newBetContext(side, marketPrices, runnerPrices, of(oldBet)).withNewPrice(oldBet.getRequestedPrice());
    }

    public BetContext newBetContext(Side side, MarketPrices marketPrices, RunnerPrices runnerPrices, Optional<Bet> oldBet) {
        var fairness = new Fairness(OptionalDouble.of(0.9d), OptionalDouble.of(1.1));

        var bets = new LinkedList<Bet>();
        oldBet.ifPresent(bet -> bets.add(bet));
        var snapshot = MarketSnapshot.from(marketPrices, bets, empty());

        return contextFactory.create(side, CoreTestFactory.HOME, snapshot, fairness,
                empty(), Set.of());
    }

    public RunnerPrices newRP(long selectionId, double bestBack, double bestLay) {
        return newRP(selectionId, bestBack, bestLay, OptionalDouble.empty());

    }

    public BetContext createContext(Side side, double bestBack, double bestLay) {
        var marketPrices = createMarket(bestBack, bestLay, OptionalDouble.of(3d), 1);
        var runnerPrices = marketPrices.getRunnerPrices().iterator().next();
        var selectionId = runnerPrices.getSelectionId();
        var bestPrice = runnerPrices.getHomogeneous(side.getOpposite()).getBestPrice();
        var bets = new LinkedList<Bet>();
        if (bestPrice.isPresent()) {
            var marketId = CoreTestFactory.MARKET_ID;
            double price = bestPrice.get().getPrice();
            var requestedPrice = new Price(price, provider.getMinAmount(), side.getOpposite());
            var date = Instant.now().minus(2, ChronoUnit.HOURS);
            var counterBet = new Bet(BET_ID, marketId, selectionId, requestedPrice,
                    Date.from(date), provider.getMinAmount());
            bets.add(counterBet);
        }
        var snapshot = MarketSnapshot.from(marketPrices, bets, empty());
        return contextFactory.create(side, selectionId, snapshot,
                calculator.getFairness(marketPrices), empty(), Set.of());

    }

    public List<RunnerPrices> createRP(List<Double> unfairPrices) {
        var runnerPrices = new LinkedList<RunnerPrices>();
        for (var i = 0; i < unfairPrices.size(); i++) {
            var unfairPrice = unfairPrices.get(i);
            runnerPrices.add(newRP(i, unfairPrice, 10d));
        }
        return runnerPrices;
    }

    public RunnerPrices newRP(long selectionId, double bestBack, double bestLay, OptionalDouble lastMatchedPrice) {
        if (!lastMatchedPrice.isPresent()) {
            lastMatchedPrice = roundingService.roundBet((bestBack + bestLay) / 2);
        }
        var backBestPrice = new Price(bestBack, 100d, Side.BACK);
        var layBestPrice = new Price(bestLay, 100d, Side.LAY);
        return RunnerPrices.create(selectionId, List.of(
                backBestPrice,
                layBestPrice,
                roundingService.decrement(backBestPrice, 1).get(),
                roundingService.decrement(backBestPrice, 2).get(),
                roundingService.increment(layBestPrice, 1).get(),
                roundingService.increment(layBestPrice, 2).get()),
                100d, lastMatchedPrice.getAsDouble());
    }

    public MarketPrices createMarket(double betBack, double bestLay, OptionalDouble lastMatched, int winnerCount) {
        var market = createMarket();
        var home = newRP(CoreTestFactory.HOME, betBack, bestLay, lastMatched);
        var draw = newRP(CoreTestFactory.DRAW, betBack, bestLay, lastMatched);
        var away = newRP(CoreTestFactory.AWAY, betBack, bestLay, lastMatched);
        return MarketPricesTest.create(winnerCount, market, List.of(home, draw, away), new Date());
    }

    public MarketPrices createMarket(double downgradeFraction, List<Double> probabilities) {
        var market = createMarket();
        var runnerPrices = new LinkedList<RunnerPrices>();
        for (var i = 0; i < probabilities.size(); i++) {
            var fairPrice = 1 / probabilities.get(i);
            var backPrice = priceService.downgrade(fairPrice, downgradeFraction, Side.LAY);
            var backRounded = roundingService.roundBet(backPrice).getAsDouble();
            var layPrice = priceService.downgrade(fairPrice, downgradeFraction, Side.BACK);
            var layRounded = roundingService.roundBet(layPrice).getAsDouble();
            var selectionId = CoreTestFactory.HOME + i;
            var lastMatched = roundingService.roundBet(fairPrice).getAsDouble();
            runnerPrices.add(newRP(selectionId, backRounded, layRounded, OptionalDouble.of(lastMatched)));
        }
        return MarketPricesTest.create(1, market, runnerPrices, new Date());
    }

    private Market createMarket() {
        var event = Event.create("1", "Vischya Liga", addHours(new Date(), 2), CoreTestFactory.COUNTRY_CODE);
        event.setId("1");
        var market = CoreTestFactory.newMarket();
        market.setEvent(event);
        return market;
    }
}
