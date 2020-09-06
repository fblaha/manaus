package cz.fb.manaus.core.repository.no2

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.spring.ManausProfiles
import org.dizitart.kno2.filters.and
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.filters.gte
import org.dizitart.kno2.filters.lte
import org.dizitart.kno2.getRepository
import org.dizitart.no2.FindOptions
import org.dizitart.no2.Nitrite
import org.dizitart.no2.SortOrder
import org.dizitart.no2.objects.filters.ObjectFilters
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant

@Component
@Profile(ManausProfiles.DB)
class NitriteSettledBetRepository(db: Nitrite) :
    NitriteRepositoryAware<SettledBet> by NitriteRepository(db.getRepository {}, SettledBet::id),
    SettledBetRepository {

    override fun update(settledBet: SettledBet) {
        check(repository.update(SettledBet::id eq settledBet.id, settledBet).affectedCount == 1)
    }

    override fun find(
        from: Instant?,
        to: Instant?,
        side: Side?,
        maxResults: Int?,
        asc: Boolean
    ): List<SettledBet> {
        var filter = ObjectFilters.ALL
        if (from != null) {
            val fromFilter = SettledBet::settled gte from
            filter = filter?.and(fromFilter) ?: fromFilter
        }
        if (to != null) {
            val toFilter = SettledBet::settled lte to
            filter = filter?.and(toFilter) ?: toFilter
        }
        if (side != null) {
            val sideFilter = SettledBet::side eq side
            filter = filter?.and(sideFilter) ?: sideFilter
        }
        val sortOrder = if (asc) SortOrder.Ascending else SortOrder.Descending
        var options = FindOptions.sort("settled", sortOrder)
        if (maxResults != null) options = options.thenLimit(0, maxResults)
        return repository.find(filter, options).toList()
    }
}
