package cz.fb.manaus.core.batch

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.core.time.IntervalParser
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.logging.Logger
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Component
@ExperimentalTime
@Profile(ManausProfiles.DB)
class BetLoader(
        private val settledBetRepository: SettledBetRepository,
        private val realizedBetLoader: RealizedBetLoader
) {

    private val log = Logger.getLogger(BetLoader::class.simpleName)

    fun load(interval: String): List<RealizedBet> {
        val (from, to) = IntervalParser.parse(Instant.now(), interval)
        val (settledBets, sDur) = measureTimedValue { settledBetRepository.find(from = from, to = to) }
        log.info { "settle bets '$interval' loaded  in '$sDur'" }
        val (bets, rDur) = measureTimedValue { settledBets.map { realizedBetLoader.toRealizedBet(it) } }
        log.info { "realized bets '$interval' loaded in '${rDur}'" }
        return bets
    }

}
