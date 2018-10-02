package cz.fb.manaus.core.category

import com.google.common.collect.ImmutableSet.copyOf
import cz.fb.manaus.core.category.categorizer.Categorizer
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.category.categorizer.SimulationAware
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.SettledBet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class CategoryService {
    @Autowired
    private val categorizers = mutableListOf<Categorizer>()
    @Autowired
    private val settledBetCategorizers = mutableListOf<SettledBetCategorizer>()

    fun getMarketCategories(market: Market, simulationAwareOnly: Boolean): Set<String> {
        val result = HashSet<String>()
        for (categorizer in filterCategorizers(categorizers, simulationAwareOnly)) {
            val categories = categorizer.getCategories(market)
            result.addAll(categories)
        }
        return copyOf(result)
    }

    fun getSettledBetCategories(settledBet: SettledBet, simulationAwareOnly: Boolean, coverage: BetCoverage): Set<String> {
        val result = HashSet<String>()
        for (categorizer in filterCategorizers(settledBetCategorizers, simulationAwareOnly)) {
            val prices = settledBet.betAction.marketPrices
            if (prices == null && categorizer.isMarketSnapshotRequired) continue
            val categories = categorizer.getCategories(settledBet, coverage)
            result.addAll(categories)
        }
        return copyOf(result)
    }

    fun filterBets(settledBets: List<SettledBet>, projection: String, coverage: BetCoverage): List<SettledBet> {
        // TODO parallel stream was here
        return settledBets.filter { input ->
            val categories = getSettledBetCategories(input, false, coverage)
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
