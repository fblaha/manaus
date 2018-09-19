package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.Price
import java.util.*

interface PriceAdviser {

    fun getNewPrice(betContext: BetContext): Optional<Price>

}
