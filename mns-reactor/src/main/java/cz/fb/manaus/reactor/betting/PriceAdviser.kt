package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.Price

interface PriceAdviser {

    fun getNewPrice(betContext: BetContext): Price?

}
