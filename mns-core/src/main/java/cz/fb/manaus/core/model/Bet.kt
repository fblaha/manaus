package cz.fb.manaus.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

data class Bet(val betId: String?,
               val marketId: String,
               val selectionId: Long,
               val requestedPrice: Price,
               val placedDate: Date?,
               val matchedAmount: Double,
               var actionId: Int = 0) {

    val isMatched: Boolean
        @JsonIgnore
        get() = !Price.amountEq(matchedAmount, 0.0)

    val isHalfMatched: Boolean
        @JsonIgnore
        get() = matchedAmount > requestedPrice.amount / 2


    fun replacePrice(newPrice: Double): Bet {
        return copy(requestedPrice = Price(newPrice, requestedPrice.amount, requestedPrice.side))
    }
}
