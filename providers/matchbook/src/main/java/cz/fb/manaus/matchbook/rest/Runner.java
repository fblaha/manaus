package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

public class Runner {

    private String name;
    private String status;
    private double handicap;
    @JsonProperty("event-id")
    private long eventId;
    private long id;
    @JsonProperty("market-id")
    private long marketId;
    @JsonProperty("asian-handicap")
    private String asianHandicap;
    private List<Price> prices;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getHandicap() {
        return handicap;
    }

    public void setHandicap(double handicap) {
        this.handicap = handicap;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMarketId() {
        return marketId;
    }

    public void setMarketId(long marketId) {
        this.marketId = marketId;
    }

    public String getAsianHandicap() {
        return asianHandicap;
    }

    public void setAsianHandicap(String asianHandicap) {
        this.asianHandicap = asianHandicap;
    }


    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("status", status)
                .add("handicap", handicap)
                .add("eventId", eventId)
                .add("id", id)
                .add("marketId", marketId)
                .add("asianHandicap", asianHandicap)
                .add("prices", prices)
                .toString();
    }
}
