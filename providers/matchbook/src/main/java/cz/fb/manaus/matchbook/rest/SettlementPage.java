package cz.fb.manaus.matchbook.rest;

import com.google.common.base.MoreObjects;

import java.util.List;

public class SettlementPage extends AbstractPage {

    private String currency;
    private List<Settlement> markets;

    public List<Settlement> getMarkets() {
        return markets;
    }

    public void setMarkets(List<Settlement> markets) {
        this.markets = markets;
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
                .add("offset", getOffset())
                .add("total", getTotal())
                .add("perPage", getPerPage())
                .add("currency", getCurrency())
                .add("markets", getMarkets())
                .toString();
    }
}
