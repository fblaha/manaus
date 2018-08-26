package cz.fb.manaus.rest

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.dao.BetActionDao
import cz.fb.manaus.core.dao.MarketDao
import cz.fb.manaus.core.dao.MarketPricesDao
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.action.ActionSaver
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*
import java.util.Optional.empty

@Controller
@Profile(ManausProfiles.DB)
class BetActionController {

    @Autowired
    private lateinit var betActionDao: BetActionDao
    @Autowired
    private lateinit var marketDao: MarketDao
    @Autowired
    private lateinit var marketPricesDao: MarketPricesDao
    @Autowired
    private lateinit var actionSaver: ActionSaver
    @Autowired
    private lateinit var metricRegistry: MetricRegistry

    @ResponseBody
    @RequestMapping(value = ["/markets/{id}/actions"], method = [RequestMethod.GET])
    fun getBetActions(@PathVariable id: String): List<BetAction> {
        val actions = betActionDao.getBetActions(id, OptionalLong.empty(), empty<Side>())
        betActionDao.fetchMarketPrices(actions.stream())
        return actions
    }

    @ResponseBody
    @RequestMapping(value = ["/actions"], method = [RequestMethod.GET])
    fun getBetActions(@RequestParam(defaultValue = "20") maxResults: Int): List<BetAction> {
        val actions = betActionDao.getBetActions(OptionalInt.of(maxResults))
        betActionDao.fetchMarketPrices(actions.stream())
        return actions.reversed()
    }

    @RequestMapping(value = ["/markets/{id}/actions"], method = [RequestMethod.POST])
    fun addAction(@PathVariable id: String,
                  @RequestParam priceId: Int,
                  @RequestBody action: BetAction): ResponseEntity<*> {
        val market = marketDao.get(id)
                .orElseThrow { IllegalArgumentException("no such market") }
        action.market = market
        betActionDao.saveOrUpdate(action)
        marketPricesDao.get(priceId).ifPresent { action.marketPrices = it }
        betActionDao.saveOrUpdate(action)
        val location = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri()
        return ResponseEntity.created(location).build<Any>()
    }

    @RequestMapping(value = ["/actions/{id}/betId"], method = [RequestMethod.PUT])
    fun setBetId(@PathVariable id: Int,
                 @RequestBody betId: String): ResponseEntity<*> {
        val changedRows = actionSaver.setBetId(betId, id)
        metricRegistry.counter("action.betId.put").inc()
        if (changedRows > 0) {
            return ResponseEntity.ok().build<Any>()
        } else {
            metricRegistry.counter("action.betId.notFound").inc()
            return ResponseEntity.notFound().build<Any>()
        }
    }
}
