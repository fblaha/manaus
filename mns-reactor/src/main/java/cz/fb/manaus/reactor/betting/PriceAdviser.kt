package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.reactor.betting.proposer.ProposedPrice

interface PriceAdviser {

    fun getNewPrice(betContext: BetContext): ProposedPrice<Price>?

}
