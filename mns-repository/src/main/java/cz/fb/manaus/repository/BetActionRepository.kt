package cz.fb.manaus.repository

import cz.fb.manaus.core.persistence.BetAction
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.no2.FindOptions
import org.dizitart.no2.Nitrite
import org.dizitart.no2.NitriteId
import org.dizitart.no2.SortOrder
import org.dizitart.no2.objects.ObjectRepository


class BetActionRepository(private val db: Nitrite) {

    private val repository: ObjectRepository<BetAction> by lazy { db.getRepository<BetAction> {} }

    fun save(betAction: BetAction): Long {
        val action = if (betAction.id == 0L) betAction.copy(id = NitriteId.newId().idValue) else betAction
        repository.insert(action)
        return action.id
    }

    fun delete(marketID: String): Int {
        return repository.remove(BetAction::marketID eq marketID).affectedCount
    }

    fun find(marketID: String): List<BetAction> {
        var options = FindOptions.sort("time", SortOrder.Ascending)
        return repository.find(BetAction::marketID eq marketID, options).toList()
    }

    fun setBetID(actionID: Long, betID: String): Int {
        val action = repository.find(BetAction::id eq actionID).first()
        return repository.update(action.copy(betID = betID)).affectedCount
    }
}