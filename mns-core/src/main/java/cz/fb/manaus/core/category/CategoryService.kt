package cz.fb.manaus.core.category

import cz.fb.manaus.core.category.categorizer.Categorizer
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.category.categorizer.SimulationAware
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Service
import java.util.*

@Service
class CategoryService(private val categorizers: List<Categorizer> = emptyList(),
                      private val settledBetCategorizers: List<RealizedBetCategorizer> = emptyList()) {

    fun getMarketCategories(market: Market, simulationAwareOnly: Boolean): Set<String> {
        val result = HashSet<String>()
        for (categorizer in filterCategorizers(categorizers, simulationAwareOnly)) {
            val categories = categorizer.getCategories(market)
            result.addAll(categories)
        }
        return result.toSet()
    }

    fun getRealizedBetCategories(realizedBet: RealizedBet, simulationAwareOnly: Boolean, coverage: BetCoverage): Set<String> {
        val result = HashSet<String>()
        for (categorizer in filterCategorizers(settledBetCategorizers, simulationAwareOnly)) {
            val prices = realizedBet.betAction.runnerPrices
            if (prices.isEmpty() && categorizer.isMarketSnapshotRequired) continue
            val categories = categorizer.getCategories(realizedBet, coverage)
            result.addAll(categories)
        }
        return result.toSet()
    }

    fun filterBets(realizedBets: List<RealizedBet>, projection: String, coverage: BetCoverage): List<RealizedBet> {
        return realizedBets.filter { input ->
            val categories = getRealizedBetCategories(input, false, coverage)
            categories.any { projection in it }
        }
    }

    private fun <T : SimulationAware> filterCategorizers(
            categorizers: List<T>, simulationAwareOnly: Boolean): List<T> {
        return if (simulationAwareOnly)
            categorizers.filter { it.isSimulationSupported }
        else
            categorizers
    }
}
