package cz.fb.manaus.reactor.price;


import com.google.common.collect.Range;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.PriceComparator;
import cz.fb.manaus.core.model.Side;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.concat;

public abstract class AbstractPriceFilter {

    private final int minCount;
    private final double bulldozeThreshold;
    private final Range<Double> priceRange;
    @Autowired
    private PriceBulldozer bulldozer;


    public AbstractPriceFilter(int minCount, double bulldozeThreshold,
                               Range<Double> priceRange) {
        this.minCount = minCount;
        this.bulldozeThreshold = bulldozeThreshold;
        this.priceRange = priceRange;
    }

    List<Price> getSignificantPrices(int minCount, List<Price> prices) {
        Map<Side, List<Price>> bySide = prices.stream().filter(this::priceRangeFilter)
                .collect(Collectors.groupingBy(Price::getSide));
        List<Price> sortedBack = PriceComparator.ORDERING.immutableSortedCopy(bySide.get(Side.BACK));
        List<Price> sortedLay = PriceComparator.ORDERING.immutableSortedCopy(bySide.get(Side.LAY));
        List<Price> bulldozedBack = bulldozer.bulldoze(bulldozeThreshold, sortedBack);
        List<Price> bulldozedLay = bulldozer.bulldoze(bulldozeThreshold, sortedLay);
        List<Price> topBack = from(bulldozedBack).limit(minCount).toList();
        List<Price> topLay = from(bulldozedLay).limit(minCount).toList();
        return copyOf(concat(topBack, topLay));
    }


    private boolean priceRangeFilter(Price price) {
        return priceRange.contains(price.getPrice());
    }


    public List<Price> filter(List<Price> prices) {
        return getSignificantPrices(minCount, prices);
    }

}
