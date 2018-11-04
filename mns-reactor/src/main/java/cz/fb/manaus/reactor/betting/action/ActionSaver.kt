package cz.fb.manaus.reactor.betting.action

import cz.fb.manaus.core.dao.BetActionDao
import cz.fb.manaus.core.dao.MarketPricesDao
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallbackWithoutResult
import org.springframework.transaction.support.TransactionTemplate
import java.time.Instant
import java.util.Objects.requireNonNull
import java.util.logging.Level
import java.util.logging.Logger


@Component
@Profile(ManausProfiles.DB)
class ActionSaver(
        private val betActionDao: BetActionDao,
        private val pricesDao: MarketPricesDao,
        private val transactionManager: PlatformTransactionManager) {

    fun setBetId(betId: String, actionId: Int): Int {
        replaceExistingBetId(betId)
        return betActionDao.setBetId(actionId, betId)
    }

    fun saveAction(action: BetAction) {
        val template = TransactionTemplate(transactionManager)
        template.execute(object : TransactionCallbackWithoutResult() {
            override fun doInTransactionWithoutResult(status: TransactionStatus) {
                val prices = action.marketPrices
                if (prices.id != null) {
                    pricesDao.saveOrUpdate(prices)
                    requireNonNull(prices.id)
                }
                betActionDao.saveOrUpdate(action)
            }
        })
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
