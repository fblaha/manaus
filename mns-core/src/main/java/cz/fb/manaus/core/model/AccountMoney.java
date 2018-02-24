package cz.fb.manaus.core.model;


import com.google.common.base.MoreObjects;

public class AccountMoney {
    private double total;
    private double available;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getAvailable() {
        return available;
    }

    public void setAvailable(double available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("total", total)
                .add("available", available)
                .toString();
    }
}
