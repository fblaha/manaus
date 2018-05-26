package cz.fb.manaus.reactor.categorizer;


import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class CoverageCategorizer implements SettledBetCategorizer {

    public static final String PREFIX = "coverage_";

    @Override
    public boolean isSimulationSupported() {
        return false;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        var marketId = settledBet.getBetAction().getMarket().getId();
        var selectionId = settledBet.getSelectionId();
        var sides = coverage.getSides(marketId, selectionId);
        Preconditions.checkState(sides.size() > 0);
        var builder = ImmutableMap.<Side, Double>builder();
        for (var side : sides) {
            builder.put(side, coverage.getAmount(marketId, selectionId, side));
        }
        var amounts = builder.build();
        return getCategories(settledBet.getPrice().getSide(), amounts);
    }

    Set<String> getCategories(Side mySide, Map<Side, Double> amounts) {
        var sideFormatted = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, mySide.name());
        if (EnumSet.of(mySide).equals(amounts.keySet())) {
            var soloSide = "solo" + sideFormatted;
            return Set.of(PREFIX + soloSide, PREFIX + "solo");
        } else if (EnumSet.of(mySide, mySide.getOpposite()).equals(amounts.keySet())) {
            var builder = ImmutableSet.<String>builder().add(PREFIX + "both");
            builder.add(PREFIX + "both");
            builder.add(PREFIX + "both" + sideFormatted);
            if (Price.amountEq(amounts.get(Side.LAY), amounts.get(Side.BACK))) {
                builder.add(PREFIX + "bothEqual");
            } else if (amounts.get(Side.LAY) > amounts.get(Side.BACK)) {
                builder.add(PREFIX + "bothLayGt");
            } else if (amounts.get(Side.LAY) < amounts.get(Side.BACK)) {
                builder.add(PREFIX + "bothBackGt");
            }
            return builder.build();
        }
        throw new IllegalStateException();
    }

}
