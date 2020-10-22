package cz.fb.manaus.core.model


import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class TaskExecution(
        @Id
        val name: String,
        val time: Instant
)