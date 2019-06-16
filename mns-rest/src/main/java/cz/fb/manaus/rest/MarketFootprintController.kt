package cz.fb.manaus.rest

import cz.fb.manaus.core.model.MarketFootprint
import cz.fb.manaus.core.repository.MarketFootprintLoader
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@Profile(ManausProfiles.DB)
class MarketFootprintController(
        private val marketRepository: MarketRepository,
        private val marketFootprintLoader: MarketFootprintLoader) {


    @ResponseBody
    @RequestMapping(value = ["/footprints/{id}"], method = [RequestMethod.GET])
    fun getFootprint(@PathVariable id: String): MarketFootprint {
        val market = marketRepository.read(id)!!
        return marketFootprintLoader.toFootprint(market)
    }
}