package cz.fb.manaus.ischia.amount

import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.reactor.betting.AmountAdviser
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class BraveAmountAdviser(private val provider: ExchangeProvider) : AmountAdviser {

    override val amount: Double
        // TODO to config
        get() = provider.minAmount + 1.0

}
