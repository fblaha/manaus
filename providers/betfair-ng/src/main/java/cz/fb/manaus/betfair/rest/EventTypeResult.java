package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

public class EventTypeResult implements MarketCountAware {
    private EventType eventType;
    private int marketCount;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public int getMarketCount() {
        return marketCount;
    }

    public void setMarketCount(int marketCount) {
        this.marketCount = marketCount;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("eventType", eventType)
                .add("marketCount", marketCount)
                .toString();
    }
}
