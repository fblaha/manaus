package cz.fb.manaus.core.provider

import cz.fb.manaus.core.provider.ProviderTag.VendorBetfair
import cz.fb.manaus.core.provider.ProviderTag.VendorMatchbook

enum class ProviderTag {

    VendorMatchbook,
    VendorBetfair,

    LastMatchedPrice,
    TradedVolume,
    MatchedAmount,

}


val vendorTags = setOf(VendorBetfair, VendorMatchbook)

fun validateTags(tags: Set<ProviderTag>) {
    check((vendorTags intersect tags).size <= 1)
}


