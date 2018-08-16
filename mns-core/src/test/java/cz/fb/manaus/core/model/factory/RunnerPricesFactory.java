package cz.fb.manaus.core.model.factory;

import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;

import java.util.Collection;

public class RunnerPricesFactory {
    public static RunnerPrices create(long selectionId, Collection<Price> prices, Double matched, Double lastMatchedPrice) {
        var rp = new RunnerPrices();
        rp.setSelectionId(selectionId);
        rp.setPrices(prices);
        rp.setMatchedAmount(matched);
        rp.setLastMatchedPrice(lastMatchedPrice);
        return rp;
    }
}
