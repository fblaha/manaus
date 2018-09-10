package cz.fb.manaus.reactor.categorizer


import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.util.*

@Component
class PlacedBeforeCategorizer : AbstractBeforeCategorizer(CATEGORY) {

    override fun getDate(settledBet: SettledBet): Date {
        return settledBet.placedOrActionDate
    }

    companion object {
        const val CATEGORY = "placedBefore"
    }

}
