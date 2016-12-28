package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

public class SettledMarket {
    @JsonProperty("sub-total")
    private String subTotal;
    @JsonProperty("odds-type")
    private String oddsType;
    @JsonProperty("total-stake")
    private String totalStake;
    @JsonProperty("commission-credit")
    private String commissionCredit;
    private String commission;
    @JsonProperty("total-profit-and-loss")
    private String totalProfitAndLoss;
    private String currency;
    private List<SettledRunner> runners;

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getOddsType() {
        return oddsType;
    }

    public void setOddsType(String oddsType) {
        this.oddsType = oddsType;
    }

    public String getTotalStake() {
        return totalStake;
    }

    public void setTotalStake(String totalStake) {
        this.totalStake = totalStake;
    }

    public List<SettledRunner> getRunners() {
        return runners;
    }

    public void setRunners(List<SettledRunner> runners) {
        this.runners = runners;
    }

    public String getCommissionCredit() {
        return commissionCredit;
    }

    public void setCommissionCredit(String commissionCredit) {
        this.commissionCredit = commissionCredit;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getTotalProfitAndLoss() {
        return totalProfitAndLoss;
    }

    public void setTotalProfitAndLoss(String totalProfitAndLoss) {
        this.totalProfitAndLoss = totalProfitAndLoss;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("subTotal", subTotal)
                .add("oddsType", oddsType)
                .add("totalStake", totalStake)
                .add("runners", runners)
                .add("commissionCredit", commissionCredit)
                .add("commission", commission)
                .add("totalProfitAndLoss", totalProfitAndLoss)
                .add("currency", currency)
                .toString();
    }
}
