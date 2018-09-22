package cz.fb.manaus.reactor.rounding

import cz.fb.manaus.core.model.Price
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component


@Component
@Profile("matchbook")
class MatchbookRoundingPlugin : RoundingPlugin {

    override fun shift(price: Double, stepNum: Int): Double {
        return price + stepNum * getStep(price)
    }

    private fun getStep(price: Double): Double {
        return (price - 1) * 0.02
    }

    override fun round(price: Double): Double {
        return Price.round(price)
    }

}
