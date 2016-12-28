package cz.fb.manaus.reactor.categorizer;


import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.price.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class MatchedPriceCategorizer implements SettledBetCategorizer {

    public static final String PREFIX = "matchedPrice_";

    @Autowired
    private PriceService priceService;

    @Override
    public boolean isSimulationSupported() {
        return false;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        double matched = settledBet.getPrice().getPrice();
        double requested = settledBet.getBetAction().getPrice().getPrice();
        Side side = settledBet.getPrice().getSide();
        return Collections.singleton(getCategory(matched, requested, side));
    }

    String getCategory(double matched, double requested, Side side) {
        if (Price.priceEq(matched, requested)) {
            return PREFIX + "equal";
        } else {
            boolean downgrade = priceService.isDowngrade(matched, requested, side);
            if (downgrade) {
                return PREFIX + "better";
            } else {
                return PREFIX + "worse";
            }
        }
    }

}