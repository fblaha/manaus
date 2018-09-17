package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.provider.ExchangeProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MinimalAmountAdviser : AmountAdviser {

    @Autowired
    private lateinit var provider: ExchangeProvider

    override fun getAmount(): Double {
        return provider.minAmount
    }
}
