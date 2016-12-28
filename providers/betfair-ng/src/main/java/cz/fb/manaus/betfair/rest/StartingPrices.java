package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

import java.util.List;

public class StartingPrices {
    private Double nearPrice;
    private Double farPrice;
    private List<PriceSize> backStakeTaken = null;
    private List<PriceSize> layLiabilityTaken = null;
    private Double actualSP;

    public Double getNearPrice() {
        return nearPrice;
    }

    public void setNearPrice(Double nearPrice) {
        this.nearPrice = nearPrice;
    }

    public Double getFarPrice() {
        return farPrice;
    }

    public void setFarPrice(Double farPrice) {
        this.farPrice = farPrice;
    }

    public List<PriceSize> getBackStakeTaken() {
        return backStakeTaken;
    }

    public void setBackStakeTaken(List<PriceSize> backStakeTaken) {
        this.backStakeTaken = backStakeTaken;
    }

    public List<PriceSize> getLayLiabilityTaken() {
        return layLiabilityTaken;
    }

    public void setLayLiabilityTaken(List<PriceSize> layLiabilityTaken) {
        this.layLiabilityTaken = layLiabilityTaken;
    }

    public Double getActualSP() {
        return actualSP;
    }

    public void setActualSP(Double actualSP) {
        this.actualSP = actualSP;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("nearPrice", nearPrice)
                .add("farPrice", farPrice)
                .add("backStakeTaken", backStakeTaken)
                .add("layLiabilityTaken", layLiabilityTaken)
                .add("actualSP", actualSP)
                .toString();
    }
}
