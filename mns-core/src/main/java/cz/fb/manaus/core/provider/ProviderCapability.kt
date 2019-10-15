package cz.fb.manaus.core.provider

enum class ProviderCapability {
    FixedStepPrice,
    LastMatchedPrice,
    TradedVolume,
    MatchedAmount
}

val allCapabilities = ProviderCapability.values().toSet()
