package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

/**
 * {"event":{"id":"27238535","name":"FC Zbrojovka Brno U21 v 1. SC Znojmo U21","countryCode":"CZ","timezone":"Europe/London","openDate":"2014-07-28T13:00:00.000Z"}
 */
public class EventResult implements MarketCountAware {
    private Event event;
    private int marketCount;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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
                .add("event", event)
                .add("marketCount", marketCount)
                .toString();
    }
}
