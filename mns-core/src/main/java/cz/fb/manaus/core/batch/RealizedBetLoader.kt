package cz.fb.manaus.core.batch

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(ManausProfiles.DB)
class RealizedBetLoader(
    private val betActionRepository: BetActionRepository,
    private val marketRepository: MarketRepository
) {

    fun toRealizedBet(settledBet: SettledBet): RealizedBet {
        val action = betActionRepository.findRecentBetAction(settledBet.id)!!
        return RealizedBet(
            settledBet = settledBet,
            betAction = action,
            market = marketRepository.read(action.marketId)!!
        )
    }
}