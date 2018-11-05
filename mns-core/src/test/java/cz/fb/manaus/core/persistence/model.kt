package cz.fb.manaus.core.persistence

import org.dizitart.no2.objects.Id
import java.util.*

data class Event(
        val id: String,
        val name: String,
        val countryCode: String,
        val timezone: String,
        val venue: String,
        val openDate: Date
)

data class Competition(
        val id: String,
        val name: String
)

data class EventType(
        val id: String,
        val name: String
)

data class Runner(
        val selectionId: Long,
        val name: String,
        val handicap: Double,
        val sortPriority: Int
)

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
)
