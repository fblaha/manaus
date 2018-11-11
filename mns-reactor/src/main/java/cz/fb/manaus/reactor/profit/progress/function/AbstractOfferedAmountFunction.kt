package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.RunnerPrices

abstract class AbstractOfferedAmountFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double {
        val runnerPrices = getRunnerPrices(bet)
        return runnerPrices.prices.map { it.amount }.sum()
    }

    protected abstract fun getRunnerPrices(bet: RealizedBet): RunnerPrices
}
