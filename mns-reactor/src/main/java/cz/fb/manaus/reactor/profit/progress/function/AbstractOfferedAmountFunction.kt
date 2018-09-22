package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RunnerPrices
import cz.fb.manaus.core.model.SettledBet

abstract class AbstractOfferedAmountFunction : ProgressFunction {

    override fun invoke(bet: SettledBet): Double {
        val runnerPrices = getRunnerPrices(bet)
        return runnerPrices.prices.map { it.amount }.sum()
    }

    protected abstract fun getRunnerPrices(bet: SettledBet): RunnerPrices
}
