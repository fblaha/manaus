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
import java.util.OptionalDouble;

@Component
public class PriceBulldozer {

    @Autowired
    private RoundingService roundingService;

    public List<Price> bulldoze(double threshold, List<Price> prices) {
        double sum = 0;
        Preconditions.checkState(PriceComparator.ORDERING.isStrictlyOrdered(prices));
        LinkedList<Price> convicts = new LinkedList<>(), untouched = new LinkedList<>();
        for (Price price : prices) {
            if (sum >= threshold) {
                untouched.add(price);
            } else {
                convicts.add(price);
            }
            sum += price.getAmount();
        }
        OptionalDouble priceMean = TradedVolume.getWeightedMean(convicts);
        if (priceMean.isPresent()) {
            double amount = convicts.stream().mapToDouble(Price::getAmount).sum();
            double price = roundingService.roundBet(priceMean.getAsDouble()).getAsDouble();
            untouched.addFirst(new Price(price, amount, prices.get(0).getSide()));
        }
        return untouched;
    }

}
