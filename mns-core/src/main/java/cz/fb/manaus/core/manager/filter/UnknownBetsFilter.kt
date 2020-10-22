package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.MarketSnapshotEvent
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class UnknownBetsFilter(
        private val betActionRepository: BetActionRepository
) : MarketSnapshotEventFilter {

    private val log = Logger.getLogger(UnknownBetsFilter::class.simpleName)

    override fun accept(event: MarketSnapshotEvent): Boolean {
        val myBets = betActionRepository.find(event.snapshot.market.id).mapNotNull { it.betId }.toSet()
        return getUnknownBets(event.snapshot.currentBets, myBets)
                .onEach { log.warning { "unknown bet '${it.betId}'" } }
                .isEmpty()
    }
}

fun getUnknownBets(bets: List<Bet>, myBets: Set<String>): List<Bet> {
    return bets.filter { it.betId !in myBets }
}
