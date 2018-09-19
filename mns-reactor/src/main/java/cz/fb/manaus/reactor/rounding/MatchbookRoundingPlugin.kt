package cz.fb.manaus.reactor.rounding

import cz.fb.manaus.core.model.Price
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*


@Component
@Profile("matchbook")
class MatchbookRoundingPlugin : RoundingPlugin {

    override fun shift(price: Double, stepNum: Int): OptionalDouble {
        return OptionalDouble.of(price + stepNum * getStep(price))
    }

    private fun getStep(price: Double): Double {
        return (price - 1) * 0.02
    }

    override fun round(price: Double): OptionalDouble {
        return OptionalDouble.of(Price.round(price))
    }

}
