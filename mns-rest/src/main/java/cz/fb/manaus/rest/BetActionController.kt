package cz.fb.manaus.rest

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@Profile(ManausProfiles.DB)
class BetActionController(private val betActionRepository: BetActionRepository,
                          private val marketRepository: MarketRepository,
                          private val metricRegistry: MetricRegistry) {


    @ResponseBody
    @RequestMapping(value = ["/markets/{id}/actions"], method = [RequestMethod.GET])
    fun getBetActions(@PathVariable id: String): List<BetAction> {
        return betActionRepository.find(id)
    }

    @ResponseBody
    @RequestMapping(value = ["/actions"], method = [RequestMethod.GET])
    fun getBetActions(@RequestParam(defaultValue = "20") maxResults: Int): List<BetAction> {
        return betActionRepository.findRecentBetActions(maxResults).reversed()
    }

    @RequestMapping(value = ["/actions/{id}/betId"], method = [RequestMethod.PUT])
    fun setBetId(@PathVariable id: Long,
                 @RequestBody betID: String): ResponseEntity<*> {
        val changedRows = betActionRepository.setBetID(id, betID)
        metricRegistry.counter("action.betId.put").inc()
        return if (changedRows > 0) {
            ResponseEntity.ok().build<Any>()
        } else {
            metricRegistry.counter("action.betId.notFound").inc()
            ResponseEntity.notFound().build<Any>()
        }
    }
}
