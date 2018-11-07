package cz.fb.manaus.repository

import cz.fb.manaus.core.persistence.MarketPrices
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.no2.FindOptions
import org.dizitart.no2.Nitrite
import org.dizitart.no2.SortOrder
import org.dizitart.no2.objects.ObjectRepository


class MarketPricesRepository(private val db: Nitrite) {

    private val repository: ObjectRepository<MarketPrices> by lazy { db.getRepository<MarketPrices> {} }

    fun save(marketPrices: MarketPrices) {
        repository.insert(marketPrices)
    }

    fun delete(marketID: String): Int {
        return repository.remove(MarketPrices::marketID eq marketID).affectedCount
    }

    fun find(marketID: String): List<MarketPrices> {
        var options = FindOptions.sort("time", SortOrder.Ascending)
        return repository.find(MarketPrices::marketID eq marketID, options).toList()
    }
}