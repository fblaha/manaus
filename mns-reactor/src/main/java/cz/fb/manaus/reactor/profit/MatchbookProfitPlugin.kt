package cz.fb.manaus.reactor.profit

import cz.fb.manaus.core.model.RealizedBet
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("matchbook")
class MatchbookProfitPlugin : ProfitPlugin {

    override fun getCharges(bets: List<RealizedBet>, chargeRate: Double): Map<String, Double> {
        val result = mutableMapOf<String, Double>()
        for (bet in bets) {
            val betId = bet.settledBet.id
            result[betId] = getCharge(chargeRate, bet.settledBet.profitAndLoss, bet.settledBet.price.amount)
        }
        return result.toMap()
    }

    internal fun getCharge(chargeRate: Double, profitAndLoss: Double, amount: Double): Double {
        return if (profitAndLoss < 0) {
            Math.min(amount, -profitAndLoss) * chargeRate
        } else {
            profitAndLoss * chargeRate
        }
    }
}
