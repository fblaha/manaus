package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

import java.util.List;

public class MarketCatalogue {

    private String marketId;
    private String marketName;
    private double totalMatched;
    private MarketDescription description;
    private List<RunnerCatalog> runners;
    private EventType eventType;
    private Competition competition;
    private Event event;

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public MarketDescription getDescription() {
        return description;
    }

    public void setDescription(MarketDescription description) {
        this.description = description;
    }

    public List<RunnerCatalog> getRunners() {
        return runners;
    }

    public void setRunners(List<RunnerCatalog> runners) {
        this.runners = runners;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public double getTotalMatched() {
        return totalMatched;
    }

    public void setTotalMatched(double totalMatched) {
        this.totalMatched = totalMatched;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("marketId", marketId)
                .add("marketName", marketName)
                .add("totalMatched", totalMatched)
                .add("description", description)
                .add("runners", runners)
                .add("eventType", eventType)
                .add("competition", competition)
                .add("event", event)
                .toString();
    }
}
