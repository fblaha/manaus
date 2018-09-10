package cz.fb.manaus.rest

import com.google.common.base.Stopwatch
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import cz.fb.manaus.core.dao.BetActionDao
import cz.fb.manaus.core.dao.SettledBetDao
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import java.util.Optional.empty
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class SettledBetLoader {
    @Autowired
    private lateinit var intervalParser: IntervalParser
    @Autowired
    private lateinit var settledBetDao: SettledBetDao
    @Autowired
    private lateinit var betActionDao: BetActionDao
    private var cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(object : CacheLoader<String, List<SettledBet>>() {
                override fun load(key: String): List<SettledBet> {
                    return loadFromDatabase(key)
                }
            })

    fun load(interval: String, useCache: Boolean): List<SettledBet> {
        return if (useCache) {
            cache.getUnchecked(interval)
        } else {
            loadFromDatabase(interval)
        }
    }

    private fun loadFromDatabase(interval: String): List<SettledBet> {
        val range = intervalParser.parse(Instant.now(), interval)
        val stopwatch = Stopwatch.createStarted()
        val settledBets = settledBetDao.getSettledBets(
                Optional.of(Date.from(range.lowerEndpoint())),
                Optional.of(Date.from(range.upperEndpoint())),
                empty<Side>(), OptionalInt.empty())
        var elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS)
        log.log(Level.INFO, "Settle bets loaded {0} in ''{1}'' seconds", elapsed)
        stopwatch.reset().start()
        betActionDao.fetchMarketPrices(settledBets.map { it.betAction }.stream())
        elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS)
        log.log(Level.INFO, "Market prices loaded in ''{1}'' seconds", elapsed)
        return settledBets
    }

    companion object {
        private val log = Logger.getLogger(SettledBetLoader::class.java.simpleName)
    }

}
