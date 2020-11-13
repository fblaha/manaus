package cz.fb.manaus.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

data class Bet(
        val betId: String? = null,
        // TODO remove
        val marketId: String,
        val selectionId: Long,
        val requestedPrice: Price,
        val placedDate: Instant,
        val matchedAmount: Double = 0.0,
        val action: BetAction? = null,
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
