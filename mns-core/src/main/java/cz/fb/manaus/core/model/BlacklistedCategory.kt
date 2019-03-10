package cz.fb.manaus.core.model

import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import java.time.Duration

@Index(value = "name", type = IndexType.Unique)
data class BlacklistedCategory(
        @Id val name: String,
        val period: Duration,
        val profit: Double
)