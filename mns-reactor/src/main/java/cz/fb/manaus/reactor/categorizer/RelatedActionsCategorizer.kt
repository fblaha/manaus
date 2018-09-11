package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.dao.BetActionDao
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.action.BetUtils
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*
import java.util.Optional.of
import java.util.logging.Level
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class RelatedActionsCategorizer : SettledBetCategorizer {


    @Autowired
    private lateinit var betActionDao: BetActionDao
    @Autowired
    private lateinit var betUtils: BetUtils
    @Autowired
    private lateinit var relatedActionsAwareCategorizers: List<RelatedActionsAwareCategorizer>

    override fun isSimulationSupported(): Boolean {
        return false
    }

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val market = settledBet.betAction.market
        val betActions = betActionDao.getBetActions(market.id,
                OptionalLong.of(settledBet.selectionId), of<Side>(settledBet.price.side))
        if (betActions.isEmpty()) {
            log.log(Level.WARNING, "missing  bet actions ''{0}''", settledBet)
            return emptySet()
        }
        val current = betUtils.getCurrentActions(betActions)
        val result = HashSet<String>()
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





