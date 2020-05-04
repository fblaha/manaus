package cz.fb.manaus.core.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import java.time.Instant

const val TYPE_MONEY_LINE = "moneyline"

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
        val runners: List<Runner>
) {
    internal val openDate: Instant = event.openDate

    fun getRunner(selectionId: Long): Runner {
        return runners.first { it.selectionId == selectionId }
    }

}