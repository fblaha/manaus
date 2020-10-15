package cz.fb.manaus.core.model

import java.time.Duration

data class BlacklistedCategory(
        val name: String,
        val period: Duration,
        val profit: Double
)