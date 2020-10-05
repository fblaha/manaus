package cz.fb.manaus.core.model

import org.dizitart.no2.objects.Id
import java.time.Instant

class MarketStatus(
        @Id
        val id: String,
        val openDate: Instant,
        val lastEvent: Instant,
        val bets: List<Bet>
)