package cz.fb.manaus.repository

import cz.fb.manaus.core.persistence.BetAction
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.no2.FindOptions
import org.dizitart.no2.Nitrite
import org.dizitart.no2.SortOrder
import org.dizitart.no2.objects.ObjectRepository


class BetActionRepository(private val db: Nitrite) {

    private val repository: ObjectRepository<BetAction> by lazy { db.getRepository<BetAction> {} }

    fun save(betAction: BetAction) {
        repository.insert(betAction)
    }

    fun delete(marketID: String): Int {
        return repository.remove(BetAction::marketID eq marketID).affectedCount
    }

    fun find(marketID: String): List<BetAction> {
        var options = FindOptions.sort("time", SortOrder.Ascending)
        return repository.find(BetAction::marketID eq marketID, options).toList()
    }
}