package cz.fb.manaus.ischia.amount

import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.betting.MinimalAmountAdviser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class BraveAmountAdviser : AmountAdviser {

    @Autowired
    private lateinit var minimalAmountAdviser: MinimalAmountAdviser

    override fun getAmount(): Double {
        return minimalAmountAdviser.amount + 1.0
    }
}
