package cz.fb.manaus.core.provider

enum class ProviderCapability {
    FixedStepPrice,
    ContinuousPrice,
    LastMatchedPrice,
    TradedVolume,
    MatchedAmount
}

fun validateProviderCapabilities(capabilities: Set<ProviderCapability>) {
    check(ProviderCapability.FixedStepPrice in capabilities || ProviderCapability.ContinuousPrice in capabilities)
}