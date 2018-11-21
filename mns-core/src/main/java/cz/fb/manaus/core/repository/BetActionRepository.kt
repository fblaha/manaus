package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.spring.ManausProfiles
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.no2.FindOptions
import org.dizitart.no2.Nitrite
import org.dizitart.no2.NitriteId
import org.dizitart.no2.SortOrder
import org.dizitart.no2.objects.ObjectRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component


@Component
@Profile(ManausProfiles.DB)
class BetActionRepository(private val db: Nitrite) {

    internal val repository: ObjectRepository<BetAction> by lazy { db.getRepository<BetAction> {} }

    fun save(betAction: BetAction): Long {
        val action = if (betAction.id == 0L) betAction.copy(id = NitriteId.newId().idValue) else betAction
        repository.insert(action)
        return action.id
    }

    fun delete(marketId: String): Int {
        return repository.remove(BetAction::marketId eq marketId).affectedCount
    }

    fun find(marketId: String): List<BetAction> {
        val options = FindOptions.sort("time", SortOrder.Ascending)
        return repository.find(BetAction::marketId eq marketId, options).toList()
    }

    fun setBetId(actionId: Long, betId: String): Int {
        val action = repository.find(BetAction::id eq actionId).first()
        return repository.update(action.copy(betId = betId)).affectedCount
    }

    fun findRecentBetAction(betId: String): BetAction? {
        val options = FindOptions.sort("time", SortOrder.Descending).thenLimit(0, 1)
        return repository.find(BetAction::betId eq betId, options).firstOrDefault()
    }

    fun findRecentBetActions(limit: Int): List<BetAction> {
        val options = FindOptions.sort("time", SortOrder.Descending).thenLimit(0, limit)
        return repository.find(options).toList()
    }
}