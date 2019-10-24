package cz.fb.manaus.reactor.rounding

import cz.fb.manaus.core.provider.ProviderMatcher
import org.springframework.stereotype.Service

@Service
class RoundingService(private val plugins: List<RoundingPlugin>) {

    fun increment(price: Double, stepNum: Int, providerMatcher: ProviderMatcher): Double? {
        val plugin = findPlugin(providerMatcher)
        val result = plugin.shift(price, stepNum)
        if (result != null) check(result > price)
        return result
    }

    private fun findPlugin(providerMatcher: ProviderMatcher): RoundingPlugin {
        return plugins.find(providerMatcher) ?: error("no such plugin")
    }

    fun decrement(price: Double, stepNum: Int, minPrice: Double, providerMatcher: ProviderMatcher): Double? {
        val result = findPlugin(providerMatcher).shift(price, -stepNum)
        if (result != null) {
            check(result < price)
            if (result < minPrice) {
                return null
            }
        }
        return result
    }

    fun roundBet(price: Double, providerMatcher: ProviderMatcher): Double? {
        return findPlugin(providerMatcher).round(price)
    }

}
