package cz.fb.manaus.core.provider

import cz.fb.manaus.core.provider.ProviderCapability.PriceShiftContinuous
import cz.fb.manaus.core.provider.ProviderCapability.PriceShiftFixedStep

enum class ProviderCapability {
    PriceShiftFixedStep,
    PriceShiftContinuous,
    LastMatchedPrice,
    TradedVolume,
    MatchedAmount
}


val priceCapabilities = setOf(PriceShiftContinuous, PriceShiftFixedStep)

fun validateProviderCapabilities(capabilities: Set<ProviderCapability>) {
    check((priceCapabilities intersect capabilities).size == 1)
}