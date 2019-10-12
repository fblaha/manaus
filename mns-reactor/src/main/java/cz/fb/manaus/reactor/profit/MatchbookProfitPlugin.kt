package cz.fb.manaus.reactor.profit

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.SettledBet
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
@Profile("matchbook")
class MatchbookProfitPlugin : ProfitPlugin {

    override fun getCharges(bets: List<RealizedBet>, chargeRate: Double): Map<String, Double> {
        return bets.asSequence().map { it.settledBet }
                .map { it.id to getCharge(chargeRate, it) }
                .toMap()
    }

    private fun getCharge(chargeRate: Double, bet: SettledBet): Double {
        return if (bet.commission != null) {
            bet.commission!!
        } else {
            getCountedCharge(chargeRate, bet.profitAndLoss, bet.price.amount)
        }
    }

    internal fun getCountedCharge(chargeRate: Double, profitAndLoss: Double, amount: Double): Double {
        return if (profitAndLoss < 0) {
            min(amount, -profitAndLoss) * chargeRate
        } else {
            profitAndLoss * chargeRate
        }
    }
}
