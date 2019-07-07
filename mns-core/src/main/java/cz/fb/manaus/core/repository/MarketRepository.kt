package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.spring.ManausProfiles
import org.dizitart.kno2.filters.and
import org.dizitart.kno2.filters.gte
import org.dizitart.kno2.filters.lt
import org.dizitart.kno2.filters.lte
import org.dizitart.kno2.getRepository
import org.dizitart.no2.FindOptions
import org.dizitart.no2.Nitrite
import org.dizitart.no2.SortOrder
import org.dizitart.no2.objects.Cursor
import org.dizitart.no2.objects.filters.ObjectFilters
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant


@Component
@Profile(ManausProfiles.DB)
class MarketRepository(private val db: Nitrite) :
        AbstractRepository<Market, String>({ db.getRepository {} }, Market::id) {

    fun delete(olderThan: Instant): Int {
        return repository.remove(Market::openDate lt olderThan).affectedCount
    }

    fun find(from: Instant? = null, to: Instant? = null, maxResults: Int? = null): List<Market> {
        return findLazy(from, to, maxResults).toList()
    }

    fun findIDs(from: Instant? = null, to: Instant? = null): List<String> {
        return findLazy(from, to, null).map { it.id }.toList()
    }

    private fun findLazy(from: Instant?, to: Instant?, maxResults: Int?): Cursor<Market> {
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
        return repository.find(filter, options)
    }

}