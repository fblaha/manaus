package cz.fb.manaus.reactor.profit

import com.google.common.collect.ImmutableMap
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.reactor.charge.MarketCharge
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("betfair")
class BetfairProfitPlugin : ProfitPlugin {

    override fun getCharges(bets: List<SettledBet>, chargeRate: Double): Map<String, Double> {
        val result = ImmutableMap.builder<String, Double>()
        val marketMap = bets.groupBy { bet -> bet.betAction.market.id }
        for (marketBets in marketMap.values) {
            val charge = MarketCharge.fromBets(chargeRate, marketBets)
            for (bet in marketBets) {
                val betId = bet.betAction.betId
                result.put(betId, charge.getChargeContribution(betId))
            }
        }
        return result.build()
    }
}
