package cz.fb.manaus.repository.domain

import java.time.Instant

data class Event(
        val id: String,
        val name: String,
        val countryCode: String,
        val timezone: String,
        val venue: String,
        val openDate: Instant
)