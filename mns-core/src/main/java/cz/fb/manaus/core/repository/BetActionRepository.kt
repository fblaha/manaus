package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.BetAction

interface BetActionRepository : Repository<BetAction> {
    fun deleteByMarket(marketId: String): Int
    fun find(marketId: String): List<BetAction>
    fun setBetId(actionId: String, betId: String): Boolean
    fun findRecentBetAction(betId: String): BetAction?
    fun findRecentBetActions(limit: Int): List<BetAction>
}