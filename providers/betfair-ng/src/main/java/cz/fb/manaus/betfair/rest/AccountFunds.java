package cz.fb.manaus.betfair.rest;


import com.google.common.base.MoreObjects;

public class AccountFunds {

    private double availableToBetBalance;
    private double exposure;
    private double retainedCommission;
    private double exposureLimit;
    private double discountRate;
    private int pointsBalance;


    public double getAvailableToBetBalance() {
        return availableToBetBalance;
    }

    public void setAvailableToBetBalance(double availableToBetBalance) {
        this.availableToBetBalance = availableToBetBalance;
    }

    public double getExposure() {
        return exposure;
    }

    public void setExposure(double exposure) {
        this.exposure = exposure;
    }

    public double getRetainedCommission() {
        return retainedCommission;
    }

    public void setRetainedCommission(double retainedCommission) {
        this.retainedCommission = retainedCommission;
    }

    public double getExposureLimit() {
        return exposureLimit;
    }

    public void setExposureLimit(double exposureLimit) {
        this.exposureLimit = exposureLimit;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public int getPointsBalance() {
        return pointsBalance;
    }

    public void setPointsBalance(int pointsBalance) {
        this.pointsBalance = pointsBalance;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("availableToBetBalance", availableToBetBalance)
                .add("exposure", exposure)
                .add("retainedCommission", retainedCommission)
                .add("exposureLimit", exposureLimit)
                .add("discountRate", discountRate)
                .add("pointsBalance", pointsBalance)
                .toString();
    }
}
