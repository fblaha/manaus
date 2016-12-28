package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class Price {

    private String currency;
    private double odds;
    @JsonProperty("available-amount")
    private double availableAmount;
    private String side;
    @JsonProperty("odds-type")
    private String oddsType;
    @JsonProperty("decimal-odds")
    private double decimalOdds;
    @JsonProperty("exchange-type")
    private String exchangeType;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getOdds() {
        return odds;
    }

    public void setOdds(double odds) {
        this.odds = odds;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getOddsType() {
        return oddsType;
    }

    public void setOddsType(String oddsType) {
        this.oddsType = oddsType;
    }

    public double getDecimalOdds() {
        return decimalOdds;
    }

    public void setDecimalOdds(double decimalOdds) {
        this.decimalOdds = decimalOdds;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
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
                .add("currency", currency)
                .add("odds", odds)
                .add("availableAmount", availableAmount)
                .add("side", side)
                .add("oddsType", oddsType)
                .add("decimalOdds", decimalOdds)
                .add("exchangeType", exchangeType)
                .toString();
    }
}
