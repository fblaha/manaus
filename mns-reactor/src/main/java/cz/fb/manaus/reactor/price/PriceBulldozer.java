package cz.fb.manaus.reactor.price;

import com.google.common.base.Preconditions;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.PriceComparator;
import cz.fb.manaus.core.model.TradedVolume;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class PriceBulldozer {

    @Autowired
    private RoundingService roundingService;

    public List<Price> bulldoze(double threshold, List<Price> prices) {
        var sum = 0d;
        Preconditions.checkState(PriceComparator.ORDERING.isStrictlyOrdered(prices));
        var convicts = new LinkedList<Price>();
        var untouched = new LinkedList<Price>();
        for (var price : prices) {
            if (sum >= threshold) {
                untouched.add(price);
            } else {
                convicts.add(price);
            }
            sum += price.getAmount();
        }
        var priceMean = TradedVolume.getWeightedMean(convicts);
        if (priceMean.isPresent()) {
            var amount = convicts.stream().mapToDouble(Price::getAmount).sum();
            var price = roundingService.roundBet(priceMean.getAsDouble()).getAsDouble();
            untouched.addFirst(new Price(price, amount, prices.get(0).getSide()));
        }
        return untouched;
    }

}
