package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class PlacedAheadFunction : AheadTimeFunction {

    override fun getRelatedTime(bet: RealizedBet): Instant? {
        return bet.settledBet.placed
    }

}
