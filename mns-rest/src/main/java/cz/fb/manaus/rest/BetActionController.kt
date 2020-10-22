package cz.fb.manaus.rest

import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.spring.ManausProfiles
import io.micrometer.core.instrument.Metrics
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@Profile(ManausProfiles.DB)
class BetActionController(
        private val betActionRepository: BetActionRepository
) {

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

    @RequestMapping(value = ["/actions/{id}/ack"], method = [RequestMethod.PUT])
    fun acknowledge(
            @PathVariable id: String,
            @RequestBody betId: String
    ): ResponseEntity<*> {
        val updated = betActionRepository.setBetId(id, sanitizeId(betId))
        Metrics.counter("mns_action_ack").increment()
        return if (updated) {
            ResponseEntity.ok().build<Any>()
        } else {
            Metrics.counter("mns_action_ack_notFound").increment()
            ResponseEntity.notFound().build<Any>()
        }
    }

    private fun sanitizeId(betId: String): String {
        return betId.trim('"')
    }
}
