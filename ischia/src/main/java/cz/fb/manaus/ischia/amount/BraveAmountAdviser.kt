package cz.fb.manaus.ischia.amount

import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.betting.MinimalAmountAdviser
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class BraveAmountAdviser(private val minimalAmountAdviser: MinimalAmountAdviser) : AmountAdviser {

    override val amount: Double
        get() = minimalAmountAdviser.amount
}
