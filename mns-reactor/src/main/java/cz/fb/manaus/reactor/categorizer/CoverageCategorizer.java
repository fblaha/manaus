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
        String marketId = settledBet.getBetAction().getMarket().getId();
        long selectionId = settledBet.getSelectionId();
        Set<Side> sides = coverage.getSides(marketId, selectionId);
        Preconditions.checkState(sides.size() > 0);
        ImmutableMap.Builder<Side, Double> builder = ImmutableMap.builder();
        for (Side side : sides) {
            builder.put(side, coverage.getAmount(marketId, selectionId, side));
        }
        Map<Side, Double> amounts = builder.build();
        return getCategories(settledBet.getPrice().getSide(), amounts);
    }

    Set<String> getCategories(Side mySide, Map<Side, Double> amounts) {
        String sideFormatted = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, mySide.name());
        if (EnumSet.of(mySide).equals(amounts.keySet())) {
            String soloSide = "solo" + sideFormatted;
            return Set.of(PREFIX + soloSide, PREFIX + "solo");
        } else if (EnumSet.of(mySide, mySide.getOpposite()).equals(amounts.keySet())) {
            ImmutableSet.Builder<String> builder = ImmutableSet.<String>builder().add(PREFIX + "both");
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
