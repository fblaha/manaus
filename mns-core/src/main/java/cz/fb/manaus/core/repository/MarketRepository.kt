package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.Market
import java.time.Instant

interface MarketRepository : Repository<Market> {
    fun delete(olderThan: Instant): Int
    fun find(from: Instant? = null, to: Instant? = null, maxResults: Int? = null): List<Market>
    fun findIDs(from: Instant? = null, to: Instant? = null): List<String>
}