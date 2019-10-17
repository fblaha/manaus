package cz.fb.manaus.core.provider

enum class ProviderCapability {
    PriceShiftFixedStep,
    PriceShiftContinuous,
    LastMatchedPrice,
    TradedVolume,
    MatchedAmount
}


val priceCapabilities = setOf(ProviderCapability.PriceShiftContinuous, ProviderCapability.PriceShiftFixedStep)

fun validateProviderCapabilities(capabilities: Set<ProviderCapability>) {
    check((priceCapabilities intersect capabilities).size == 1)
}