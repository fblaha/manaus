package cz.fb.manaus.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant

data class Bet(
        val betId: String? = null,
        val marketId: String,
        val selectionId: Long,
        val requestedPrice: Price,
        @Field(type = FieldType.Date, format = DateFormat.date_time)
        val placedDate: Instant,
        val matchedAmount: Double = 0.0,
        val actionId: String? = null,
) {

    val isMatched: Boolean
        @JsonIgnore
        get() = !(matchedAmount amountEq 0.0)

    val isHalfMatched: Boolean
        @JsonIgnore
        get() = matchedAmount > requestedPrice.amount / 2


    infix fun replacePrice(newPrice: Double): Bet {
        return copy(requestedPrice = Price(newPrice, requestedPrice.amount, requestedPrice.side))
    }
}
