package cz.fb.manaus.core.model

import org.springframework.data.annotation.Id
import java.time.Instant

data class MarketStatus(
        @Id
        val id: String,
        val openDate: Instant,
        val lastEvent: Instant,
        val bets: List<Bet>
)