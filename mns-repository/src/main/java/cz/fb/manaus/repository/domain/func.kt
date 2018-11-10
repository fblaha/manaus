package cz.fb.manaus.repository.domain

fun getOverround(bestPrices: List<Double>): Double {
    return bestPrices.map { 1 / it }.sum()
}

fun getBestPrices(runnerPrices: List<RunnerPrices>, type: Side): List<Double?> {
    return runnerPrices.map { it.getHomogeneous(type).bestPrice?.price }
}

fun getOverround(runnerPrices: List<RunnerPrices>, type: Side): Double? {
    val bestPrices = getBestPrices(runnerPrices, type)
    return if (bestPrices.all { it != null }) {
        getOverround(bestPrices.filterNotNull())
    } else {
        null
    }
}

fun getReciprocal(runnerPrices: List<RunnerPrices>, type: Side): Double? {
    val overround = getOverround(runnerPrices, type)
    return if (overround == null) null else 1 / overround
}

fun getRunnerPrices(runnerPrices: List<RunnerPrices>, selectionId: Long): RunnerPrices {
    return runnerPrices.first { it.selectionId == selectionId }
}


