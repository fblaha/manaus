package cz.fb.manaus.manila.filter

import com.google.common.collect.Range
import cz.fb.manaus.reactor.price.AbstractPriceFilter
import org.springframework.stereotype.Component

@Component
class AbnormalPriceFilter : AbstractPriceFilter(3, 100.0, Range.all())
