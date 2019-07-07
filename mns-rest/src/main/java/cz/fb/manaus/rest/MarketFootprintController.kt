package cz.fb.manaus.rest

import cz.fb.manaus.core.model.MarketFootprint
import cz.fb.manaus.core.repository.Importer
import cz.fb.manaus.core.repository.MarketFootprintLoader
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Controller
@Profile(ManausProfiles.DB)
class MarketFootprintController(
        private val marketRepository: MarketRepository,
        private val importer: Importer,
        private val marketFootprintLoader: MarketFootprintLoader) {


    @ResponseBody
    @RequestMapping(value = ["/footprints/{id}"], method = [RequestMethod.GET])
    fun getFootprint(@PathVariable id: String): MarketFootprint {
        val market = marketRepository.read(id)!!
        return marketFootprintLoader.toFootprint(market)
    }

    @ResponseBody
    @RequestMapping(value = ["/footprints"], method = [RequestMethod.POST])
    fun import(@RequestBody footprint: MarketFootprint): ResponseEntity<*> {
        importer.import(footprint)
        val location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(footprint.market.id).toUri()
        return ResponseEntity.created(location).build<Any>()
    }

}
