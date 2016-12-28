package cz.fb.manaus.betfair.rest;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

public class Filter {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<String> eventTypeIds;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<String> competitionIds;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<String> eventIds;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TimeRange marketStartTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<MarketBettingType> marketBettingTypes;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<OrderStatus> withOrders;


    public Set<String> getEventTypeIds() {
        return eventTypeIds;
    }

    public void setEventTypeIds(Set<String> eventTypeIds) {
        this.eventTypeIds = eventTypeIds;
    }

    public TimeRange getMarketStartTime() {
        return marketStartTime;
    }

    public void setMarketStartTime(TimeRange marketStartTime) {
        this.marketStartTime = marketStartTime;
    }

    public Set<String> getEventIds() {
        return eventIds;
    }

    public void setEventIds(Set<String> eventIds) {
        this.eventIds = eventIds;
    }

    public Set<String> getCompetitionIds() {
        return competitionIds;
    }

    public void setCompetitionIds(Set<String> competitionIds) {
        this.competitionIds = competitionIds;
    }

    public Set<MarketBettingType> getMarketBettingTypes() {
        return marketBettingTypes;
    }

    public void setMarketBettingTypes(Set<MarketBettingType> marketBettingTypes) {
        this.marketBettingTypes = marketBettingTypes;
    }

    public Set<OrderStatus> getWithOrders() {
        return withOrders;
    }

    public void setWithOrders(Set<OrderStatus> withOrders) {
        this.withOrders = withOrders;
    }
}
