package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RunnerPrices
import cz.fb.manaus.core.model.SettledBet
import java.util.*

abstract class AbstractOfferedAmountFunction : ProgressFunction {

    override fun apply(bet: SettledBet): OptionalDouble {
        val runnerPrices = getRunnerPrices(bet)
        val sum = runnerPrices.prices.map { it.amount }.sum()
        return OptionalDouble.of(sum)
    }

    protected abstract fun getRunnerPrices(bet: SettledBet): RunnerPrices
}
