package cz.fb.manaus.core.category

import com.google.common.collect.ImmutableSet.copyOf
import cz.fb.manaus.core.category.categorizer.Categorizer
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.category.categorizer.SimulationAware
import cz.fb.manaus.core.repository.domain.Market
import cz.fb.manaus.core.repository.domain.RealizedBet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class CategoryService {
    @Autowired
    private val categorizers = mutableListOf<Categorizer>()
    @Autowired
    private val settledBetCategorizers = mutableListOf<RealizedBetCategorizer>()

    fun getMarketCategories(market: Market, simulationAwareOnly: Boolean): Set<String> {
        val result = HashSet<String>()
        for (categorizer in filterCategorizers(categorizers, simulationAwareOnly)) {
            val categories = categorizer.getCategories(market)
            result.addAll(categories)
        }
        return copyOf(result)
    }

    fun getRealizedBetCategories(realizedBet: RealizedBet, simulationAwareOnly: Boolean, coverage: BetCoverage): Set<String> {
        val result = HashSet<String>()
        for (categorizer in filterCategorizers(settledBetCategorizers, simulationAwareOnly)) {
            val prices = realizedBet.betAction.runnerPrices
            if (prices.isEmpty() && categorizer.isMarketSnapshotRequired) continue
            val categories = categorizer.getCategories(realizedBet, coverage)
            result.addAll(categories)
        }
        return copyOf(result)
    }

    fun filterBets(realizedBets: List<RealizedBet>, projection: String, coverage: BetCoverage): List<RealizedBet> {
        return realizedBets.filter { input ->
            val categories = getRealizedBetCategories(input, false, coverage)
            categories.any { it.contains(projection) }
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
