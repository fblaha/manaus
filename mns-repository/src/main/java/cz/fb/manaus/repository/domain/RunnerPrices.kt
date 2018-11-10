package cz.fb.manaus.repository.domain

data class RunnerPrices(
        val selectionId: Long,
        val prices: List<Price>,
        val lastMatchedPrice: Double?,
        val matchedAmount: Double?
)