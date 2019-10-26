package cz.fb.manaus.core.provider

import cz.fb.manaus.core.provider.ProviderTag.*

enum class ProviderTag {

    PriceShiftFixedStep,
    PriceShiftContinuous,

    VendorMatchbook,
    VendorBetfair,

    LastMatchedPrice,
    TradedVolume,
    MatchedAmount,
}


val priceShiftTags = setOf(PriceShiftContinuous, PriceShiftFixedStep)
val vendorTags = setOf(VendorBetfair, VendorMatchbook)

fun validateTags(tags: Set<ProviderTag>) {
    check((priceShiftTags intersect tags).size == 1)
    check((vendorTags intersect tags).size <= 1)
}

typealias ProviderMatcher = (ProviderSelector) -> Boolean

