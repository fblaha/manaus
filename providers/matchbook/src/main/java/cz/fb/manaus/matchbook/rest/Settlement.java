package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Date;

public class Settlement {

    @JsonProperty("sport-id")
    private long sportId;
    @JsonProperty("event-id")
    private long eventId;
    @JsonProperty("event-name")
    private String eventName;
    @JsonProperty("market-id")
    private long marketId;
    @JsonProperty("market-type")
    private String marketType;
    private String stake;
    @JsonProperty("profit-and-loss")
    private String profitAndLoss;
    @JsonProperty("settled-at")
    private Date settledAt;

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

    public String getStake() {
        return stake;
    }

    public void setStake(String stake) {
        this.stake = stake;
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

    public long getSportId() {
        return sportId;
    }

    public void setSportId(long sportId) {
        this.sportId = sportId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("sportId", sportId)
                .add("eventId", eventId)
                .add("eventName", eventName)
                .add("marketId", marketId)
                .add("marketType", marketType)
                .add("stake", stake)
                .add("profitAndLoss", profitAndLoss)
                .add("settledAt", settledAt)
                .toString();
    }
}
