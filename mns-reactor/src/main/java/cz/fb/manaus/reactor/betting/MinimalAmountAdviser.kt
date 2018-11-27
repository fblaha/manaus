package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.provider.ExchangeProvider
import org.springframework.stereotype.Component

@Component
class MinimalAmountAdviser(private val provider: ExchangeProvider) : AmountAdviser {

    override val amount: Double
        get() = provider.minAmount
}
