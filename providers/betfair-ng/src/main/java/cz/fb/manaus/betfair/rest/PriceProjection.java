package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

import java.util.Set;


public class PriceProjection {
    private Set<PriceData> priceData;

    public Set<PriceData> getPriceData() {
        return priceData;
    }

    public void setPriceData(Set<PriceData> priceData) {
        this.priceData = priceData;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("priceData", priceData)
                .toString();
    }
}

