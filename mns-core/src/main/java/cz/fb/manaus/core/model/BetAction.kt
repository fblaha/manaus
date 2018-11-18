package cz.fb.manaus.core.model

import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import java.time.Instant

@Indices(
        Index(value = "marketId", type = IndexType.NonUnique),
        Index(value = "betId", type = IndexType.NonUnique)
)
data class BetAction(
        @Id val id: Long,
        val betActionType: BetActionType,
        val time: Instant,
        val price: Price,
        val marketId: String,
        val selectionId: Long,
        val betId: String? = null,
        val runnerPrices: List<RunnerPrices>,
        val properties: Map<String, String>
) {
    companion object {
        const val TRADED_VOL_MEAN = "tradedVolumeMean"
        const val PROPOSER_PROP = "proposer"
    }
}