package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

public class OfferPage extends AbstractPage {

    @JsonProperty("exchange-type")
    private String exchangeType;
    @JsonProperty("odds-type")
    private String oddsType;
    private String currency;
    private String language;
    private List<Offer> offers;


    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getOddsType() {
        return oddsType;
    }

    public void setOddsType(String oddsType) {
        this.oddsType = oddsType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("exchangeType", exchangeType)
                .add("oddsType", oddsType)
                .add("currency", currency)
                .add("language", language)
                .add("offers", offers)
                .toString();
    }
}
