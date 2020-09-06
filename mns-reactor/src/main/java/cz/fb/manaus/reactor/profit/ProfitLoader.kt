package cz.fb.manaus.reactor.profit

import cz.fb.manaus.core.batch.SettledBetLoader
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.reactor.profit.progress.FixedBinFunctionProfitService
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.logging.Logger
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


@Component
@ExperimentalTime
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
        val (records, duration) = measureTimedValue {
            profitService.getProfitRecords(
                    settledBets,
                    projection,
                    false
            )
        }
        log.info { "profit records computed in '${duration}' seconds" }
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
        val (records, duration) = measureTimedValue {
            fixedBinFunctionProfitService.getProfitRecords(
                    settledBets,
                    function,
                    binCount,
                    projection
            )
        }
        log.info { "profit records computed in '${duration}' seconds" }
        return records
    }

}