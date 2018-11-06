package cz.fb.manaus.repository

import cz.fb.manaus.core.persistence.Market
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.filters.lt
import org.dizitart.kno2.getRepository
import org.dizitart.no2.Nitrite
import org.dizitart.no2.objects.ObjectRepository
import java.time.Instant


class MarketRepository(private val db: Nitrite) {

    private val repository: ObjectRepository<Market>  by lazy { db.getRepository<Market> {} }

    fun saveOrUpdate(market: Market) {
        repository.insert(market)
    }

    fun read(id: String): Market? {
        for (market in repository.find(Market::id eq id)) {
            return market
        }
        return null
    }

    fun deleteMarkets(olderThan: Instant): Int {
        val result = repository.remove(Market::openDate lt olderThan)
        return result.affectedCount
    }
}