package cz.fb.manaus.matchbook.rest;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

public class PlaceReport {

    @JsonProperty("errors")
    private List<Object> errors;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("balance")
    private double balance;
    @JsonProperty("exposure")
    private double exposure;
    @JsonProperty("language")
    private String language;
    @JsonProperty("offers")
    private List<Offer> offers;
    @JsonProperty("odds-type")
    private String oddsType;
    @JsonProperty("exchange-type")
    private String exchangeType;
    @JsonProperty("available-amount")
    private double availableAmount;

    public List<Object> getErrors() {
        return errors;
    }

    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getExposure() {
        return exposure;
    }

    public void setExposure(double exposure) {
        this.exposure = exposure;
    }

    public double getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(double availableAmount) {
        this.availableAmount = availableAmount;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("errors", errors)
                .add("currency", currency)
                .add("balance", balance)
                .add("exposure", exposure)
                .add("language", language)
                .add("offers", offers)
                .add("oddsType", oddsType)
                .add("exchangeType", exchangeType)
                .add("availableAmount", availableAmount)
                .toString();
    }
}
