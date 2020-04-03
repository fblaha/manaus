package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.getCurrentActions
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class ActionHistoryCategorizerCollector(
        private val betActionRepository: BetActionRepository,
        private val categorizers: List<ActionHistoryCategorizer>) : RealizedBetCategorizer {

    private val log = Logger.getLogger(ActionHistoryCategorizerCollector::class.simpleName)

    override val isSimulationSupported: Boolean = false

    override fun getCategories(realizedBet: RealizedBet): Set<String> {
        val market = realizedBet.market
        val side = realizedBet.settledBet.price.side
        val selectionId = realizedBet.settledBet.selectionId
        val betActions = betActionRepository.find(market.id)
                .filter { it.selectionId == selectionId && side == it.price.side }
        if (betActions.isEmpty()) {
            log.warning { "missing  bet actions '$realizedBet'" }
            return emptySet()
        }
        val current = getCurrentActions(betActions)
        return categorizers.flatMap { it.getCategories(current, market) }.toSet()
    }
}





