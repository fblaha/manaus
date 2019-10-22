package cz.fb.manaus.core.provider

data class ExchangeProvider(
        val name: String,
        val minAmount: Double,
        val minPrice: Double,
        val commission: Double,
        val capabilities: Set<ProviderCapability> = emptySet()) {

    fun validate() {
        validateProviderCapabilities(capabilities)
    }

    fun hasCapabilities(required: RequiredCapabilitiesAware): Boolean = capabilities.containsAll(required.requiredCapabilities)
}


