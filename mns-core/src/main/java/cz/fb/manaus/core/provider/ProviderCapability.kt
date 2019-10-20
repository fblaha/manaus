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


val priceShiftCapabilities = setOf(PriceShiftContinuous, PriceShiftFixedStep)

fun validateProviderCapabilities(capabilities: Set<ProviderCapability>) {
    check((priceShiftCapabilities intersect capabilities).size == 1)
}

typealias CapabilityPredicate = (RequiredCapabilitiesAware) -> Boolean

fun predicate(capabilities: Set<ProviderCapability>): CapabilityPredicate {
    return { capabilities.containsAll(it.requiredCapabilities) }
}