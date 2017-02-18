package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

public class SettledSelection {
    private String id;
    private String name;
    private String side;
    private double odds;
    private double stake;
    private double commission;
    @JsonProperty("profit-and-loss")
    private String profitAndLoss;
    private List<SettledBet> bets;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
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

    public String getProfitAndLoss() {
        return profitAndLoss;
    }

    public void setProfitAndLoss(String profitAndLoss) {
        this.profitAndLoss = profitAndLoss;
    }

    public List<SettledBet> getBets() {
        return bets;
    }

    public void setBets(List<SettledBet> bets) {
        this.bets = bets;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("side", side)
                .add("odds", odds)
                .add("stake", stake)
                .add("commission", commission)
                .add("profitAndLoss", profitAndLoss)
                .add("bets", bets)
                .toString();
    }
}


