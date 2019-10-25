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


val priceShiftTags = setOf(PriceShiftContinuous, PriceShiftFixedStep)
val commissionTags = setOf(CommissionNetWin, CommissionSingleBet)
val providerTags = setOf(CommissionNetWin, CommissionSingleBet)

fun validateTags(tags: Set<ProviderTag>) {
    check((priceShiftTags intersect tags).size == 1)
    check((commissionTags intersect tags).size == 1)
    check((providerTags intersect tags).size <= 1)
}

typealias ProviderMatcher = (ProviderSelector) -> Boolean

