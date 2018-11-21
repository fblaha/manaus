package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.reactor.betting.action.BetUtils
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.logging.Level
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class RelatedActionsCategorizer(
        private val betActionRepository: BetActionRepository,
        private val relatedActionsAwareCategorizers: List<RelatedActionsAwareCategorizer>) : RealizedBetCategorizer {


    override val isSimulationSupported: Boolean = false

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val market = realizedBet.market
        val side = realizedBet.settledBet.price.side
        val selectionId = realizedBet.settledBet.selectionId
        // TODO filter in DB
        val betActions = betActionRepository.find(market.id)
                .filter { it.selectionId == selectionId && side == it.price.side }
        if (betActions.isEmpty()) {
            log.log(Level.WARNING, "missing  bet actions ''{0}''", realizedBet)
            return emptySet()
        }
        val current = BetUtils.getCurrentActions(betActions)
        val result = mutableSetOf<String>()
        for (categorizer in relatedActionsAwareCategorizers) {
            val partial = categorizer.getCategories(current, market)
            result.addAll(partial)
        }
        return result
    }

    companion object {
        private val log = Logger.getLogger(RelatedActionsCategorizer::class.java.simpleName)
    }
}





