package cz.fb.manaus.core.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Duration

@Document
data class BlacklistedCategory(
        @Id
        val name: String,
        val period: Duration,
        val profit: Double
)