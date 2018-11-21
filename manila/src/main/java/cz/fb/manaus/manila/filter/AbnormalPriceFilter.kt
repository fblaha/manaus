package cz.fb.manaus.manila.filter

import cz.fb.manaus.reactor.price.AbstractPriceFilter
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class AbnormalPriceFilter : AbstractPriceFilter(3, 100.0, 0.0..100.0)
