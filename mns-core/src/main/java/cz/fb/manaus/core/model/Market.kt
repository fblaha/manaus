package cz.fb.manaus.core.model

const val TYPE_MONEY_LINE = "moneyline"
const val TYPE_MATCH_ODDS = "match_odds"
const val TYPE_TOTAL = "total"
const val TYPE_HANDICAP = "handicap"

data class Market(
        val id: String,
        val name: String,
        val matchedAmount: Double,
        val inPlay: Boolean,
        val type: String?,
        val eventType: EventType,
        val competition: Competition?,
        val event: Event,
        val runners: List<Runner>
) {

    fun getRunner(selectionId: Long): Runner {
        return runners.first { it.selectionId == selectionId }
    }

}