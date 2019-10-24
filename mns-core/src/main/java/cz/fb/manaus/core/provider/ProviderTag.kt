package cz.fb.manaus.core.provider

import cz.fb.manaus.core.provider.ProviderTag.*

enum class ProviderTag {

    PriceShiftFixedStep,
    PriceShiftContinuous,

    CommissionNetWin,
    CommissionSingleBet,

    ProviderMatchbook,
    ProviderBetfair,

    LastMatchedPrice,
    TradedVolume,
    MatchedAmount,
}


val priceShiftCapabilities = setOf(PriceShiftContinuous, PriceShiftFixedStep)
val commissionCapabilities = setOf(CommissionNetWin, CommissionSingleBet)

fun validateProviderCapabilities(tags: Set<ProviderTag>) {
    check((priceShiftCapabilities intersect tags).size == 1)
    check((commissionCapabilities intersect tags).size == 1)
}

typealias ProviderMatcher = (ProviderSelector) -> Boolean

