package cz.fb.manaus.core.provider

enum class ProviderCapability {
    FixedStepPrice,
    ContinuousPrice,
    LastMatchedPrice,
    TradedVolume,
    MatchedAmount
}

val priceCapabilities = setOf(ProviderCapability.ContinuousPrice, ProviderCapability.FixedStepPrice)

fun validateProviderCapabilities(capabilities: Set<ProviderCapability>) {
    check((priceCapabilities intersect capabilities).size == 1)
}