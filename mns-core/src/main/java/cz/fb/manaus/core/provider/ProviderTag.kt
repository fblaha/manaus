package cz.fb.manaus.core.provider

import cz.fb.manaus.core.provider.ProviderTag.*

enum class ProviderTag {

    PriceShiftFixedStep,
    PriceShiftContinuous,

    ProviderMatchbook,
    ProviderBetfair,

    LastMatchedPrice,
    TradedVolume,
    MatchedAmount,
}


val priceShiftTags = setOf(PriceShiftContinuous, PriceShiftFixedStep)
val providerTags = setOf(ProviderBetfair, ProviderMatchbook)

fun validateTags(tags: Set<ProviderTag>) {
    check((priceShiftTags intersect tags).size == 1)
    check((providerTags intersect tags).size <= 1)
}

typealias ProviderMatcher = (ProviderSelector) -> Boolean

