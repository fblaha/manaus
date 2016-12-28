package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Date;

public class SettledRunner {
    private long id;

    @JsonProperty("event-id")
    private long eventId;
    @JsonProperty("market-id")
    private long marketId;
    @JsonProperty("event-name")
    private String eventName;
    @JsonProperty("market-type")
    private String marketType;
    @JsonProperty("profit-and-loss")
    private String profitAndLoss;
    @JsonProperty("settled-at")
    private Date settledAt;
    private String stake;
    private String name;
    private String odds;
    @JsonProperty("sport-id")
    private long sportId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getProfitAndLoss() {
        return profitAndLoss;
    }

    public void setProfitAndLoss(String profitAndLoss) {
        this.profitAndLoss = profitAndLoss;
    }

    public Date getSettledAt() {
        return settledAt;
    }

    public void setSettledAt(Date settledAt) {
        this.settledAt = settledAt;
    }

    public String getStake() {
        return stake;
    }

    public void setStake(String stake) {
        this.stake = stake;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOdds() {
        return odds;
    }

    public void setOdds(String odds) {
        this.odds = odds;
    }

    public long getMarketId() {
        return marketId;
    }

    public void setMarketId(long marketId) {
        this.marketId = marketId;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public long getSportId() {
        return sportId;
    }

    public void setSportId(long sportId) {
        this.sportId = sportId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("eventId", eventId)
                .add("marketId", marketId)
                .add("eventName", eventName)
                .add("marketType", marketType)
                .add("profitAndLoss", profitAndLoss)
                .add("settledAt", settledAt)
                .add("stake", stake)
                .add("name", name)
                .add("odds", odds)
                .add("sportId", sportId)
                .toString();
    }
}


