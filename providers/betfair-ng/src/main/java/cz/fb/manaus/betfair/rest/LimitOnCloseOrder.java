package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

public class LimitOnCloseOrder {
    private double liability;
    private double price;

    public double getLiability() {
        return liability;
    }

    public void setLiability(double liability) {
        this.liability = liability;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("liability", liability)
                .add("price", price)
                .toString();
    }
}
