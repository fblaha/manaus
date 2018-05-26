package cz.fb.manaus.reactor.profit;

import com.google.common.collect.ImmutableMap;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Profile("matchbook")
public class MatchbookProfitPlugin implements ProfitPlugin {

    @Override
    public Map<String, Double> getCharges(List<SettledBet> bets, double chargeRate) {
        var result = ImmutableMap.<String, Double>builder();
        for (var bet : bets) {
            var betId = bet.getBetAction().getBetId();
            result.put(betId, getCharge(chargeRate, bet.getProfitAndLoss(), bet.getPrice().getAmount()));
        }
        return result.build();
    }

    double getCharge(double chargeRate, double profitAndLoss, double amount) {
        if (profitAndLoss < 0) {
            return Math.min(amount, -profitAndLoss) * chargeRate;
        } else {
            return profitAndLoss * chargeRate;
        }
    }
}
