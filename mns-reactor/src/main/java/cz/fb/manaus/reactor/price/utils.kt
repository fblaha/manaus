package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.RunnerPrices
import cz.fb.manaus.core.model.Side

fun getOverRound(bestPrices: List<Double>): Double {
    return bestPrices.sumOf { 1 / it }
}

fun getBestPrices(runnerPrices: List<RunnerPrices>, side: Side): List<Double?> {
    return runnerPrices.map { it.getHomogeneous(side).bestPrice?.price }
}

fun getOverRound(runnerPrices: List<RunnerPrices>, side: Side): Double? {
    val bestPrices = getBestPrices(runnerPrices, side)
    return if (bestPrices.all { it != null }) {
        getOverRound(bestPrices.filterNotNull())
    } else {
        null
    }
}

fun getReciprocal(runnerPrices: List<RunnerPrices>, side: Side): Double? {
    return when (val overRound = getOverRound(runnerPrices, side)) {
        null -> null
        else -> 1 / overRound
    }
}

fun getRunnerPrices(runnerPrices: List<RunnerPrices>, selectionId: Long): RunnerPrices {
    return runnerPrices.first { it.selectionId == selectionId }
}