package cz.fb.manaus.reactor.rounding

import cz.fb.manaus.core.provider.CapabilityPredicate
import org.springframework.stereotype.Service

@Service
class RoundingService(private val plugins: List<RoundingPlugin>) {

    fun increment(price: Double, stepNum: Int, capabilityPredicate: CapabilityPredicate): Double? {
        val plugin = findPlugin(capabilityPredicate)
        val result = plugin.shift(price, stepNum)
        if (result != null) check(result > price)
        return result
    }

    private fun findPlugin(capabilityPredicate: CapabilityPredicate): RoundingPlugin {
        return plugins.find(capabilityPredicate) ?: error("no such plugin")
    }

    fun decrement(price: Double, stepNum: Int, minPrice: Double, capabilityPredicate: CapabilityPredicate): Double? {
        val result = findPlugin(capabilityPredicate).shift(price, -stepNum)
        if (result != null) {
            check(result < price)
            if (result < minPrice) {
                return null
            }
        }
        return result
    }

    fun roundBet(price: Double, capabilityPredicate: CapabilityPredicate): Double? {
        return findPlugin(capabilityPredicate).round(price)
    }

}
