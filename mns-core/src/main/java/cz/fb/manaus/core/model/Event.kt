package cz.fb.manaus.core.model

import org.springframework.data.mongodb.core.index.Indexed
import java.time.Instant

data class Event(
        val id: String,
        val name: String,
        val countryCode: String?,
        val timezone: String?,
        val venue: String?,
        @Indexed
        val openDate: Instant
)