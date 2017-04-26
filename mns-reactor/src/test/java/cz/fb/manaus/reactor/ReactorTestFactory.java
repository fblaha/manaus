package cz.fb.manaus.reactor;

import com.google.common.collect.ImmutableList;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Event;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
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
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

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
        Bet oldBet = newBet(new Price(5d, 5d, side));
        return newBetContext(side, marketPrices, runnerPrices, of(oldBet)).withNewPrice(oldBet.getRequestedPrice());
    }

    public BetContext newBetContext(Side side, MarketPrices marketPrices, RunnerPrices runnerPrices, Optional<Bet> oldBet) {
        Fairness fairness = new Fairness(OptionalDouble.of(0.9d), OptionalDouble.of(1.1));

        List<Bet> bets = new LinkedList<>();
        oldBet.ifPresent(bet -> bets.add(bet));
        MarketSnapshot snapshot = new MarketSnapshot(marketPrices, bets, empty());

        return contextFactory.create(side, CoreTestFactory.HOME, snapshot, fairness);
    }

    public RunnerPrices newRP(long selectionId, double bestBack, double bestLay) {
        return newRP(selectionId, bestBack, bestLay, OptionalDouble.empty());

    }

    public BetContext createContext(Side side, double bestBack, double bestLay) {
        MarketPrices marketPrices = createMarket(bestBack, bestLay, OptionalDouble.of(3d), 1);
        RunnerPrices runnerPrices = marketPrices.getRunnerPrices().iterator().next();
        long selectionId = runnerPrices.getSelectionId();
        Optional<Price> bestPrice = runnerPrices.getHomogeneous(side.getOpposite()).getBestPrice();
        List<Bet> bets = new LinkedList<>();
        if (bestPrice.isPresent()) {
            String marketId = CoreTestFactory.MARKET_ID;
            double price = bestPrice.get().getPrice();
            Price requestedPrice = new Price(price, provider.getMinAmount(), side.getOpposite());
            Instant date = Instant.now().minus(2, ChronoUnit.HOURS);
            Bet counterBet = new Bet(BET_ID, marketId, selectionId, requestedPrice,
                    Date.from(date), provider.getMinAmount());
            bets.add(counterBet);
        }
        MarketSnapshot snapshot = new MarketSnapshot(marketPrices, bets, empty());
        return contextFactory.create(side, selectionId, snapshot,
                calculator.getFairness(marketPrices));

    }

    public List<RunnerPrices> createRP(List<Double> unfairPrices) {
        List<RunnerPrices> runnerPrices = new LinkedList<>();
        for (int i = 0; i < unfairPrices.size(); i++) {
            double unfairPrice = unfairPrices.get(i);
            runnerPrices.add(newRP(i, unfairPrice, 10d));
        }
        return runnerPrices;
    }

    public RunnerPrices newRP(long selectionId, double bestBack, double bestLay, OptionalDouble lastMatchedPrice) {
        if (!lastMatchedPrice.isPresent()) {
            lastMatchedPrice = roundingService.roundBet((bestBack + bestLay) / 2);
        }
        Price backBestPrice = new Price(bestBack, 100d, Side.BACK);
        Price layBestPrice = new Price(bestLay, 100d, Side.LAY);
        return new RunnerPrices(selectionId, ImmutableList.of(
                backBestPrice,
                layBestPrice,
                roundingService.decrement(backBestPrice, 1).get(),
                roundingService.decrement(backBestPrice, 2).get(),
                roundingService.increment(layBestPrice, 1).get(),
                roundingService.increment(layBestPrice, 2).get()),
                100d, lastMatchedPrice.getAsDouble());
    }

    public MarketPrices createMarket(double betBack, double bestLay, OptionalDouble lastMatched, int winnerCount) {
        Market market = createMarket();
        RunnerPrices home = newRP(CoreTestFactory.HOME, betBack, bestLay, lastMatched);
        RunnerPrices draw = newRP(CoreTestFactory.DRAW, betBack, bestLay, lastMatched);
        RunnerPrices away = newRP(CoreTestFactory.AWAY, betBack, bestLay, lastMatched);
        return new MarketPrices(winnerCount, market, Arrays.asList(home, draw, away));
    }

    public MarketPrices createMarket(double downgradeFraction, List<Double> probabilities) {
        Market market = createMarket();
        List<RunnerPrices> runnerPrices = new LinkedList<>();
        for (int i = 0; i < probabilities.size(); i++) {
            double fairPrice = 1 / probabilities.get(i);
            double backPrice = priceService.downgrade(fairPrice, downgradeFraction, Side.LAY);
            double backRounded = roundingService.roundBet(backPrice).getAsDouble();
            double layPrice = priceService.downgrade(fairPrice, downgradeFraction, Side.BACK);
            double layRounded = roundingService.roundBet(layPrice).getAsDouble();
            long selectionId = CoreTestFactory.HOME + i;
            double lastMatched = roundingService.roundBet(fairPrice).getAsDouble();
            runnerPrices.add(newRP(selectionId, backRounded, layRounded, OptionalDouble.of(lastMatched)));
        }
        return new MarketPrices(1, market, runnerPrices);
    }

    private Market createMarket() {
        Event event = new Event("1", "Vischya Liga", addHours(new Date(), 2), CoreTestFactory.COUNTRY_CODE);
        event.setId("1");
        Market market = CoreTestFactory.newMarket();
        market.setEvent(event);
        return market;
    }

}
