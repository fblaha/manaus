package cz.fb.manaus.reactor.profit

import com.google.common.base.Stopwatch
import cz.fb.manaus.core.batch.SettledBetLoader
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.reactor.profit.progress.FixedBinFunctionProfitService
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import java.util.logging.Logger


@Component
@Profile(ManausProfiles.DB)
class ProfitLoader(
    private val betLoader: SettledBetLoader,
    private val profitService: ProfitService,
    private val fixedBinFunctionProfitService: FixedBinFunctionProfitService
) {

    private val log = Logger.getLogger(ProfitLoader::class.simpleName)

    fun loadProfitRecords(
        interval: String,
        cache: Boolean,
        projection: String? = null
    ): List<ProfitRecord> {
        val settledBets = betLoader.load(interval, cache)
        val stopwatch = Stopwatch.createStarted()
        val records = profitService.getProfitRecords(
            settledBets,
            projection,
            false
        )
        logTime(stopwatch, "profit records computed")
        return records
    }

    fun loadFixedBinRecords(
        interval: String,
        binCount: Int,
        function: String?,
        projection: String?,
        cache: Boolean
    ): List<ProfitRecord> {
        val settledBets = betLoader.load(interval, cache)
        val stopwatch = Stopwatch.createStarted()
        val records = fixedBinFunctionProfitService.getProfitRecords(
            settledBets,
            function,
            binCount,
            projection
        )
        logTime(stopwatch, "profit records computed")
        return records
    }

    private fun logTime(stopwatch: Stopwatch, taskName: String) {
        val elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS)
        log.info { "$taskName in '$elapsed' seconds" }
    }

}