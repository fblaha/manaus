package cz.fb.manaus.core.provider

data class ExchangeProvider(
        val name: String,
        val minAmount: Double,
        val minPrice: Double,
        val commission: Double,
        val capabilities: Set<ProviderCapability>) {

    fun validate() {
        validateProviderCapabilities(capabilities)
    }

    fun capabilityMatch(required: RequiredCapabilitiesAware) = capabilities.containsAll(required.requiredCapabilities)
}


