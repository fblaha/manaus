package cz.fb.manaus.rest

import cz.fb.manaus.core.batch.BetLoader
import cz.fb.manaus.core.time.IntervalParser
import cz.fb.manaus.reactor.ml.BetFeatureService
import cz.fb.manaus.reactor.ml.BetFeatureVector
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import kotlin.time.ExperimentalTime

@Controller
@ExperimentalTime
@Profile(ManausProfiles.DB)
class MachineLearningController(
        private val betFeatureService: BetFeatureService,
        private val betLoader: BetLoader
) {

    @ResponseBody
    @RequestMapping(value = ["/ml/bet-features/" + IntervalParser.INTERVAL], method = [RequestMethod.GET])
    fun getBetFeatures(
            @PathVariable interval: String
    ): List<BetFeatureVector> {
        val bets = betLoader.load(interval)
        return bets.map { betFeatureService.toFeatureVector(it) }
    }

}
