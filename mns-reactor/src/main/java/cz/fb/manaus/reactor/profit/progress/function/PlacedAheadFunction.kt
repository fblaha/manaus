package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class PlacedAheadFunction : AheadTimeFunction {

    override fun getRelatedTime(bet: SettledBet): Instant? {
        return bet.placedOrActionDate.toInstant()
    }

}
