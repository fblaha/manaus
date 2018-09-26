package cz.fb.manaus.ischia.filter

import com.google.common.collect.Range
import cz.fb.manaus.reactor.price.AbstractPriceFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class AbnormalPriceFilter @Autowired
constructor(@Qualifier("priceBulldoze") priceBulldoze: Double) : AbstractPriceFilter(3, priceBulldoze, Range.closed(1.03, 100.0))
