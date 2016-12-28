package cz.fb.manaus.core.model;


import com.google.common.base.MoreObjects;

public class AccountMoney {
    private final double total;
    private final double available;

    public AccountMoney(double total, double available) {
        this.total = total;
        this.available = available;
    }

    public double getTotal() {
        return total;
    }

    public double getAvailable() {
        return available;
    }

    public double getAvailableRate() {
        return available / total;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("total", total)
                .add("available", available)
                .toString();
    }
}
