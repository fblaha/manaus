package cz.fb.manaus.rest

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.CategoryService
import cz.fb.manaus.core.dao.BetActionDao
import cz.fb.manaus.core.dao.SettledBetDao
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.settlement.SaveStatus
import cz.fb.manaus.core.settlement.SettledBetSaver
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*
import java.util.Optional.empty


@Controller
@Profile(ManausProfiles.DB)
class SettledBetController {

    @Autowired
    private lateinit var settledBetDao: SettledBetDao
    @Autowired
    private lateinit var betActionDao: BetActionDao
    @Autowired
    private lateinit var intervalParser: IntervalParser
    @Autowired
    private lateinit var categoryService: CategoryService
    @Autowired
    private lateinit var betSaver: SettledBetSaver
    @Autowired
    private lateinit var metricRegistry: MetricRegistry


    @ResponseBody
    @RequestMapping(value = ["/markets/{id}/bets"], method = [RequestMethod.GET])
    fun getSettledBets(@PathVariable id: String): List<SettledBet> {
        val settledBets = settledBetDao.getSettledBets(id, OptionalLong.empty(), empty<Side>())
        betActionDao.fetchMarketPrices(settledBets.stream().map { it.betAction })
        return settledBets
    }

    @ResponseBody
    @RequestMapping(value = ["/bets"], method = [RequestMethod.GET])
    fun getSettledBets(@RequestParam(defaultValue = "20") maxResults: Int): List<SettledBet> {
        val bets = settledBetDao.getSettledBets(empty(), Optional.empty(), empty<Side>(), OptionalInt.of(maxResults))
        betActionDao.fetchMarketPrices(bets.stream().map { it.betAction })
        return bets.reversed()
    }

    @ResponseBody
    @RequestMapping(value = ["/bets/" + IntervalParser.INTERVAL], method = [RequestMethod.GET])
    fun getSettledBets(@PathVariable interval: String,
                       @RequestParam(required = false) projection: String?): List<SettledBet> {
        val range = intervalParser.parse(Instant.now(), interval)
        val from = Date.from(range.lowerEndpoint())
        val to = Date.from(range.upperEndpoint())
        var settledBets = settledBetDao.getSettledBets(Optional.of(from), Optional.of(to), empty<Side>(),
                OptionalInt.empty())
        if (projection != null) {
            settledBets = categoryService.filterBets(settledBets, projection, BetCoverage.from(settledBets))
        }
        betActionDao.fetchMarketPrices(settledBets.stream().map { it.betAction })
        return settledBets.reversed()
    }

    @RequestMapping(value = ["/bets"], method = [RequestMethod.POST])
    fun addBet(@RequestParam betId: String, @RequestBody bet: SettledBet): ResponseEntity<*> {
        metricRegistry.counter("settled.bet.post").inc()
        return if (betSaver.saveBet(betId, bet) == SaveStatus.NO_ACTION) {
            ResponseEntity.noContent().build<Any>()
        } else {
            ResponseEntity.accepted().build<Any>()
        }
    }

}
