package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class PlacedAheadFunction : AheadTimeFunction {

    override fun getRelatedTime(bet: SettledBet): Optional<Instant> {
        return Optional.ofNullable(bet.placedOrActionDate.toInstant())
    }

}
