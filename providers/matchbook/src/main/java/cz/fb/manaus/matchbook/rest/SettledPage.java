package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

public class SettledPage extends AbstractPage {

    private String language;
    @JsonProperty("odds-type")
    private String oddsType;
    @JsonProperty("overall-staked-amount")
    private double overallStakedAmount;
    @JsonProperty("profit-and-loss")
    private String profitAndLoss;

    private List<SettledEvent> events;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOddsType() {
        return oddsType;
    }

    public void setOddsType(String oddsType) {
        this.oddsType = oddsType;
    }

    public double getOverallStakedAmount() {
        return overallStakedAmount;
    }

    public void setOverallStakedAmount(double overallStakedAmount) {
        this.overallStakedAmount = overallStakedAmount;
    }

    public String getProfitAndLoss() {
        return profitAndLoss;
    }

    public void setProfitAndLoss(String profitAndLoss) {
        this.profitAndLoss = profitAndLoss;
    }

    public List<SettledEvent> getEvents() {
        return events;
    }

    public void setEvents(List<SettledEvent> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("language", language)
                .add("oddsType", oddsType)
                .add("overallStakedAmount", overallStakedAmount)
                .add("profitAndLoss", profitAndLoss)
                .add("events", events)
                .toString();
    }
}
