package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class MatchedAheadFunction : AheadTimeFunction {

    override fun getRelatedTime(bet: SettledBet): Optional<Instant> {
        val matched = bet.matched
        return if (matched == null) {
            Optional.empty()
        } else {
            Optional.ofNullable(matched.toInstant())
        }
    }

}
