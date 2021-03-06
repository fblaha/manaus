package cz.fb.manaus.reactor.ml

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction
import org.springframework.stereotype.Component

@Component
class BetFeatureService(private val functions: List<ProgressFunction>) {

    fun toFeatureVector(bet: RealizedBet): BetFeatureVector {
        val features = this.functions.associate { it.name to it(bet) }
        return BetFeatureVector(
                id = bet.settledBet.id,
                side = bet.settledBet.price.side,
                profit = bet.settledBet.profitAndLoss,
                features = features
        )
    }
}