package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Date;

public class MatchedBet {
    private long id;
    @JsonProperty("odds-type")
    private String oddsType;
    @JsonProperty("decimal-odds")
    private double decimalOdds;
    private double odds;
    private double commission;
    private double stake;
    @JsonProperty("potential-profit")
    private double potentialProfit;
    private String currency;
    @JsonProperty("created-at")
    private Date createdAt;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public double getOdds() {
        return odds;
    }

    public void setOdds(double odds) {
        this.odds = odds;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public double getStake() {
        return stake;
    }

    public void setStake(double stake) {
        this.stake = stake;
    }

    public double getPotentialProfit() {
        return potentialProfit;
    }

    public void setPotentialProfit(double potentialProfit) {
        this.potentialProfit = potentialProfit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("oddsType", oddsType)
                .add("decimalOdds", decimalOdds)
                .add("odds", odds)
                .add("commission", commission)
                .add("stake", stake)
                .add("potentialProfit", potentialProfit)
                .add("currency", currency)
                .add("createdAt", createdAt)
                .toString();
    }
}
