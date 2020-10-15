package cz.fb.manaus.core.model


import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant

data class TaskExecution(
        val name: String,
        @Field(type = FieldType.Date, format = DateFormat.date_time)
        val time: Instant
)