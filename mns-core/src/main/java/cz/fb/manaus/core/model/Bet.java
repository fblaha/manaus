package cz.fb.manaus.core.model;

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
    private Bet predecessor;


    public Bet(String betId, String marketId, long selectionId, Price requestedPrice, Date placedDate, double matchedAmount) {
        this.marketId = marketId;
        this.selectionId = selectionId;
        this.requestedPrice = requestedPrice;
        this.betId = betId;
        this.placedDate = placedDate;
        this.matchedAmount = matchedAmount;

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

    public boolean isMatched() {
        return !Price.amountEq(getMatchedAmount(), 0);
    }

    public boolean isHalfMatched() {
        return getMatchedAmount() > getRequestedPrice().getAmount() / 2;
    }

    public String getBetId() {
        return betId;
    }

    public Bet getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Bet predecessor) {
        this.predecessor = predecessor;
    }

    public Bet replacePrice(double newPrice) {
        Price newOne = new Price(newPrice, getRequestedPrice().getAmount(), getRequestedPrice().getSide());
        Bet bet = new Bet(getBetId(), getMarketId(), getSelectionId(), newOne, placedDate, matchedAmount);
        bet.setPredecessor(predecessor);
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
        final Bet other = (Bet) obj;
        return new EqualsBuilder().append(marketId, other.marketId).append(selectionId, other.selectionId)
                .append(requestedPrice, other.requestedPrice).append(betId, other.betId)
                .append(matchedAmount, other.matchedAmount)
                .append(predecessor, other.predecessor)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(marketId).append(selectionId).append(requestedPrice)
                .append(betId).append(matchedAmount).append(predecessor)
                .toHashCode();
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
                .add("predecessor", predecessor)
                .toString();
    }
}
