package cz.fb.manaus.core.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class MarketStatus(
        @Id
        val id: String,
        val eventDate: Instant,
        val eventName: String,
        val lastEvent: Instant,
        val bets: List<Bet>
) {

    val matchedAmount: Double
        get() {
            return bets.sumByDouble { it.matchedAmount }
        }

}