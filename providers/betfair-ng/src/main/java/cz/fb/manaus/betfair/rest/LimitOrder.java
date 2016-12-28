package cz.fb.manaus.betfair.rest;


import com.google.common.base.MoreObjects;

public class LimitOrder {

    private double size;
    private double price;
    private PersistenceType persistenceType;

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PersistenceType getPersistenceType() {
        return persistenceType;
    }

    public void setPersistenceType(PersistenceType persistenceType) {
        this.persistenceType = persistenceType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("size", size)
                .add("price", price)
                .add("persistenceType", persistenceType)
                .toString();
    }
}
