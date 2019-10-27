package cz.fb.manaus.core.provider

import java.util.*

data class ExchangeProvider(
        val name: String,
        val minAmount: Double,
        val minPrice: Double,
        val commission: Double,
        val tags: Set<ProviderTag>) {

    private val enumTags = EnumSet.copyOf(tags)

    fun validate() {
        validateTags(enumTags)
    }

    fun matches(selector: ProviderSelector) = enumTags.containsAll(selector.tags)
}


