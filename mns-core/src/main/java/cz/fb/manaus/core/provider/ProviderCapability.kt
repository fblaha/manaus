package cz.fb.manaus.core.provider

import cz.fb.manaus.core.provider.ProviderCapability.*

enum class ProviderCapability {
    PriceShiftFixedStep,
    PriceShiftContinuous,
    LastMatchedPrice,
    TradedVolume,
    MatchedAmount,
    CommissionNetWin,
    CommissionSingleBet,

}


val priceShiftCapabilities = setOf(PriceShiftContinuous, PriceShiftFixedStep)
val commissionCapabilities = setOf(CommissionNetWin, CommissionSingleBet)

fun validateProviderCapabilities(capabilities: Set<ProviderCapability>) {
    check((priceShiftCapabilities intersect capabilities).size == 1)
    check((commissionCapabilities intersect capabilities).size == 1)
}

typealias ProviderMatcher = (ProviderSelector) -> Boolean

