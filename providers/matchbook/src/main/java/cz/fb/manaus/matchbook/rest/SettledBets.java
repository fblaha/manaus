package cz.fb.manaus.matchbook.rest;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

public class SettledBets {

    @JsonProperty("odds-type")
    private String oddsType;
    private String currency;

    private List<SettledBet> bets;

    public String getOddsType() {
        return oddsType;
    }

    public void setOddsType(String oddsType) {
        this.oddsType = oddsType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<SettledBet> getBets() {
        return bets;
    }

    public void setBets(List<SettledBet> bets) {
        this.bets = bets;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("oddsType", oddsType)
                .add("currency", currency)
                .add("bets", bets)
                .toString();
    }
}
