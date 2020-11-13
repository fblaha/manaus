package cz.fb.manaus.core.model

data class BetStatus(
        val betId: String?,
        val selectionId: Long,
        val requestedPrice: Price,
        val matchedAmount: Double = 0.0,
)