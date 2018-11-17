package cz.fb.manaus.rest

import com.google.common.base.Stopwatch
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.repository.RealizedBetLoader
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class SettledBetLoader(private val intervalParser: IntervalParser,
                       private val settledBetRepository: SettledBetRepository,
                       private val realizedBetLoader: RealizedBetLoader) {

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
        return if (useCache) {
            cache.getUnchecked(interval)
        } else {
            loadFromDatabase(interval)
        }
    }

    private fun loadFromDatabase(interval: String): List<RealizedBet> {
        val range = intervalParser.parse(Instant.now(), interval)
        val stopwatch = Stopwatch.createStarted()
        val settledBets = settledBetRepository.find(from = range.lowerEndpoint(), to = range.upperEndpoint())
        var elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS)
        log.log(Level.INFO, "Settle bets loaded in ''{0}'' seconds", elapsed)
        stopwatch.reset().start()
        val realizedBets = settledBets.map { realizedBetLoader.toRealizedBet(it) }
        elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS)
        log.log(Level.INFO, "Realized bets loaded in ''{0}'' seconds", elapsed)
        return realizedBets
    }

    companion object {
        private val log = Logger.getLogger(SettledBetLoader::class.java.simpleName)
    }

}
