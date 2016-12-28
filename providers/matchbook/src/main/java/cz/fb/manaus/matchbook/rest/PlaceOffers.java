
package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

public class PlaceOffers {

    @JsonProperty("odds-type")
    private String oddsType;
    @JsonProperty("exchange-type")
    private String exchangeType;
    @JsonProperty("offers")
    private List<Offer> offers;

    public String getOddsType() {
        return oddsType;
    }

    public void setOddsType(String oddsType) {
        this.oddsType = oddsType;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("oddsType", oddsType)
                .add("exchangeType", exchangeType)
                .add("offers", offers)
                .toString();
    }
}
