package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class Balance {

    private long id;
    private double balance;
    private double exposure;
    @JsonProperty("free-funds")
    private double freeFunds;
    @JsonProperty("commission-credit")
    private double commissionCredit;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getExposure() {
        return exposure;
    }

    public void setExposure(double exposure) {
        this.exposure = exposure;
    }

    public double getFreeFunds() {
        return freeFunds;
    }

    public void setFreeFunds(double freeFunds) {
        this.freeFunds = freeFunds;
    }

    public double getCommissionCredit() {
        return commissionCredit;
    }

    public void setCommissionCredit(double commissionCredit) {
        this.commissionCredit = commissionCredit;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("balance", balance)
                .add("exposure", exposure)
                .add("freeFunds", freeFunds)
                .add("commissionCredit", commissionCredit)
                .toString();
    }
}
