package cz.fb.manaus.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant


data class TrackedBet(
        val remote: Bet,
        val local: BetAction,
) {

    infix fun replacePrice(newPrice: Double): TrackedBet {
        val replaced = remote.requestedPrice.copy(price = newPrice)
        return copy(remote = remote.copy(requestedPrice = replaced))
    }

}

data class Bet(
        val betId: String? = null,
        val marketId: String,
        val selectionId: Long,
        val requestedPrice: Price,
        val placedDate: Instant,
        val matchedAmount: Double = 0.0,
) {

    val isMatched: Boolean
        @JsonIgnore
        get() = !(matchedAmount amountEq 0.0)

    val isHalfMatched: Boolean
        @JsonIgnore
        get() = matchedAmount > requestedPrice.amount / 2

}
