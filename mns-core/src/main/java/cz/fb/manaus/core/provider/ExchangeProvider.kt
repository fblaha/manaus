package cz.fb.manaus.core.provider

data class ExchangeProvider(
        val name: String,
        val minAmount: Double,
        val minPrice: Double,
        val commission: Double,
        val tags: Set<ProviderTag>) {

    fun validate() {
        validateTags(tags)
    }

    fun matches(selector: ProviderSelector) = tags.containsAll(selector.tags)
}


