package cz.fb.manaus.rest

import cz.fb.manaus.core.batch.SettledBetSaver
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.core.time.IntervalParser
import cz.fb.manaus.spring.ManausProfiles
import io.micrometer.core.instrument.Metrics
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.time.Instant


@Controller
@Profile(ManausProfiles.DB)
class SettledBetController(
    private val settledBetRepository: SettledBetRepository,
    private val betSaver: SettledBetSaver
) {

    @ResponseBody
    @RequestMapping(value = ["/bets"], method = [RequestMethod.GET])
    fun getSettledBets(@RequestParam(defaultValue = "20") maxResults: Int): List<SettledBet> {
        val bets = settledBetRepository.find(maxResults = maxResults, asc = false)
        return bets.reversed()
    }

    @ResponseBody
    @RequestMapping(value = ["/bets/" + IntervalParser.INTERVAL], method = [RequestMethod.GET])
    fun getSettledBets(@PathVariable interval: String): List<SettledBet> {
        val (from, to) = IntervalParser.parse(Instant.now(), interval)
        val settledBets = settledBetRepository.find(from = from, to = to, asc = false)
        return settledBets.reversed()
    }

    @RequestMapping(value = ["/bets"], method = [RequestMethod.POST])
    fun addBet(builder: UriComponentsBuilder, @RequestBody bet: SettledBet): ResponseEntity<*> {
        Metrics.counter("mns_settled_bet_post").increment()
        return if (betSaver.saveBet(bet)) {
            val uriComponents = builder.path("/bets/{id}").buildAndExpand(bet.id)
            ResponseEntity.created(uriComponents.toUri()).build<Any>()
        } else {
            ResponseEntity.noContent().build<Any>()
        }
    }
}
