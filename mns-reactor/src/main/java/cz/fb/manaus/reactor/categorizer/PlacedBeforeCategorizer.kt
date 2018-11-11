package cz.fb.manaus.reactor.categorizer


import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class PlacedBeforeCategorizer : AbstractBeforeCategorizer(CATEGORY) {

    override fun getDate(realizedBet: RealizedBet): Instant? {
        return realizedBet.settledBet.placed
    }

    companion object {
        const val CATEGORY = "placedBefore"
    }
}
