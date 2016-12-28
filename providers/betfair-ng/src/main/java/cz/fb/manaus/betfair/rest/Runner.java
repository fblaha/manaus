package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

import java.util.Date;
import java.util.List;

public class Runner {
    private Long selectionId;
    private Double handicap;
    private String status;
    private Double adjustmentFactor;
    private Double lastPriceTraded;
    private Double totalMatched;
    private Date removalDate;
    private StartingPrices sp;
    private ExchangePrices ex;
    private List<Order> orders;
    private List<Match> matches;

    public Long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(Long selectionId) {
        this.selectionId = selectionId;
    }

    public Double getHandicap() {
        return handicap;
    }

    public void setHandicap(Double handicap) {
        this.handicap = handicap;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAdjustmentFactor() {
        return adjustmentFactor;
    }

    public void setAdjustmentFactor(Double adjustmentFactor) {
        this.adjustmentFactor = adjustmentFactor;
    }

    public Double getLastPriceTraded() {
        return lastPriceTraded;
    }

    public void setLastPriceTraded(Double lastPriceTraded) {
        this.lastPriceTraded = lastPriceTraded;
    }

    public Double getTotalMatched() {
        return totalMatched;
    }

    public void setTotalMatched(Double totalMatched) {
        this.totalMatched = totalMatched;
    }

    public Date getRemovalDate() {
        return removalDate;
    }

    public void setRemovalDate(Date removalDate) {
        this.removalDate = removalDate;
    }

    public StartingPrices getSp() {
        return sp;
    }

    public void setSp(StartingPrices sp) {
        this.sp = sp;
    }

    public ExchangePrices getEx() {
        return ex;
    }

    public void setEx(ExchangePrices ex) {
        this.ex = ex;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("selectionId", selectionId)
                .add("handicap", handicap)
                .add("status", status)
                .add("adjustmentFactor", adjustmentFactor)
                .add("lastPriceTraded", lastPriceTraded)
                .add("totalMatched", totalMatched)
                .add("removalDate", removalDate)
                .add("sp", sp)
                .add("ex", ex)
                .add("orders", orders)
                .add("matches", matches)
                .toString();
    }
}
