package cz.fb.manaus.ischia.filter

import cz.fb.manaus.reactor.price.AbstractPriceFilter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class AbnormalPriceFilter(@Qualifier("priceBulldoze") priceBulldoze: Double) : AbstractPriceFilter(3, priceBulldoze, 1.03..100.0)
