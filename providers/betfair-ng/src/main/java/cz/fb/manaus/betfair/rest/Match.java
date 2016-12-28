package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

import java.util.Date;

public class Match {

    private String betId;
    private String matchId;
    private String side;
    private Double price;
    private Double Size;
    private Date matchDate;

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSize() {
        return Size;
    }

    public void setSize(Double size) {
        Size = size;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("betId", betId)
                .add("matchId", matchId)
                .add("side", side)
                .add("price", price)
                .add("Size", Size)
                .add("matchDate", matchDate)
                .toString();
    }
}
