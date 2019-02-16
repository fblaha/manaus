package cz.fb.manaus.rest

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.core.settlement.SettledBetSaver
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.Instant


@Controller
@Profile(ManausProfiles.DB)
class SettledBetController(private val settledBetRepository: SettledBetRepository,
                           private val betSaver: SettledBetSaver,
                           private val metricRegistry: MetricRegistry) {


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
    fun addBet(@RequestBody bet: SettledBet): ResponseEntity<*> {
        metricRegistry.counter("settled.bet.post").inc()
        return if (betSaver.saveBet(bet)) {
            ResponseEntity.noContent().build<Any>()
        } else {
            ResponseEntity.accepted().build<Any>()
        }
    }

}
