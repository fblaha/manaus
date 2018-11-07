package cz.fb.manaus.repository

import cz.fb.manaus.core.persistence.Market
import org.dizitart.kno2.filters.*
import org.dizitart.kno2.getRepository
import org.dizitart.no2.FindOptions
import org.dizitart.no2.Nitrite
import org.dizitart.no2.SortOrder
import org.dizitart.no2.objects.ObjectRepository
import org.dizitart.no2.objects.filters.ObjectFilters
import java.time.Instant


class MarketRepository(private val db: Nitrite) {

    private val repository: ObjectRepository<Market> by lazy { db.getRepository<Market> {} }

    fun save(market: Market) {
        repository.insert(market)
    }

    fun read(id: String): Market? {
        return repository.find(Market::id eq id).firstOrDefault()
    }

    fun deleteMarkets(olderThan: Instant): Int {
        return repository.remove(Market::openDate lt olderThan).affectedCount
    }

    fun delete(id: String) {
        repository.remove(Market::id eq id)
    }

    fun getMarkets(from: Instant?, to: Instant?, maxResults: Int?): List<Market> {
        var filter = ObjectFilters.ALL
        if (from != null) {
            val fromFilter = Market::openDate gte from
            filter = filter?.and(fromFilter) ?: fromFilter
        }
        if (to != null) {
            val toFilter = Market::openDate lte to
            filter = filter?.and(toFilter) ?: toFilter
        }
        var options = FindOptions.sort("openDate", SortOrder.Ascending)
        if (maxResults != null) options = options.thenLimit(0, maxResults)
        return repository.find(filter, options).toList()
    }

}