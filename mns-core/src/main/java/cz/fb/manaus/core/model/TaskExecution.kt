package cz.fb.manaus.core.model

import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import java.time.Instant

@Index(value = "name", type = IndexType.Unique)
data class TaskExecution(
        @Id val name: String,
        val time: Instant
)