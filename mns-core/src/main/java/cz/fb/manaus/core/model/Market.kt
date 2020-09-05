package cz.fb.manaus.core.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import java.time.Instant

const val TYPE_MONEY_LINE = "moneyline"
const val TYPE_MATCH_ODDS = "match_odds"
const val TYPE_TOTAL = "total"
const val TYPE_HANDICAP = "handicap"

@Index(value = "event.openDate", type = IndexType.NonUnique)
@JsonIgnoreProperties("openDate\$manaus_core")
data class Market(
        @Id val id: String,
        val name: String,
        val matchedAmount: Double,
        val inPlay: Boolean,
        val type: String?,
        val eventType: EventType,
        val competition: Competition?,
        val event: Event,
        val latestEvent: Instant?,
        val runners: List<Runner>
) {
    internal val openDate: Instant = event.openDate

    fun getRunner(selectionId: Long): Runner {
        return runners.first { it.selectionId == selectionId }
    }

}