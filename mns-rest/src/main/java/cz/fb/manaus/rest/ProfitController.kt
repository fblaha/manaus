package cz.fb.manaus.rest

import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.time.IntervalParser
import cz.fb.manaus.reactor.profit.ProfitLoader
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import kotlin.time.ExperimentalTime

@Controller
@ExperimentalTime
@Profile(ManausProfiles.DB)
class ProfitController(
        private val profitLoader: ProfitLoader
) {

    private val comparators: Map<String, Comparator<ProfitRecord>> = mapOf(
            "category" to compareBy { it.category },
            "betProfit" to compareBy { it.betProfit },
            "profit" to compareBy { it.profit })

    @ResponseBody
    @RequestMapping(value = ["/profit/" + IntervalParser.INTERVAL], method = [RequestMethod.GET])
    fun getProfitRecords(
            @PathVariable interval: String,
            @RequestParam(required = false) filter: String?,
            @RequestParam(required = false) sort: String?,
            @RequestParam(required = false) projection: String?
    ): List<ProfitRecord> {
        var profitRecords = profitLoader.loadProfitRecords(interval, projection)
        if (filter != null) {
            val filters = parseFilter(filter)
            profitRecords = profitRecords.filter { filters.any { token -> token in it.category } }
        }
        if (sort != null) {
            profitRecords = profitRecords.sortedWith(comparators[sort] ?: error("no such comparator"))
        }
        return profitRecords
    }

    @ResponseBody
    @RequestMapping(value = ["/fc-progress/" + IntervalParser.INTERVAL], method = [RequestMethod.GET])
    fun getProgressRecords(
            @PathVariable interval: String,
            @RequestParam(defaultValue = "5") binCount: Int,
            @RequestParam(required = false) function: String?,
            @RequestParam(required = false) projection: String?
    ): List<ProfitRecord> {
        return profitLoader.loadFixedBinRecords(interval, binCount, function, projection)
    }

    private fun parseFilter(rawFilter: String): List<String> {
        return rawFilter.split(',')
    }
}
