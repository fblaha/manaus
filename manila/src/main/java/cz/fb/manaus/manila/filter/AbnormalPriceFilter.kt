package cz.fb.manaus.manila.filter

import com.google.common.collect.Range
import cz.fb.manaus.reactor.price.AbstractPriceFilter
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class AbnormalPriceFilter : AbstractPriceFilter(3, 100.0, Range.all())
