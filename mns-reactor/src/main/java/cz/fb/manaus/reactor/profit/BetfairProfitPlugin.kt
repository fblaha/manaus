package cz.fb.manaus.reactor.profit

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.reactor.charge.MarketCharge
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("betfair")
class BetfairProfitPlugin : ProfitPlugin {

    override fun getCharges(bets: List<RealizedBet>, chargeRate: Double): Map<String, Double> {
        val result = mutableMapOf<String, Double>()
        val marketMap = bets.groupBy { it.market.id }
        for (marketBets in marketMap.values) {
            val charge = MarketCharge.fromBets(chargeRate, marketBets.map { it.settledBet })
            val charges = marketBets.map { it.settledBet.id }
                    .onEach { check(it !in result) }
                    .associate { it to charge.getChargeContribution(it) }
            result.putAll(charges)
        }
        return result
    }
}
