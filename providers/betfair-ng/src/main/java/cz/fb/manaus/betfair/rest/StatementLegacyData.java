package cz.fb.manaus.betfair.rest;


import com.google.common.base.MoreObjects;

import java.util.Date;

public class StatementLegacyData {

    private double avgPrice;
    private double betSize;
    private String betType;
    private String betCategoryType;
    private String commissionRate;
    private long eventId;
    private long eventTypeId;
    private String fullMarketName;
    private double grossBetAmount;
    private String marketName;
    private String marketType;
    private Date placedDate;
    private long selectionId;
    private String selectionName;
    private Date startDate;
    private String transactionType;
    private long transactionId;
    private WinLose winLose;

    public double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public double getBetSize() {
        return betSize;
    }

    public void setBetSize(double betSize) {
        this.betSize = betSize;
    }

    public String getBetType() {
        return betType;
    }

    public void setBetType(String betType) {
        this.betType = betType;
    }

    public String getBetCategoryType() {
        return betCategoryType;
    }

    public void setBetCategoryType(String betCategoryType) {
        this.betCategoryType = betCategoryType;
    }

    public String getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(String commissionRate) {
        this.commissionRate = commissionRate;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getFullMarketName() {
        return fullMarketName;
    }

    public void setFullMarketName(String fullMarketName) {
        this.fullMarketName = fullMarketName;
    }

    public double getGrossBetAmount() {
        return grossBetAmount;
    }

    public void setGrossBetAmount(double grossBetAmount) {
        this.grossBetAmount = grossBetAmount;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public Date getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(Date placedDate) {
        this.placedDate = placedDate;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(long selectionId) {
        this.selectionId = selectionId;
    }

    public String getSelectionName() {
        return selectionName;
    }

    public void setSelectionName(String selectionName) {
        this.selectionName = selectionName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public WinLose getWinLose() {
        return winLose;
    }

    public void setWinLose(WinLose winLose) {
        this.winLose = winLose;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("avgPrice", avgPrice)
                .add("betSize", betSize)
                .add("betType", betType)
                .add("betCategoryType", betCategoryType)
                .add("commissionRate", commissionRate)
                .add("eventId", eventId)
                .add("eventTypeId", eventTypeId)
                .add("fullMarketName", fullMarketName)
                .add("grossBetAmount", grossBetAmount)
                .add("marketName", marketName)
                .add("marketType", marketType)
                .add("placedDate", placedDate)
                .add("selectionId", selectionId)
                .add("selectionName", selectionName)
                .add("startDate", startDate)
                .add("transactionType", transactionType)
                .add("transactionId", transactionId)
                .add("winLose", winLose)
                .toString();
    }
}
