package cz.fb.manaus.repository.domain

import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import java.time.Instant

@Index(value = "event.openDate", type = IndexType.NonUnique)
data class Market(
        @Id val id: String,
        val name: String,
        val matchedAmount: Double,
        val isInPlay: Boolean,
        val type: String,
        val eventType: EventType,
        val competition: Competition,
        val event: Event,
        val runners: List<Runner>
) {
    internal val openDate: Instant = event.openDate
}