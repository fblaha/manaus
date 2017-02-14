package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Date;
import java.util.List;

public class Event {

    private long id;
    private String name;
    private Date start;
    private String status;
    @JsonProperty("sport-id")
    private long sportId;
    @JsonProperty("category-id")
    private List<Long> categoryId;
    @JsonProperty("in-running-flag")
    private boolean inRunningFlag;
    @JsonProperty("allow-live-betting")
    private boolean allowLiveBetting;
    @JsonProperty("market-ids")
    private List<Long> marketIds;
    @JsonProperty("meta-tags")
    private List<MetaTag> metaTags;
    private List<Market> markets;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getSportId() {
        return sportId;
    }

    public void setSportId(long sportId) {
        this.sportId = sportId;
    }

    public List<Long> getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(List<Long> categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isInRunningFlag() {
        return inRunningFlag;
    }

    public void setInRunningFlag(boolean inRunningFlag) {
        this.inRunningFlag = inRunningFlag;
    }

    public boolean isAllowLiveBetting() {
        return allowLiveBetting;
    }

    public void setAllowLiveBetting(boolean allowLiveBetting) {
        this.allowLiveBetting = allowLiveBetting;
    }

    public List<Long> getMarketIds() {
        return marketIds;
    }

    public void setMarketIds(List<Long> marketIds) {
        this.marketIds = marketIds;
    }

    public List<MetaTag> getMetaTags() {
        return metaTags;
    }

    public void setMetaTags(List<MetaTag> metaTags) {
        this.metaTags = metaTags;
    }

    public List<Market> getMarkets() {
        return markets;
    }

    public void setMarkets(List<Market> markets) {
        this.markets = markets;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("start", start)
                .add("status", status)
                .add("sportId", sportId)
                .add("categoryId", categoryId)
                .add("inRunningFlag", inRunningFlag)
                .add("allowLiveBetting", allowLiveBetting)
                .add("marketIds", marketIds)
                .add("metaTags", metaTags)
                .add("markets", markets)
                .toString();
    }
}
