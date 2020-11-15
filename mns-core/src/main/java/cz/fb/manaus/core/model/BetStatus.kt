package cz.fb.manaus.core.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class BetStatus(
        val betId: String?,
        val selectionId: Long,
        val requestedPrice: Price,
        val matchedAmount: Double = 0.0,
)


val Bet.status: BetStatus
    @JsonIgnore
    get() = BetStatus(
            betId = betId,
            selectionId = selectionId,
            requestedPrice = requestedPrice,
            matchedAmount = matchedAmount
    )
