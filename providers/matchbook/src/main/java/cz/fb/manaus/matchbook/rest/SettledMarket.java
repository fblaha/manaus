package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

public class SettledMarket {
    private long id;
    private String name;
    private double commission;
    @JsonProperty("profit-and-loss")
    private String profitAndLoss;
    private double stake;

    private List<SettledSelection> selections;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getStake() {
        return stake;
    }

    public void setStake(double stake) {
        this.stake = stake;
    }

    public List<SettledSelection> getSelections() {
        return selections;
    }

    public void setSelections(List<SettledSelection> selections) {
        this.selections = selections;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("commission", commission)
                .add("profitAndLoss", profitAndLoss)
                .add("stake", stake)
                .add("selections", selections)
                .toString();
    }
}
