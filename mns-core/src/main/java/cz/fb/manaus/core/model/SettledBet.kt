package cz.fb.manaus.core.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant

@JsonIgnoreProperties("side\$manaus_core")
data class SettledBet(
        var id: String,
        val selectionId: Long,
        val selectionName: String,
        val profitAndLoss: Double,
        val commission: Double?,
        @Field(type = FieldType.Date, format = DateFormat.date_time)
        val placed: Instant?,
        @Field(type = FieldType.Date, format = DateFormat.date_time)
        val matched: Instant,
        @Field(type = FieldType.Date, format = DateFormat.date_time)
        val settled: Instant,
        val price: Price
) {
    internal val side: Side = price.side
}
