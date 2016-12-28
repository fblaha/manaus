package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Date;

public class SettledBet {

    private long id;
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
    @JsonProperty("runner-id")
    private long runnerId;
    @JsonProperty("runner-name")
    private String runnerName;
    private String odds;
    private String stake;
    @JsonProperty("profit-and-loss")
    private String profitAndLoss;
    @JsonProperty("placed-at")
    private Date placedAt;
    @JsonProperty("settled-at")
    private Date settledAt;
    private String action;

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

    public long getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(long runnerId) {
        this.runnerId = runnerId;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public String getOdds() {
        return odds;
    }

    public void setOdds(String odds) {
        this.odds = odds;
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

    public Date getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(Date placedAt) {
        this.placedAt = placedAt;
    }

    public Date getSettledAt() {
        return settledAt;
    }

    public void setSettledAt(Date settledAt) {
        this.settledAt = settledAt;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("sportId", sportId)
                .add("eventId", eventId)
                .add("eventName", eventName)
                .add("marketId", marketId)
                .add("marketType", marketType)
                .add("runnerId", runnerId)
                .add("runnerName", runnerName)
                .add("odds", odds)
                .add("stake", stake)
                .add("profitAndLoss", profitAndLoss)
                .add("placedAt", placedAt)
                .add("settledAt", settledAt)
                .add("action", action)
                .toString();
    }
}
