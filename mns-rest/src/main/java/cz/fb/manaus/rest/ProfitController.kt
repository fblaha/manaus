package cz.fb.manaus.rest

import com.google.common.base.Stopwatch
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.reactor.betting.action.BetUtils
import cz.fb.manaus.reactor.profit.ProfitService
import cz.fb.manaus.reactor.profit.progress.FixedBinFunctionProfitService
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

@Controller
@Profile(ManausProfiles.DB)
class ProfitController(private val profitService: ProfitService,
                       private val fixedBinFunctionProfitService: FixedBinFunctionProfitService,
                       private val provider: ExchangeProvider,
                       private val betLoader: SettledBetLoader) {

    private val log = Logger.getLogger(ProfitController::class.simpleName)

    @ResponseBody
    @RequestMapping(value = ["/profit/" + IntervalParser.INTERVAL], method = [RequestMethod.GET])
    fun getProfitRecords(@PathVariable interval: String,
                         @RequestParam(required = false) filter: String?,
                         @RequestParam(required = false) sort: String?,
                         @RequestParam(required = false) projection: String?,
                         @RequestParam(required = false) charge: Double?,
                         @RequestParam(required = false) ceiling: Double?,
                         @RequestParam(defaultValue = "true") cache: Boolean): List<ProfitRecord> {
        var settledBets = betLoader.load(interval, cache)

        val ceil = ceiling ?: -1.0
        if (ceil > 0) {
            settledBets = settledBets.map { BetUtils.limitBetAmount(ceil, it) }
        }
        val stopwatch = Stopwatch.createStarted()
        var profitRecords = profitService.getProfitRecords(settledBets, projection,
                false, getChargeRate(charge))
        logTime(stopwatch, "profit records computed")
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
                           @RequestParam(defaultValue = "5") binCount: Int,
                           @RequestParam(required = false) function: String?,
                           @RequestParam(required = false) charge: Double?,
                           @RequestParam(required = false) projection: String?,
                           @RequestParam(defaultValue = "true") cache: Boolean): List<ProfitRecord> {
        val bets = betLoader.load(interval, cache)
        val stopwatch = Stopwatch.createStarted()
        val chargeRate = getChargeRate(charge)
        val records = fixedBinFunctionProfitService.getProfitRecords(bets, function, binCount, chargeRate, projection)
        logTime(stopwatch, "profit records computed")
        return records
    }


    private fun logTime(stopwatch: Stopwatch, messagePrefix: String) {
        val elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS)
        log.info { "$messagePrefix in '$elapsed' seconds" }
    }

    private fun getChargeRate(chargeRate: Double?): Double {
        return chargeRate ?: provider.chargeRate
    }

    private fun parseFilter(rawFilter: String): List<String> {
        return rawFilter.split(',')
    }

    companion object {
        val COMPARATORS: Map<String, Comparator<ProfitRecord>> = mapOf(
                "category" to compareBy { it.category },
                "betProfit" to compareBy { it.betProfit },
                "profit" to compareBy { it.profit })
    }
}
