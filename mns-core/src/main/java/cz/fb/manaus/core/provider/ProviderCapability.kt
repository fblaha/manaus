package cz.fb.manaus.core.provider

enum class ProviderCapability {
    PriceShiftFixedStep {
        override fun validate(providerCapabilities: Set<ProviderCapability>) {
            check(PriceShiftContinuous !in providerCapabilities)
        }
    },
    PriceShiftContinuous {
        override fun validate(providerCapabilities: Set<ProviderCapability>) {
            check(PriceShiftFixedStep !in providerCapabilities)
        }
    },
    LastMatchedPrice,
    TradedVolume,
    MatchedAmount;

    open fun validate(providerCapabilities: Set<ProviderCapability>) {}

}


fun validateProviderCapabilities(capabilities: Set<ProviderCapability>) {
    capabilities.forEach { it.validate(capabilities) }
}