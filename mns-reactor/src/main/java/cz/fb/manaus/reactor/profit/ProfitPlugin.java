package cz.fb.manaus.reactor.profit;

import cz.fb.manaus.core.model.SettledBet;

import java.util.List;
import java.util.Map;

public interface ProfitPlugin {

    Map<String, Double> getCharges(List<SettledBet> bets, double chargeRate);

}
