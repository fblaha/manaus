package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Date;


public class SettledBet {

    private long id;
    @JsonProperty("sport-id")
    private long sportId;
    @JsonProperty("offer-id")
    private long offerId;
    @JsonProperty("in-play")
    private boolean inPlay;
    @JsonProperty("matched-time")
    private Date matchedTime;
    @JsonProperty("settled-time")
    private Date settledTime;

    private double odds;
    private double stake;
    private double commission;
    @JsonProperty("commission-rate")
    private double commissionRate;
    @JsonProperty("profit-and-loss")
    private String profitAndLoss;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSportId() {
        return sportId;
    }

    public void setSportId(long sportId) {
        this.sportId = sportId;
    }

    public long getOfferId() {
        return offerId;
    }

    public void setOfferId(long offerId) {
        this.offerId = offerId;
    }

    public boolean getInPlay() {
        return inPlay;
    }

    public void setInPlay(boolean inPlay) {
        this.inPlay = inPlay;
    }

    public Date getMatchedTime() {
        return matchedTime;
    }

    public void setMatchedTime(Date matchedTime) {
        this.matchedTime = matchedTime;
    }

    public Date getSettledTime() {
        return settledTime;
    }

    public void setSettledTime(Date settledTime) {
        this.settledTime = settledTime;
    }

    public double getOdds() {
        return odds;
    }

    public void setOdds(double odds) {
        this.odds = odds;
    }

    public double getStake() {
        return stake;
    }

    public void setStake(double stake) {
        this.stake = stake;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public double getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(double commissionRate) {
        this.commissionRate = commissionRate;
    }

    public String getProfitAndLoss() {
        return profitAndLoss;
    }

    public void setProfitAndLoss(String profitAndLoss) {
        this.profitAndLoss = profitAndLoss;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("sportId", sportId)
                .add("offerId", offerId)
                .add("inPlay", inPlay)
                .add("matchedTime", matchedTime)
                .add("settledTime", settledTime)
                .add("odds", odds)
                .add("stake", stake)
                .add("commission", commission)
                .add("commissionRate", commissionRate)
                .add("profitAndLoss", profitAndLoss)
                .toString();
    }
}
