package cz.fb.manaus.core.persistence

import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import java.time.Instant

data class Event(
        val id: String,
        val name: String,
        val countryCode: String,
        val timezone: String,
        val venue: String,
        val openDate: Instant
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


data class Price(
        val price: Double,
        val amount: Double,
        val side: Side
)

data class RunnerPrices(
        val selectionId: Long,
        val prices: List<Price>,
        val lastMatchedPrice: Double?,
        val matchedAmount: Double?
)


enum class Side {
    BACK, LAY
}

enum class BetActionType {
    PLACE,
    UPDATE

}


@Indices(
        Index(value = "marketID", type = IndexType.NonUnique),
        Index(value = "betID", type = IndexType.NonUnique)
)
data class BetAction(
        @Id val id: Int,
        val betActionType: BetActionType,
        val time: Instant,
        val price: Price,
        val marketID: String,
        val selectionID: Long,
        val betID: String?,
        val runnerPrices: List<RunnerPrices>,
        val properties: Map<String, String>
)