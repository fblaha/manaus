package cz.fb.manaus.core.batch

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.core.time.IntervalParser
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Component
@ExperimentalTime
@Profile(ManausProfiles.DB)
class SettledBetLoader(
        private val settledBetRepository: SettledBetRepository,
        private val realizedBetLoader: RealizedBetLoader
) {

    private val log = Logger.getLogger(SettledBetLoader::class.simpleName)

    private var cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(object : CacheLoader<String, List<RealizedBet>>() {
                override fun load(key: String): List<RealizedBet> {
                    return loadFromDatabase(key)
                }
            })

    fun load(interval: String, useCache: Boolean): List<RealizedBet> {
        val (value, duration) = measureTimedValue {
            if (useCache) {
                cache.getUnchecked(interval)
            } else {
                loadFromDatabase(interval)
            }
        }
        return value
    }

    private fun loadFromDatabase(interval: String): List<RealizedBet> {
        val (from, to) = IntervalParser.parse(Instant.now(), interval)
        val (settledBets, sDur) = measureTimedValue { settledBetRepository.find(from = from, to = to) }
        log.info { "settle bets '$interval' loaded  in '$sDur'" }
        val (bets, rDur) = measureTimedValue { settledBets.map { realizedBetLoader.toRealizedBet(it) } }
        log.info { "realized bets '$interval' loaded in '${rDur}'" }
        return bets
    }

    fun invalidateCache() {
        cache.invalidateAll()
    }

}
