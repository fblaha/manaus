package cz.fb.manaus.core.model

import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant

data class MarketStatus(
        val id: String,
        @Field(type = FieldType.Date, format = DateFormat.date_time)
        val openDate: Instant,
        @Field(type = FieldType.Date, format = DateFormat.date_time)
        val lastEvent: Instant,
        val bets: List<Bet>
)