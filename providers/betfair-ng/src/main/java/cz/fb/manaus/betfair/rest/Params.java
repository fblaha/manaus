package cz.fb.manaus.betfair.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Params {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Filter filter;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer maxResults;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<MarketProjection> marketProjection;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PriceProjection priceProjection;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<String> marketIds;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OrderProjection orderProjection;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MatchProjection matchProjection;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int fromRecord;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int recordCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private IncludeItem includeItem;


    public static Params withFilter(Filter filter) {
        Params params = new Params();
        params.setFilter(filter);
        return params;
    }

    public static Map<String, ?> betParams(String marketId, List<?> instructions) {
        return ImmutableMap.of("marketId", marketId, "instructions", instructions);
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public Set<MarketProjection> getMarketProjection() {
        return marketProjection;
    }

    public void setMarketProjection(Set<MarketProjection> marketProjection) {
        this.marketProjection = marketProjection;
    }

    public Set<String> getMarketIds() {
        return marketIds;
    }

    public void setMarketIds(Set<String> marketIds) {
        this.marketIds = marketIds;
    }

    public PriceProjection getPriceProjection() {
        return priceProjection;
    }

    public void setPriceProjection(PriceProjection priceProjection) {
        this.priceProjection = priceProjection;
    }

    public OrderProjection getOrderProjection() {
        return orderProjection;
    }

    public void setOrderProjection(OrderProjection orderProjection) {
        this.orderProjection = orderProjection;
    }

    public MatchProjection getMatchProjection() {
        return matchProjection;
    }

    public void setMatchProjection(MatchProjection matchProjection) {
        this.matchProjection = matchProjection;
    }

    public int getFromRecord() {
        return fromRecord;
    }

    public void setFromRecord(int fromRecord) {
        this.fromRecord = fromRecord;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public IncludeItem getIncludeItem() {
        return includeItem;
    }

    public void setIncludeItem(IncludeItem includeItem) {
        this.includeItem = includeItem;
    }
}
