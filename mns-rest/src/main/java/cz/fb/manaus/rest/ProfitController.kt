package cz.fb.manaus.rest

import com.google.common.base.Splitter
import com.google.common.base.Stopwatch
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.reactor.betting.action.BetUtils
import cz.fb.manaus.reactor.profit.ProfitService
import cz.fb.manaus.reactor.profit.progress.CoverageFunctionProfitService
import cz.fb.manaus.reactor.profit.progress.ProgressProfitService
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

@Controller
@Profile(ManausProfiles.DB)
class ProfitController(private val profitService: ProfitService,
                       private val progressProfitService: ProgressProfitService,
                       private val coverageService: CoverageFunctionProfitService,
                       private val provider: ExchangeProvider,
                       private val betLoader: SettledBetLoader) {


    @ResponseBody
    @RequestMapping(value = ["/profit/" + IntervalParser.INTERVAL], method = [RequestMethod.GET])
    fun getProfitRecords(@PathVariable interval: String,
                         @RequestParam(required = false) filter: String?,
                         @RequestParam(required = false) sort: String?,
                         @RequestParam(required = false) projection: String?,
                         @RequestParam(required = false) charge: Double?,
                         @RequestParam(required = false) ceiling: Double?,
                         @RequestParam(defaultValue = "true") cache: Boolean): List<ProfitRecord> {
        var settledBets = loadBets(interval, cache)

        val ceil = ceiling ?: -1.0
        if (ceil > 0) {
            settledBets = settledBets.map { BetUtils.limitBetAmount(ceil, it) }
        }
        val stopwatch = Stopwatch.createStarted()
        var profitRecords = profitService.getProfitRecords(settledBets, projection,
                false, getChargeRate(charge))
        logTime(stopwatch, "Profit records computed")
        if (filter != null) {
            val filters = parseFilter(filter)
            profitRecords = profitRecords
                    .filter { filters.any { token -> token in it.category } }
        }
        if (sort != null) {
            profitRecords = profitRecords.sortedWith(COMPARATORS[sort]!!)
        }
        return profitRecords
    }

    @ResponseBody
    @RequestMapping(value = ["/fc-progress/" + IntervalParser.INTERVAL], method = [RequestMethod.GET])
    fun getProgressRecords(@PathVariable interval: String,
                           @RequestParam(defaultValue = "5") chunkCount: Int,
                           @RequestParam(required = false) function: String?,
                           @RequestParam(required = false) charge: Double?,
                           @RequestParam(required = false) projection: String?,
                           @RequestParam(defaultValue = "true") cache: Boolean): List<ProfitRecord> {
        val bets = loadBets(interval, cache)
        val stopwatch = Stopwatch.createStarted()
        val chargeRate = getChargeRate(charge)
        val records = progressProfitService.getProfitRecords(bets, function, chunkCount, chargeRate, projection)
        logTime(stopwatch, "Profit records computed")
        return records
    }

    @ResponseBody
    @RequestMapping(value = ["/fc-coverage/" + IntervalParser.INTERVAL], method = [RequestMethod.GET])
    fun getCoverageRecords(@PathVariable interval: String,
                           @RequestParam(required = false) function: String?,
                           @RequestParam(required = false) charge: Double?,
                           @RequestParam(required = false) projection: String?,
                           @RequestParam(defaultValue = "true") cache: Boolean): List<ProfitRecord> {
        val bets = loadBets(interval, cache)
        val stopwatch = Stopwatch.createStarted()
        val chargeRate = getChargeRate(charge)
        val records = coverageService.getProfitRecords(bets, function, chargeRate, projection)
        logTime(stopwatch, "Profit records computed")
        return records
    }

    private fun loadBets(@PathVariable interval: String,
                         @RequestParam(defaultValue = "true") cache: Boolean): List<RealizedBet> {
        val stopwatch = Stopwatch.createStarted()
        val bets = betLoader.load(interval, cache)
        logTime(stopwatch, "Bets fetched")
        return bets
    }

    private fun logTime(stopwatch: Stopwatch, messagePrefix: String) {
        val elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS)
        log.log(Level.INFO, "{0} in ''{1}'' seconds", arrayOf(messagePrefix, elapsed))
    }

    private fun getChargeRate(chargeRate: Double?): Double {
        return chargeRate ?: provider.chargeRate
    }

    private fun parseFilter(rawFilter: String): List<String> {
        return Splitter.on(',').splitToList(rawFilter)
    }

    companion object {
        val COMPARATORS: Map<String, Comparator<ProfitRecord>> = mapOf(
                "category" to compareBy { it.category },
                "betProfit" to compareBy { it.betProfit },
                "profit" to compareBy { it.profit })
        private val log = Logger.getLogger(ProfitController::class.java.simpleName)
    }
}
