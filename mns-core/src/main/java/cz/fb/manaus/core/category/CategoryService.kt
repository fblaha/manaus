package cz.fb.manaus.core.category

import cz.fb.manaus.core.category.categorizer.Categorizer
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.category.categorizer.SimulationAware
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categorizers: List<Categorizer> = emptyList(),
    private val settledBetCategorizers: List<RealizedBetCategorizer> = emptyList()
) {

    fun getMarketCategories(market: Market, simulationAwareOnly: Boolean): Set<String> {
        return filter(categorizers, simulationAwareOnly)
            .flatMap { it.getCategories(market).asSequence() }.toSet()
    }

    fun getRealizedBetCategories(realizedBet: RealizedBet, simulationAwareOnly: Boolean): Set<String> {
        return filter(settledBetCategorizers, simulationAwareOnly)
            .flatMap { it.getCategories(realizedBet).asSequence() }
            .toSet()
    }

    fun filterBets(realizedBets: List<RealizedBet>, projection: String): List<RealizedBet> {
        return realizedBets.filter { input ->
            val categories = getRealizedBetCategories(input, false)
            categories.any { projection in it }
        }
    }

    private fun <T : SimulationAware> filter(
        categorizers: List<T>,
        simulationAwareOnly: Boolean
    ): Sequence<T> {
        val catSeq = categorizers.asSequence()
        return if (simulationAwareOnly)
            catSeq.filter { it.isSimulationSupported }
        else
            catSeq
    }
}
