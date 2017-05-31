package cz.fb.manaus.reactor.price;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.OptionalDouble;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

@Service
public class PriceService {

    @Autowired
    private RoundingService roundingService;
    @Autowired
    private ExchangeProvider provider;

    public double downgrade(double price, double downgradeFraction, Side side) {
        double aboveOne = price - 1;
        double targetFairness = 1 - downgradeFraction;
        Preconditions.checkState(Range.closed(0d, 1d).contains(targetFairness));

        if (requireNonNull(side) == Side.LAY) {
            return 1 + aboveOne * targetFairness;
        } else if (side == Side.BACK) {
            return 1 + aboveOne / targetFairness;
        }
        throw new IllegalStateException();
    }

    public boolean isDowngrade(double newPrice, double oldPrice, Side type) {
        if (Price.priceEq(newPrice, oldPrice)) return false;
        if (type == Side.BACK) {
            return newPrice > oldPrice;
        } else {
            return newPrice < oldPrice;
        }
    }

    public double getProgressiveAmount(double currPrice, double minPrice, double maxPrice, double baseAmount) {
        checkState(minPrice <= maxPrice);
        currPrice = Math.min(currPrice, maxPrice);
        double probability = 1 - 1 / Math.max(currPrice, minPrice);
        double baseProbability = 1 - 1 / maxPrice;
        return Price.round(baseAmount * baseProbability / probability);
    }

    /**
     * http://www.matterofstats.com/what-is-vig-and-overround/
     */
    @Deprecated
    public double getReciprocalFairPrice(double unfairPrice, double reciprocal) {
        return unfairPrice / reciprocal;
    }

    /**
     * https://cs.wikipedia.org/wiki/S%C3%A1zkov%C3%BD_kurz
     */
    public double getFairnessFairPrice(double unfairPrice, double fairness) {
        return 1 + (unfairPrice - 1) / fairness;
    }

    /**
     * http://stats.stackexchange.com/questions/140269/how-to-convert-sport-odds-into-percentage
     */
    public double getOverroundFairPrice(double unfairPrice, double overround, int winnerCount, int runnerCount) {
        double probability = 1 / unfairPrice - (overround - winnerCount) / runnerCount;
        Preconditions.checkArgument(probability > 0, Arrays.asList(unfairPrice, overround, winnerCount, runnerCount));
        return 1 / probability;
    }

    public OptionalDouble getRoundedFairnessFairPrice(double unfairPrice, double fairness) {
        return roundingService.roundBet(getFairnessFairPrice(unfairPrice, fairness));
    }

}
