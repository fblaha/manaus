package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Offer {

    private Long id;
    @JsonProperty("event-id")
    private Long eventId;
    @JsonProperty("event-name")
    private String eventName;
    @JsonProperty("market-id")
    private Long marketId;
    @JsonProperty("market-name")
    private String marketName;
    @JsonProperty("runner-id")
    private long runnerId;
    @JsonProperty("runner-name")
    private String runnerName;
    @JsonProperty("temp-id")
    private long tempId;
    private String side;
    @JsonProperty("odds-type")
    private String oddsType;
    @JsonProperty("decimal-odds")
    private double decimalOdds;
    @JsonProperty("exchange-type")
    private String exchangeType;
    private double odds;
    private Double remaining;
    private double stake;
    @JsonProperty("potential-profit")
    private Double potentialProfit;
    @JsonProperty("remaining-potential-profit")
    private Double remainingPotentialProfit;
    private String currency;
    @JsonProperty("created-at")
    private Date createdAt;
    private String status;
    @JsonProperty("matched-bets")
    private List<MatchedBet> matchedBets;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
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

    public long getTempId() {
        return tempId;
    }

    public void setTempId(long tempId) {
        this.tempId = tempId;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getOddsType() {
        return oddsType;
    }

    public void setOddsType(String oddsType) {
        this.oddsType = oddsType;
    }

    public double getDecimalOdds() {
        return decimalOdds;
    }

    public void setDecimalOdds(double decimalOdds) {
        this.decimalOdds = decimalOdds;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public double getOdds() {
        return odds;
    }

    public void setOdds(double odds) {
        this.odds = odds;
    }

    public Double getRemaining() {
        return remaining;
    }

    public void setRemaining(Double remaining) {
        this.remaining = remaining;
    }

    public double getStake() {
        return stake;
    }

    public void setStake(double stake) {
        this.stake = stake;
    }

    public Double getPotentialProfit() {
        return potentialProfit;
    }

    public void setPotentialProfit(Double potentialProfit) {
        this.potentialProfit = potentialProfit;
    }

    public Double getRemainingPotentialProfit() {
        return remainingPotentialProfit;
    }

    public void setRemainingPotentialProfit(Double remainingPotentialProfit) {
        this.remainingPotentialProfit = remainingPotentialProfit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MatchedBet> getMatchedBets() {
        return matchedBets;
    }

    public void setMatchedBets(List<MatchedBet> matchedBets) {
        this.matchedBets = matchedBets;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("eventId", eventId)
                .add("eventName", eventName)
                .add("marketId", marketId)
                .add("marketName", marketName)
                .add("runnerId", runnerId)
                .add("runnerName", runnerName)
                .add("tempId", tempId)
                .add("side", side)
                .add("oddsType", oddsType)
                .add("decimalOdds", decimalOdds)
                .add("exchangeType", exchangeType)
                .add("odds", odds)
                .add("remaining", remaining)
                .add("stake", stake)
                .add("potentialProfit", potentialProfit)
                .add("remainingPotentialProfit", remainingPotentialProfit)
                .add("currency", currency)
                .add("createdAt", createdAt)
                .add("status", status)
                .add("matchedBets", matchedBets)
                .toString();
    }
}
