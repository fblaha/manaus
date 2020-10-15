package cz.fb.manaus.core.model

import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant

data class Event(
        val id: String,
        val name: String,
        val countryCode: String?,
        val timezone: String?,
        val venue: String?,
        @Field(type = FieldType.Date, format = DateFormat.date_time)
        val openDate: Instant
)