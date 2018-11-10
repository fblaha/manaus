package cz.fb.manaus.repository.domain

import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import java.time.Instant

@Indices(
        Index(value = "marketID", type = IndexType.NonUnique),
        Index(value = "betID", type = IndexType.NonUnique)
)
data class BetAction(
        @Id val id: Long,
        val betActionType: BetActionType,
        val time: Instant,
        val price: Price,
        val marketID: String,
        val selectionID: Long,
        val betID: String?,
        val runnerPrices: List<RunnerPrices>,
        val properties: Map<String, String>
)