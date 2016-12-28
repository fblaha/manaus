package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

public class ReplaceInstruction {

    private String betId;
    private double newPrice;


    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("betId", betId)
                .add("newPrice", newPrice)
                .toString();
    }
}
