package cz.fb.manaus.core.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import java.time.Instant

data class SettledBet(
        @Id
        var id: String,
        val selectionId: Long,
        val selectionName: String,
        val profitAndLoss: Double,
        val commission: Double?,
        val placed: Instant?,
        val matched: Instant,
        @Indexed
        val settled: Instant,
        val price: Price
)
