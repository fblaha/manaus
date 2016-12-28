package cz.fb.manaus.reactor.charge;

import com.google.common.collect.ImmutableMap;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.max;

public class MarketCharge {

    private final double totalProfit;
    private final double totalPositiveProfit;
    private final double totalCharge;
    private final Map<String, Double> profits;

    private MarketCharge(double totalProfit, double totalPositiveProfit, double totalCharge, Map<String, Double> profits) {
        this.totalProfit = totalProfit;
        this.totalPositiveProfit = totalPositiveProfit;
        this.totalCharge = totalCharge;
        this.profits = ImmutableMap.copyOf(profits);
    }

    public static MarketCharge fromBets(double chargeRate, Iterable<SettledBet> bets) {
        Map<String, Double> profits = new HashMap<>();
        double totalProfit = 0d, totalPositiveProfit = 0d;
        for (SettledBet bet : bets) {
            String betId = bet.getBetAction().getBetId();
            profits.put(betId, bet.getProfitAndLoss());
            totalProfit += bet.getProfitAndLoss();
            totalPositiveProfit += max(bet.getProfitAndLoss(), 0d);
        }
        double totalCharge = Price.round(chargeRate * max(totalProfit, 0));
        return new MarketCharge(totalProfit, totalPositiveProfit, totalCharge, profits);
    }

    public double getChargeContribution(String betId) {
        if (Price.amountEq(totalCharge, 0d)) return 0d;
        double profit = max(profits.get(betId), 0d);
        return Price.round(totalCharge * profit / totalPositiveProfit);
    }

    public double getTotalProfit() {
        return Price.round(totalProfit);
    }

    public double getTotalCharge() {
        return Price.round(totalCharge);
    }
}
