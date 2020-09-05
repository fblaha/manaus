package cz.fb.manaus.rest

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.spring.ManausProfiles
import io.micrometer.core.instrument.Metrics
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.time.Instant

@Controller
@Profile(ManausProfiles.DB)
class MarketController(private val marketRepository: MarketRepository) {

    val markets: List<Market>
        @ResponseBody
        @RequestMapping(value = ["/markets"], method = [RequestMethod.GET])
        get() = marketRepository.find(from = Instant.now())

    val marketIDs: List<String>
        @ResponseBody
        @RequestMapping(value = ["/market-ids"], method = [RequestMethod.GET])
        get() = marketRepository.findIDs()

    @ResponseBody
    @RequestMapping(value = ["/markets/{id}"], method = [RequestMethod.GET])
    fun getMarket(@PathVariable id: String): ResponseEntity<Market> {
        return handleNotFound(marketRepository.read(id))
    }

    @ResponseBody
    @RequestMapping(value = ["/markets"], method = [RequestMethod.POST])
    internal fun addOrUpdateMarket(@RequestBody market: Market): ResponseEntity<*> {
        validateMarket(market)
        Metrics.counter("mns_market_post", "eventType", market.eventType.name).increment()
        marketRepository.saveOrUpdate(market)
        val location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(market.id).toUri()
        return ResponseEntity.created(location).build<Any>()
    }

    private fun validateMarket(market: Market) {
        require(market.runners.isNotEmpty()) { "runners is empty" }
    }
}
