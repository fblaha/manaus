package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

public class PriceSize {
    private Double price;
    private Double size;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("price", price)
                .add("size", size)
                .toString();
    }
}
