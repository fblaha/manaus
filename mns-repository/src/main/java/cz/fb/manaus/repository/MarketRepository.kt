package cz.fb.manaus.repository

import cz.fb.manaus.core.persistence.Market
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.filters.lt
import org.dizitart.kno2.getRepository
import org.dizitart.no2.Nitrite
import org.dizitart.no2.objects.ObjectRepository
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
}