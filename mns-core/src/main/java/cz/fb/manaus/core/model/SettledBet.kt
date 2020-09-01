package cz.fb.manaus.core.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import java.time.Instant

@Indices(
    Index(value = "id", type = IndexType.Unique),
    Index(value = "settled", type = IndexType.NonUnique)
)
@JsonIgnoreProperties("side\$manaus_core")
data class SettledBet(
    @Id var id: String,
    val selectionId: Long,
    val selectionName: String,
    val profitAndLoss: Double,
    val commission: Double?,
    val placed: Instant?,
    val matched: Instant,
    val settled: Instant,
    val price: Price
) {
    internal val side: Side = price.side
}
