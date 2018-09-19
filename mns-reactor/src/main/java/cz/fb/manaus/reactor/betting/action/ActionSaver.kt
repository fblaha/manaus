package cz.fb.manaus.reactor.betting.action

import cz.fb.manaus.core.dao.BetActionDao
import cz.fb.manaus.core.dao.MarketPricesDao
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*
import java.util.Objects.requireNonNull
import java.util.logging.Level
import java.util.logging.Logger

@Repository
@Profile(ManausProfiles.DB)
class ActionSaver {
    @Autowired
    private lateinit var betActionDao: BetActionDao
    @Autowired
    private lateinit var pricesDao: MarketPricesDao

    fun setBetId(betId: String, actionId: Int): Int {
        replaceExistingBetId(betId)
        return betActionDao.setBetId(actionId, betId)
    }

    fun saveAction(action: BetAction) {
        val prices = action.marketPrices
        if (!Optional.ofNullable(prices.id).isPresent) {
            pricesDao.saveOrUpdate(prices)
            requireNonNull(prices.id)
        }
        betActionDao.saveOrUpdate(action)
    }

    private fun replaceExistingBetId(betId: String) {
        val time = Instant.now().toEpochMilli()
        val previousBetId = betId + "_" + java.lang.Long.toHexString(time)
        val updatedCount = betActionDao.updateBetId(betId, previousBetId)
        if (updatedCount > 0) {
            log.log(Level.INFO, "Previous action bet id set to ''{0}''", previousBetId)
        }
    }

    companion object {
        private val log = Logger.getLogger(ActionSaver::class.java.simpleName)
    }
}
