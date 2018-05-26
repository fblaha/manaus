package cz.fb.manaus.reactor.betting.listener;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;

import java.util.Comparator;
import java.util.Map;

public class ProbabilityComparator implements Comparator<RunnerPrices> {

    public static Map<Side, Ordering<RunnerPrices>> COMPARATORS = Map.of(
            Side.BACK, Ordering.from(new ProbabilityComparator(Side.BACK)),
            Side.LAY, Ordering.from(new ProbabilityComparator(Side.LAY)));
    private final Side side;

    private ProbabilityComparator(Side side) {
        this.side = side;
    }

    @Override
    public int compare(RunnerPrices list1, RunnerPrices list2) {
        var backList1 = list1.getHomogeneous(side);
        var backList2 = list2.getHomogeneous(side);
        var price1 = backList1.getBestPrice().get().getPrice();
        var price2 = backList2.getBestPrice().get().getPrice();
        return Doubles.compare(price1, price2);
    }

}
