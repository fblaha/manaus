package cz.fb.manaus.manila.filter

import cz.fb.manaus.reactor.price.PriceBulldozer
import cz.fb.manaus.reactor.price.PriceFilter
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class AbnormalPriceFilter(priceBulldozer: PriceBulldozer) :
        PriceFilter(3, 100.0, 0.0..100.0, priceBulldozer)
