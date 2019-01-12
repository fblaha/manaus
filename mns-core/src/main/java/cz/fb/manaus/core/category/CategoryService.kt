package cz.fb.manaus.core.category

import cz.fb.manaus.core.category.categorizer.Categorizer
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.category.categorizer.SimulationAware
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Service

@Service
class CategoryService(private val categorizers: List<Categorizer> = emptyList(),
                      private val settledBetCategorizers: List<RealizedBetCategorizer> = emptyList()) {

    fun getMarketCategories(market: Market, simulationAwareOnly: Boolean): Set<String> {
        return filterCategorizers(categorizers, simulationAwareOnly)
                .flatMap { it.getCategories(market).asSequence() }.toSet()
    }

    fun getRealizedBetCategories(realizedBet: RealizedBet, simulationAwareOnly: Boolean, coverage: BetCoverage): Set<String> {
        val prices = realizedBet.betAction.runnerPrices
        return filterCategorizers(settledBetCategorizers, simulationAwareOnly)
                .filter { prices.isNotEmpty() || !it.isMarketSnapshotRequired }
                .flatMap { it.getCategories(realizedBet, coverage).asSequence() }
                .toSet()
    }

    fun filterBets(realizedBets: List<RealizedBet>, projection: String, coverage: BetCoverage): List<RealizedBet> {
        return realizedBets.filter { input ->
            val categories = getRealizedBetCategories(input, false, coverage)
            categories.any { projection in it }
        }
    }

    private fun <T : SimulationAware> filterCategorizers(
            categorizers: List<T>,
            simulationAwareOnly: Boolean): Sequence<T> {
        val catSeq = categorizers.asSequence()
        return if (simulationAwareOnly)
            catSeq.filter { it.isSimulationSupported }
        else
            catSeq
    }
}
