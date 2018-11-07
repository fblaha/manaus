package cz.fb.manaus.rest

import com.google.common.base.Preconditions
import cz.fb.manaus.core.dao.MarketDao
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@Controller
@Profile(ManausProfiles.DB)
class MarketController(private val marketDao: MarketDao) {

    val markets: List<Market>
        @ResponseBody
        @RequestMapping(value = ["/markets"], method = [RequestMethod.GET])
        get() = marketDao.getMarkets(Optional.of(Date()), Optional.empty(), OptionalInt.empty())

    @ResponseBody
    @RequestMapping(value = ["/markets/{id}"], method = [RequestMethod.GET])
    fun getMarket(@PathVariable id: String): Market {
        return marketDao.get(id).get()
    }

    @ResponseBody
    @RequestMapping(value = ["/markets"], method = [RequestMethod.POST])
    internal fun addOrUpdateMarket(@RequestBody market: Market): ResponseEntity<*> {
        validateMarket(market)
        marketDao.saveOrUpdate(market)
        val location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(market.id).toUri()
        return ResponseEntity.created(location).build<Any>()
    }

    private fun validateMarket(market: Market) {
        Objects.requireNonNull(market.id, "id==null")
        Preconditions.checkArgument(!market.runners.isEmpty(), "runners is empty")
        Objects.requireNonNull(market.event.openDate, "openDate==null")
    }
}
