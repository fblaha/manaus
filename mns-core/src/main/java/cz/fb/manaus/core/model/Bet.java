package cz.fb.manaus.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

public class Bet {

    private String betId;
    private String marketId;
    private long selectionId;
    private Price requestedPrice;
    private Date placedDate;
    private double matchedAmount;
    private int actionId;


    public Bet(String betId, String marketId, long selectionId, Price requestedPrice, Date placedDate, double matchedAmount) {
        this.marketId = marketId;
        this.selectionId = selectionId;
        this.requestedPrice = requestedPrice;
        this.betId = betId;
        this.placedDate = placedDate;
        this.matchedAmount = matchedAmount;
    }

    public Bet() {
    }

    public String getMarketId() {
        return marketId;
    }

    public Date getPlacedDate() {
        return placedDate;
    }

    public Price getRequestedPrice() {
        return requestedPrice;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public double getMatchedAmount() {
        return matchedAmount;
    }

    @JsonIgnore
    public boolean isMatched() {
        return !Price.amountEq(getMatchedAmount(), 0);
    }

    @JsonIgnore
    public boolean isHalfMatched() {
        return getMatchedAmount() > getRequestedPrice().getAmount() / 2;
    }

    public String getBetId() {
        return betId;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public Bet replacePrice(double newPrice) {
        var newOne = new Price(newPrice, getRequestedPrice().getAmount(), getRequestedPrice().getSide());
        var bet = new Bet(getBetId(), getMarketId(), getSelectionId(), newOne, placedDate, matchedAmount);
        bet.setActionId(actionId);
        return bet;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        var other = (Bet) obj;
        return new EqualsBuilder().append(marketId, other.marketId).append(selectionId, other.selectionId)
                .append(requestedPrice, other.requestedPrice).append(betId, other.betId)
                .append(matchedAmount, other.matchedAmount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(marketId).append(selectionId).append(requestedPrice)
                .append(betId).append(matchedAmount).append(actionId).toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("betId", betId)
                .add("marketId", marketId)
                .add("selectionId", selectionId)
                .add("requestedPrice", requestedPrice)
                .add("placedDate", placedDate)
                .add("matchedAmount", matchedAmount)
                .add("actionId", actionId)
                .toString();
    }
}
