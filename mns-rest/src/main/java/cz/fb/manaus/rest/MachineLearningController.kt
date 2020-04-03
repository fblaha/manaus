package cz.fb.manaus.rest

import cz.fb.manaus.reactor.ml.BetFeatureService
import cz.fb.manaus.reactor.ml.BetFeatureVector
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@Profile(ManausProfiles.DB)
class MachineLearningController(
        private val betFeatureService: BetFeatureService,
        private val betLoader: SettledBetLoader
) {

    @ResponseBody
    @RequestMapping(value = ["/ml/bet-features/" + IntervalParser.INTERVAL], method = [RequestMethod.GET])
    fun getBetFeatures(@PathVariable interval: String,
                       @RequestParam(defaultValue = "true") cache: Boolean): List<BetFeatureVector> {
        val bets = betLoader.load(interval, cache)
        return bets.map { betFeatureService.toFeatureVector(it) }
    }

}
