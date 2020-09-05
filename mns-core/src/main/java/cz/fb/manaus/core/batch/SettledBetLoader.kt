package cz.fb.manaus.core.batch

import com.google.common.base.Stopwatch
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

@Component
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
        val stopwatch = Stopwatch.createStarted()
        val result = if (useCache) {
            cache.getUnchecked(interval)
        } else {
            loadFromDatabase(interval)
        }
        val elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS)
        log.info { "bets fetched in '$elapsed' seconds" }
        return result
    }

    private fun loadFromDatabase(interval: String): List<RealizedBet> {
        val (from, to) = IntervalParser.parse(Instant.now(), interval)
        val stopwatch = Stopwatch.createStarted()
        val settledBets = settledBetRepository.find(from = from, to = to)
        var elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS)
        log.info { "settle bets loaded in '$elapsed' seconds" }
        stopwatch.reset().start()
        val realizedBets = settledBets.map { realizedBetLoader.toRealizedBet(it) }
        elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS)
        log.info { "realized bets loaded in '$elapsed' seconds" }
        return realizedBets
    }

    fun invalidateCache() {
        cache.invalidateAll()
    }

}
