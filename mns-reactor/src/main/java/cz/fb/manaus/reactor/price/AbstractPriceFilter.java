package cz.fb.manaus.reactor.price;


import com.google.common.collect.Range;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.PriceComparator;
import cz.fb.manaus.core.model.Side;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        var bySide = prices.stream().filter(this::priceRangeFilter)
                .collect(Collectors.groupingBy(Price::getSide));
        var sortedBack = PriceComparator.ORDERING.immutableSortedCopy(
                bySide.getOrDefault(Side.BACK, List.of()));
        var sortedLay = PriceComparator.ORDERING.immutableSortedCopy(
                bySide.getOrDefault(Side.LAY, List.of()));
        var bulldozedBack = bulldozer.bulldoze(bulldozeThreshold, sortedBack);
        var bulldozedLay = bulldozer.bulldoze(bulldozeThreshold, sortedLay);
        var topBack = bulldozedBack.stream().limit(minCount);
        var topLay = bulldozedLay.stream().limit(minCount);
        return Stream.concat(topBack, topLay).collect(Collectors.toList());
    }


    private boolean priceRangeFilter(Price price) {
        return priceRange.contains(price.getPrice());
    }


    public List<Price> filter(List<Price> prices) {
        return getSignificantPrices(minCount, prices);
    }

}
