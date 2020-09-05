package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import java.time.Instant

interface SettledBetRepository : Repository<SettledBet> {
    fun update(settledBet: SettledBet)
    fun find(
            from: Instant? = null,
            to: Instant? = null,
            side: Side? = null,
            maxResults: Int? = null,
            asc: Boolean = true
    ): List<SettledBet>
}