package cz.fb.manaus.reactor.profit;

import com.google.common.collect.ImmutableMap;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.reactor.charge.MarketCharge;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Component
@Profile("betfair")
public class BetfairProfitPlugin implements ProfitPlugin {

    @Override
    public Map<String, Double> getCharges(List<SettledBet> bets, double chargeRate) {
        var result = ImmutableMap.<String, Double>builder();
        var marketMap = bets.stream().collect(groupingBy(bet -> bet.getBetAction().getMarket().getId()));
        for (var marketBets : marketMap.values()) {
            var charge = MarketCharge.fromBets(chargeRate, marketBets);
            for (var bet : marketBets) {
                var betId = bet.getBetAction().getBetId();
                result.put(betId, charge.getChargeContribution(betId));
            }
        }
        return result.build();
    }
}
