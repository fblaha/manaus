package cz.fb.manaus.core.provider

data class ExchangeProvider(
        val name: String,
        val minAmount: Double,
        val minPrice: Double,
        val commission: Double,
        val tags: Set<String>,
        val capabilities: Set<ProviderCapability>) {

    fun validate() {
        validateProviderCapabilities(capabilities)
    }

    fun matches(selector: ProviderSelector) =
            capabilities.containsAll(selector.capabilities) && tags.containsAll(selector.tags)
}


