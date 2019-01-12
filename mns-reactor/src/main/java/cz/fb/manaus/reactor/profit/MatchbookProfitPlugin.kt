package cz.fb.manaus.reactor.profit

import cz.fb.manaus.core.model.RealizedBet
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("matchbook")
class MatchbookProfitPlugin : ProfitPlugin {

    override fun getCharges(bets: List<RealizedBet>, chargeRate: Double): Map<String, Double> {
        return bets
                .map { it.settledBet.id to getCharge(chargeRate, it.settledBet.profitAndLoss, it.settledBet.price.amount) }
                .toMap()
    }

    internal fun getCharge(chargeRate: Double, profitAndLoss: Double, amount: Double): Double {
        return if (profitAndLoss < 0) {
            Math.min(amount, -profitAndLoss) * chargeRate
        } else {
            profitAndLoss * chargeRate
        }
    }
}
