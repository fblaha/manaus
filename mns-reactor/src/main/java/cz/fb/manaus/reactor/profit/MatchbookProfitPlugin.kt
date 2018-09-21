package cz.fb.manaus.reactor.profit

import com.google.common.collect.ImmutableMap
import cz.fb.manaus.core.model.SettledBet
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("matchbook")
class MatchbookProfitPlugin : ProfitPlugin {

    override fun getCharges(bets: List<SettledBet>, chargeRate: Double): Map<String, Double> {
        val result = ImmutableMap.builder<String, Double>()
        for (bet in bets) {
            val betId = bet.betAction.betId
            result.put(betId, getCharge(chargeRate, bet.profitAndLoss, bet.price.amount))
        }
        return result.build()
    }

    internal fun getCharge(chargeRate: Double, profitAndLoss: Double, amount: Double): Double {
        return if (profitAndLoss < 0) {
            Math.min(amount, -profitAndLoss) * chargeRate
        } else {
            profitAndLoss * chargeRate
        }
    }
}
